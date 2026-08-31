package com.payflow.payment.dto.request;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentInitiateRequest {

	@NotNull(message = "merchant id is required")
	private UUID merchantId;
	
	@NotNull(message = "amount cannot be null")
	@Min(value = 1, message = "amount value should be greater than zero")
	private Long amount;
	
	@NotNull(message = "currency is required")
	private String currency;
	
	private String customerEmail;
	
	private String customerCellNumber;
	
	private String description;
	
	private Map<String, Object> metadata;
	
}
