package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.Payment;
import com.travel.entity.User;
import com.travel.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(
            Authentication authentication,
            @RequestBody Map<String, String> params) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return Result.error(401, "请先登录");
        }
        User user = (User) authentication.getPrincipal();
        String orderNo = params.get("orderNo");
        String payChannel = params.getOrDefault("payChannel", "wechat");

        Map<String, Object> result = paymentService.createPayment(orderNo, payChannel);
        return Result.ok(result);
    }

    /**
     * 获取支付状态
     */
    @GetMapping("/status/{orderNo}")
    public Result<Payment> getPaymentStatus(@PathVariable String orderNo) {
        Payment payment = paymentService.getByOrderNo(orderNo);
        return Result.ok(payment);
    }

    /**
     * 支付回调（模拟）
     */
    @PostMapping("/{paymentNo}/callback")
    public Result<Void> callback(
            @PathVariable String paymentNo,
            @RequestParam(required = false) String transactionId,
            @RequestBody(required = false) Map<String, String> params) {
        paymentService.handleCallback(paymentNo, transactionId, params);
        return Result.ok();
    }

    /**
     * 取消支付
     */
    @DeleteMapping("/{paymentNo}")
    public Result<Void> cancelPayment(@PathVariable String paymentNo) {
        paymentService.removeById(
            paymentService.getByOrderNo(paymentNo) != null
                ? paymentService.getByOrderNo(paymentNo).getId() : null
        );
        return Result.ok();
    }
}
