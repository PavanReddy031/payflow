package com.payflow.payment.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.payflow.payment.enums.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {

	private UUID merchantId;
	private UUID transactionId;
	private PaymentStatus status;
	private String customerEmail;
	private String customerCellNumber;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String message;
	private String description;
	private Long amount;
	private String currency;
	
}
