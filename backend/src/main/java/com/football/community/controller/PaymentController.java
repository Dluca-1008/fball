package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(
            @RequestParam Long orderId,
            @RequestParam String paymentType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(paymentService.createPayment(orderId, paymentType));
    }

    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody Map<String, String> params) {
        paymentService.handlePaymentCallback("wechat", params);
        return "success";
    }

    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        paymentService.handlePaymentCallback("alipay", params);
        return "success";
    }
}
