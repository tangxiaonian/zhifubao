package com.example.demo_springboot.service;

import com.alipay.api.AlipayApiException;
import com.example.demo_springboot.domain.ResultMap;

import javax.servlet.http.HttpServletRequest;

public interface AlipayPcService {

    /**
     * 跳转到支付宝支付页面
     * @param orderNo  订单号
     * @param subject  商品名称
     * @param amount   钱数
     * @param body    内容描述(可不传)
     * @return
     * @throws AlipayApiException
     */
    String goPayPage(String orderNo,String subject, String amount, String body) throws AlipayApiException;

    /**
     * 交易查询接口
     * @param orderNo 订单号
     * @return
     */
    String queryOrder(String orderNo);

    /**
     * @Description:
     * @param tradeStatus: 支付宝交易状态
     * @param orderNo: 订单编号
     * @param tradeNo: 支付宝订单号
     */
    boolean notify(String tradeStatus, String orderNo, String tradeNo);

    /**
     * @Description: 校验签名
     * @param request
     */
    boolean rsaCheckV1(HttpServletRequest request);

    /**
     * @Description: 退款
     * @param orderNo: 订单编号
     * @param amount: 实际支付金额
     * @param refundReason: 退款原因
     */
    ResultMap refund(String orderNo, double amount, String refundReason);
}
