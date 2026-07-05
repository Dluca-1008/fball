package com.football.community.service;

import java.util.Map;

/**
 * 支付服务接口。
 * <p>提供支付创建、验证和回调处理等业务逻辑。</p>
 */
public interface PaymentService {

    /**
     * 创建支付。
     * @param orderId 订单ID
     * @param paymentType 支付方式（wechat/alipay）
     * @return 支付参数映射
     */
    Map<String, Object> createPayment(Long orderId, String paymentType);

    /**
     * 验证支付结果。
     * @param paymentType 支付方式
     * @param params 回调参数
     * @return true-验证成功, false-验证失败
     */
    boolean verifyPayment(String paymentType, Map<String, String> params);

    /**
     * 处理支付回调。
     * @param paymentType 支付方式
     * @param params 回调参数
     */
    void handlePaymentCallback(String paymentType, Map<String, String> params);
}
