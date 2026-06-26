package com.football.community.service;

import java.util.Map;

public interface PaymentService {

    Map<String, Object> createPayment(Long orderId, String paymentType);

    boolean verifyPayment(String paymentType, Map<String, String> params);

    void handlePaymentCallback(String paymentType, Map<String, String> params);
}
