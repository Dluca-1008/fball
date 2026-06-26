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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
            // 实际项目中需要生成预付单并返回小程序调起支付的参数
            result.put("message", "微信支付参数（需对接微信支付API）");
        } else if ("alipay".equals(paymentType)) {
            result.put("appId", paymentConfig.getAlipay().getAppId());
            // 实际项目中需要生成支付表单或链接
            result.put("message", "支付宝支付参数（需对接支付宝API）");
        }

        log.info("创建支付单: 订单号={}, 支付方式={}, 金额={}", order.getOrderNo(), paymentType, order.getTotalAmount());

        return result;
    }

    @Override
    public boolean verifyPayment(String paymentType, Map<String, String> params) {
        // 实际项目中需要验证支付签名
        // 这里简化处理，直接返回true
        log.info("验证支付回调: 支付方式={}, 参数={}", paymentType, params);
        return true;
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String paymentType, Map<String, String> params) {
        if (!verifyPayment(paymentType, params)) {
            log.error("支付验证失败: 支付方式={}, 参数={}", paymentType, params);
            return;
        }

        String orderNo = params.get("orderNo");
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
}
