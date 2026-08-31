package com.payflow.payment.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEvenPublisher {

	private final KafkaTemplate<String, PaymentInitiatedEvent> kafkaTemplate;
	
	private static final String PAYMENT_INITIATED_TOPIC = "payment.initiated";
	
	public void publishPaymentInitiated(PaymentInitiatedEvent event) {
		
		kafkaTemplate.send(
				PAYMENT_INITIATED_TOPIC,
				event.getMerchantId().toString(),
				event
				);
		log.debug("Published payment.initiated event for transaction: {}", event.getTransactionId());
		
	}
	
}
