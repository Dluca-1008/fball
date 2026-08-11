package com.football.community.service.impl;

import com.football.community.config.PaymentConfig;
import com.football.community.entity.Order;
import com.football.community.exception.BusinessException;
import com.football.community.service.OrderService;
import com.football.community.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private OrderService orderService;

    @Override
    public Map<String, Object> createPayment(Long orderId, String paymentType) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态异常");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderNo", order.getOrderNo());
        result.put("amount", order.getTotalAmount());
        result.put("paymentType", paymentType);

        String paymentNo = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        result.put("paymentNo", paymentNo);

        if ("wechat".equals(paymentType)) {
            result.put("appId", paymentConfig.getWechat().getAppId());
            result.put("mchId", paymentConfig.getWechat().getMchId());
            result.put("message", "微信支付参数（需对接微信支付API）");
        } else if ("alipay".equals(paymentType)) {
            result.put("appId", paymentConfig.getAlipay().getAppId());
            result.put("message", "支付宝支付参数（需对接支付宝API）");
        }

        log.info("创建支付单: 订单号={}, 支付方式={}, 金额={}", order.getOrderNo(), paymentType, order.getTotalAmount());

        return result;
    }

    @Override
    public boolean verifyPayment(String paymentType, Map<String, String> params) {
        if (params == null) {
            log.error("支付回调参数为空");
            return false;
        }

        // 1. Required fields check
        String orderNo = params.get("orderNo");
        String totalFee = params.get("totalFee");
        String transactionId = params.get("transactionId");
        if (orderNo == null || totalFee == null || transactionId == null) {
            log.error("回调参数缺少必填字段: orderNo={}, totalFee={}, transactionId={}", orderNo, totalFee, transactionId);
            return false;
        }

        // 2. Amount consistency (±0.01 tolerance)
        Order order = orderService.lambdaQuery().eq(Order::getOrderNo, orderNo).one();
        if (order == null) {
            log.error("订单不存在: orderNo={}", orderNo);
            return false;
        }
        BigDecimal callbackAmount = new BigDecimal(totalFee);
        if (callbackAmount.subtract(order.getTotalAmount()).abs().compareTo(new BigDecimal("0.01")) > 0) {
            log.warn("金额不一致: orderNo={}, callback={}, order={}", orderNo, callbackAmount, order.getTotalAmount());
            return false;
        }

        // 3. Idempotency check
        if (order.getStatus() == 1) {
            log.info("订单已支付，跳过重复回调: orderNo={}", orderNo);
            return true;
        }

        // 4. HMAC-SHA256 signature verification
        return verifySignature(paymentType, params);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String paymentType, Map<String, String> params) {
        // Idempotency guard
        String orderNo = params != null ? params.get("orderNo") : null;
        if (orderNo != null) {
            Order order = orderService.lambdaQuery().eq(Order::getOrderNo, orderNo).one();
            if (order != null && order.getStatus() == 1) {
                log.info("订单已支付，跳过重复回调: orderNo={}", orderNo);
                return;
            }
        }

        if (!verifyPayment(paymentType, params)) {
            log.error("支付验证失败: 支付方式={}, 参数={}", paymentType, params);
            return;
        }

        if (orderNo == null) {
            log.error("回调参数缺少orderNo");
            return;
        }

        Order order = orderService.lambdaQuery().eq(Order::getOrderNo, orderNo).one();
        if (order == null) {
            log.error("订单不存在: orderNo={}", orderNo);
            return;
        }

        if (order.getStatus() != 0) {
            log.warn("订单状态异常: orderNo={}, status={}", orderNo, order.getStatus());
            return;
        }

        order.setStatus(1);
        order.setPaymentTime(LocalDateTime.now());
        orderService.updateById(order);

        log.info("订单支付成功: orderNo={}", orderNo);
    }

    private boolean verifySignature(String paymentType, Map<String, String> params) {
        String signature = params.get("signature");
        if (signature == null) {
            log.error("回调参数缺少signature");
            return false;
        }

        String secret;
        if ("wechat".equals(paymentType)) {
            secret = paymentConfig.getWechat().getApiKey();
        } else if ("alipay".equals(paymentType)) {
            secret = paymentConfig.getAlipay().getPrivateKey();
        } else {
            log.error("不支持的支付方式: {}", paymentType);
            return false;
        }

        if (secret == null || secret.isEmpty()) {
            log.error("支付密钥未配置: paymentType={}", paymentType);
            return false;
        }

        // Build canonical query string (sort keys, exclude signature)
        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("signature");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.append("key=").append(secret);

        String expectedSig = hmacSha256(sb.toString(), secret);
        boolean valid = expectedSig.equalsIgnoreCase(signature);
        if (!valid) {
            log.warn("签名验证失败: expected={}, actual={}", expectedSig, signature);
        }
        return valid;
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] result = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : result) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            log.error("HMAC-SHA256计算失败", e);
            return "";
        }
    }
}
