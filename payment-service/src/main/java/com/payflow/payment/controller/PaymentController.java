package com.payflow.payment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.payment.dto.request.PaymentInitiateRequest;
import com.payflow.payment.dto.response.PaymentResponse;
import com.payflow.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid PaymentInitiateRequest request) {
        PaymentResponse response = paymentService.initiatePayment(idempotencyKey, request);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable UUID transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentById(transactionId));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMerchant(
            @RequestParam UUID merchantId) {
        return ResponseEntity.ok(paymentService.getPaymentByMerchantId(merchantId));
    }

    @GetMapping("/{transactionId}/status")
    public ResponseEntity<PaymentResponse> getPaymentStatus(
            @PathVariable UUID transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(transactionId));
    }
}