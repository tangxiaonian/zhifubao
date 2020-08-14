package com.example.demo_springboot.controller.pay;

import com.example.demo_springboot.domain.ResultMap;
import com.example.demo_springboot.service.AlipayAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/alipay/app")
public class AlipayAppController {

    @Autowired
    private AlipayAppService alipayService;


    /**
     * 创建订单
     */
    @GetMapping("/createOrder")
    public ResultMap createOrder( @RequestParam String orderNo,
                                  @RequestParam double amount,
                                  @RequestParam String body) {
        try {
            // 1、验证订单是否存在

            // 2、创建支付宝订单
            String orderStr = alipayService.createOrder(orderNo, amount, body);
            return ResultMap.ok().put("data", orderStr);
        } catch (Exception e) {
            return ResultMap.error("订单生成失败");
        }
    }

    /**
     * 支付异步通知
     * 接收到异步通知并验签通过后，一定要检查通知内容，
     * 包括通知中的app_id、out_trade_no、total_amount是否与请求中的一致，并根据trade_status进行后续业务处理。
     * https://docs.open.alipay.com/194/103296
     */
    @RequestMapping("/notify")
    public String notify(HttpServletRequest request) {
        // 验证签名
        boolean flag = alipayService.rsaCheckV1(request);
        if (flag) {
            String tradeStatus = request.getParameter("trade_status"); // 交易状态
            String outTradeNo = request.getParameter("out_trade_no"); // 商户订单号
            String tradeNo = request.getParameter("trade_no"); // 支付宝订单号
            /**
             * 还可以从request中获取更多有用的参数，自己尝试
             */
            boolean notify = alipayService.notify(tradeStatus, outTradeNo, tradeNo);
            if(notify){
                return "success";
            }
        }
        return "fail";
    }
}
