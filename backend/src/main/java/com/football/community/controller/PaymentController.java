package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "支付回调", description = "支付创建和回调接口")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "创建支付", description = "为指定订单创建支付方式")
    @Parameters({
            @Parameter(name = "orderId", description = "订单ID", required = true),
            @Parameter(name = "paymentType", description = "支付方式(wechat/alipay)", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(
            @RequestParam Long orderId,
            @RequestParam String paymentType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(paymentService.createPayment(orderId, paymentType));
    }

    @Operation(summary = "微信支付回调", description = "处理微信支付的异步通知回调")
    @Parameter(description = "回调参数")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "处理成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody Map<String, String> params) {
        paymentService.handlePaymentCallback("wechat", params);
        return "success";
    }

    @Operation(summary = "支付宝回调", description = "处理支付宝支付的异步通知回调")
    @Parameter(description = "回调参数")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "处理成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        paymentService.handlePaymentCallback("alipay", params);
        return "success";
    }
}
