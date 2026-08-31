package com.payflow.payment.service;

import java.util.List;
import java.util.UUID;

import com.payflow.payment.dto.request.PaymentInitiateRequest;
import com.payflow.payment.dto.response.PaymentResponse;

public interface PaymentService {
	
	PaymentResponse initiatePayment(String idempotencyKey, PaymentInitiateRequest request);
	
	PaymentResponse getPaymentById(UUID transactionId);
	
	List<PaymentResponse> getPaymentByMerchantId(UUID merchantId);
	
	PaymentResponse getPaymentStatus(UUID transactionId);
}
