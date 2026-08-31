package com.payflow.payment.mapper;

import org.springframework.stereotype.Component;

import com.payflow.payment.dto.response.PaymentResponse;
import com.payflow.payment.model.Transaction;

@Component
public class PaymentMapper {

	public PaymentResponse mapToResponse(Transaction transaction, String message) {
	    return PaymentResponse.builder()
	        .transactionId(transaction.getId())
	        .status(transaction.getStatus())
	        .amount(transaction.getAmount())
	        .currency(transaction.getCurrency())
	        .merchantId(transaction.getMerchantId())
	        .customerEmail(transaction.getCustomerEmail())
	        .description(transaction.getDescription())
	        .createdAt(transaction.getCreatedAt())
	        .updatedAt(transaction.getUpdatedAt())
	        .message(message)
	        .build();
	}
}
