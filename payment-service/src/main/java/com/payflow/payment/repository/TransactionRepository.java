package com.payflow.payment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payflow.payment.enums.PaymentStatus;
import com.payflow.payment.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>{
	
	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
	
	List<Transaction> findByMerchantId(UUID merchantId);
	
	List<Transaction> findByMerchantIdAndStatus(UUID merchantId, PaymentStatus status);
	
	boolean existsByIdempotencyKey(String idempotencyKey);
	

}
