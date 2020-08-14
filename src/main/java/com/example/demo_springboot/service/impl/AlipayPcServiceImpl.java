package com.example.demo_springboot.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.dtflys.forest.utils.StringUtils;
import com.example.demo_springboot.config.AlipayPcConfig;
import com.example.demo_springboot.domain.ResultMap;
import com.example.demo_springboot.exception.QueryOrderException;
import com.example.demo_springboot.service.AlipayPcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class AlipayPcServiceImpl implements AlipayPcService {

    private final Logger logger = LoggerFactory.getLogger(AlipayPcServiceImpl.class);

    @Resource
    private AlipayClient alipayPcClient;

    @Resource
    private AlipayPcConfig alipayPcConfig;

    @Override
    public String goPayPage(String orderNo,String subject, String amount, String body) {
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayPcConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayPcConfig.getNotifyUrl());
        AlipayTradeAppPayModel payModel = new AlipayTradeAppPayModel();
        // 订单号
        payModel.setOutTradeNo(orderNo);
        // 钱数
        payModel.setTotalAmount(amount + "");
        // 名称
        payModel.setSubject(subject);
        // 描述
        payModel.setBody(body);
        payModel.setProductCode("FAST_INSTANT_TRADE_PAY");
        // 回调参数
        payModel.setPassbackParams("这个参数，会回调带回来!");
        alipayRequest.setBizModel(payModel);
        try {
            //调用SDK生成表单
            return alipayPcClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("跳转支付页面失败！");
    }

    @Override
    public String queryOrder(String orderNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
        // 商户订单号
        queryModel.setOutTradeNo(orderNo);
        request.setBizModel(queryModel);
        try {
            AlipayTradeQueryResponse queryResponse = alipayPcClient.execute(request);
            return queryResponse.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        throw new QueryOrderException("订单查询失败!");
    }

    @Override
    public boolean notify(String tradeStatus, String orderNo, String tradeNo) {
        if ("TRADE_FINISHED".equals(tradeStatus)
                || "TRADE_SUCCESS".equals(tradeStatus)) {
            // 支付成功，根据业务逻辑修改相应数据的状态
            // boolean state = orderPaymentService.updatePaymentState(orderNo, tradeNo);
//            if (state) {
                return true;
//            }
        }
        return false;
    }

    @Override
    public ResultMap refund(String orderNo, double amount, String refundReason) {
        if(StringUtils.isBlank(orderNo)){
            return ResultMap.error("订单编号不能为空");
        }
        if(amount <= 0){
            return ResultMap.error("退款金额必须大于0");
        }

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        // 商户订单号
        model.setOutTradeNo(orderNo);
        // 退款金额
        model.setRefundAmount(String.valueOf(amount));
        // 退款原因
        model.setRefundReason(refundReason);
        // 退款订单号(同一个订单可以分多次部分退款，当分多次时必传)
        // model.setOutRequestNo(UUID.randomUUID().toString());
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        alipayRequest.setBizModel(model);
        AlipayTradeRefundResponse alipayResponse = null;
        try {
            alipayResponse = alipayPcClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            logger.error("订单退款失败，异常原因:{}", e);
        }
        if(alipayResponse != null){
            String code = alipayResponse.getCode();
            String subCode = alipayResponse.getSubCode();
            String subMsg = alipayResponse.getSubMsg();
            if("10000".equals(code)
                    && StringUtils.isBlank(subCode)
                    && StringUtils.isBlank(subMsg)){
                // 表示退款申请接受成功，结果通过退款查询接口查询
                // 修改用户订单状态为退款
                return ResultMap.ok("订单退款成功");
            }
            return ResultMap.error(subCode + ":" + subMsg);
        }
        return ResultMap.error("订单退款失败");
    }

    @Override
    public boolean rsaCheckV1(HttpServletRequest request){
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            boolean verifyResult = AlipaySignature.rsaCheckV1(params, alipayPcConfig.getAlipayPublicKey(),
                    alipayPcConfig.getCharset(), alipayPcConfig.getSignType());
            return verifyResult;
        } catch (AlipayApiException e) {
            logger.debug("verify sigin error, exception is:{}", e);
            return false;
        }
    }
}
