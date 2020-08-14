package com.example.demo_springboot.controller.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.demo_springboot.config.AlipayPcConfig;
import com.example.demo_springboot.service.AlipayPcService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/zhifu")
public class AlipayPcController {

    @Resource
    public AlipayPcService alipayPcService;

    @Resource
    public AlipayPcConfig alipayPcConfig;

    @GetMapping("/go/pay")
    public void goPayPage(@RequestParam String orderNo, @RequestParam String subject,
                          @RequestParam String amount, @RequestParam(required = false) String body,
                          HttpServletResponse response) {
        try {
            String form;
            form = alipayPcService.goPayPage(orderNo, subject, amount, body);
            response.setContentType("text/html;charset=" + alipayPcConfig.getCharset());
            PrintWriter writer = response.getWriter();
            //直接将完整的表单html输出到页面
            writer.write(form);
            writer.flush();
            writer.close();
        } catch (AlipayApiException | IOException exception) {
            exception.printStackTrace();
        }
    }

    @GetMapping("/notify_url")
    public String notifyUrl() {
        System.out.println("notifyUrl....支付成功的通知...");
        return "";
    }

    @GetMapping("/return_url")
    public String returnUrl() {
        System.out.println("returnUrl....支付成功要跳转的url...");
        return "";
    }

}
