package com.payflow.payment.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitiatedEvent {

	private UUID transactionId;
	private UUID merchantId;
	private Long amount;
	private String currency;
	private String customerEmail;
	private String customerCellNumber;
	private LocalDateTime initiatedAt;
	
	
}
