package com.football.community.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private WechatPay wechat = new WechatPay();
    private Alipay alipay = new Alipay();

    @Data
    public static class WechatPay {
        private String appId;
        private String mchId;
        private String apiKey;
        private String certPath;
        private String notifyUrl;
    }

    @Data
    public static class Alipay {
        private String appId;
        private String privateKey;
        private String publicKey;
        private String notifyUrl;
        private String returnUrl;
    }
}
