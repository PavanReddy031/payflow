package com.payflow.payment.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.payflow.payment.dto.request.PaymentInitiateRequest;
import com.payflow.payment.dto.response.PaymentResponse;
import com.payflow.payment.enums.PaymentStatus;
import com.payflow.payment.event.PaymentEvenPublisher;
import com.payflow.payment.event.PaymentInitiatedEvent;
import com.payflow.payment.mapper.PaymentMapper;
import com.payflow.payment.model.Transaction;
import com.payflow.payment.repository.TransactionRepository;
import com.payflow.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final RedisTemplate<String, String> redisTemplate;
	
	private final TransactionRepository transactionRepository;
	
	private final PaymentMapper paymentMapper;
	
	private final PaymentEvenPublisher paymentEventPublisher;
	
	private static final String IDEMPOTENCY_PREFIX = "idempotency:";
	private static final long IDEMPOTENCY_TTL_HOURS = 24;
	
	private String buildIdempotencyKey(UUID merchantId, String idempotencyKey) {
		return IDEMPOTENCY_PREFIX + merchantId + ":" + idempotencyKey;
	}
	
	private boolean isIdempotencyKeyPresent(String redisKey) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
	}
	
	private void saveIdempotencyKey(String redisKey, UUID transactionId) {
		redisTemplate.opsForValue().set(
				redisKey,
				transactionId.toString(),
				IDEMPOTENCY_TTL_HOURS,
				TimeUnit.HOURS
				);
	}

	@Override
	public PaymentResponse initiatePayment(String idempotencyKey, PaymentInitiateRequest request) {
		// first check if idempotency key already exists in the table if so simply return that response
		// if none found then validate merchantId
		// and then save a record with status as INITIATED
		// store idempotency key in redis
		// publish this event to kafka
		// return 202 response
		
		// check if idempotency key exists
		String redisKey = buildIdempotencyKey(request.getMerchantId(), idempotencyKey);
		boolean isTransactionExists = isIdempotencyKeyPresent(redisKey);
		
		if ( isTransactionExists ) {
			
			String existingTransactionId = redisTemplate.opsForValue().get(redisKey);
			Transaction existing = transactionRepository
									.findById(UUID.fromString(existingTransactionId))
									.orElseThrow();
			
			return paymentMapper.mapToResponse( existing, "Duplicate request — returning existing transaction" );
		}
		
		// to validate merchant id we need a merchant table, it is a simple find by merchant id query and 
		// check if he is present 
		// TO DO
		
		// save the new transaction to table
		Transaction transaction = new Transaction();
	    transaction.setMerchantId(request.getMerchantId());
	    transaction.setAmount(request.getAmount());
	    transaction.setCurrency(request.getCurrency());
	    transaction.setCustomerEmail(request.getCustomerEmail());
	    transaction.setCustomerCellNumber(request.getCustomerCellNumber());
	    transaction.setDescription(request.getDescription());
	    transaction.setStatus(PaymentStatus.INITIATED);
	    transaction.setIdempotencyKey(idempotencyKey);
		
	    // save to db
	    Transaction saved = transactionRepository.save(transaction);
	    
	    // save idempotency key to redis
	    saveIdempotencyKey(redisKey, saved.getId());
	    
	    
	    // Publish to KAFKA
	    PaymentInitiatedEvent event =  PaymentInitiatedEvent.builder()
	    									.transactionId(saved.getId())
	    									.merchantId(saved.getMerchantId())
	    									.amount(saved.getAmount())
	    									.currency(saved.getCurrency())
	    									.customerEmail(saved.getCustomerEmail())
	    									.customerCellNumber(saved.getCustomerCellNumber())
	    									.initiatedAt(saved.getCreatedAt())
	    									.build();
	    
	    paymentEventPublisher.publishPaymentInitiated(event);
		
	    // return response
		return paymentMapper.mapToResponse(saved, "Payment Initiated successfully");
	}

	@Override
	public PaymentResponse getPaymentById(UUID transactionId) {
		
		Transaction transaction = transactionRepository.findById(transactionId)
									.orElseThrow();
		
		PaymentResponse response = paymentMapper.mapToResponse(transaction, "Fetched payment successfully");
		
		return response;
	}

	@Override
	public List<PaymentResponse> getPaymentByMerchantId(UUID merchantId) {
		 
		List<Transaction> transactionList = transactionRepository.findByMerchantId(merchantId);
		
		return transactionList.stream()
					.map(t -> paymentMapper.mapToResponse(t, "Fetched Successfully"))
					.collect(Collectors.toList());
	}

	@Override
	public PaymentResponse getPaymentStatus(UUID transactionId) {
	    Transaction transaction = transactionRepository.findById(transactionId)
	        .orElseThrow();
	    // return only status — use a lighter response or same response
	    return paymentMapper.mapToResponse(transaction, null);
	}

}
