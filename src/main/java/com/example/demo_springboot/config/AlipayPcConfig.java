package com.example.demo_springboot.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "pay.alipay.pc")
public class AlipayPcConfig {

    /**
     * 支付宝gatewayUrl
     */
    private String gatewayUrl;
    /**
     * 商户应用id
     */
    private String appid;
    /**
     * 应用RSA私钥，用于对商户请求报文加签，
     */
    private String appPrivateKey;
    /**
     * 支付宝RSA公钥，用于验签支付宝应答
     */
    private String alipayPublicKey;
    /**
     * 签名类型
     */
    private String signType = "RSA2";
    /**
     * 格式
     */
    private String formate = "json";
    /**
     * 编码
     */
    private String charset = "UTF-8";
    /**
     * 页面跳转同步通知页面地址
     */
    private String returnUrl;
    /**
     * 异步通知页面地址
     */
    private String notifyUrl;
    /**
     * 最大查询次数
     */
    private static int maxQueryRetry = 5;
    /**
     * 查询间隔（毫秒）
     */
    private static long queryDuration = 5000;
    /**
     * 最大撤销次数
     */
    private static int maxCancelRetry = 3;
    /**
     * 撤销间隔（毫秒）
     */
    private static long cancelDuration = 3000;

    @Bean(name = "alipayPcClient")
    public AlipayClient alipayPcClient(){
        return new DefaultAlipayClient(this.getGatewayUrl(),
                this.getAppid(),
                this.getAppPrivateKey(),
                this.getFormate(),
                this.getCharset(),
                this.getAlipayPublicKey(),
                this.getSignType());
    }
}
