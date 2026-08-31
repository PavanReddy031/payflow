package com.payflow.payment.enums;

public enum PaymentStatus {
	INITIATED, PROCESSING, SUCCESS, FAILED, TIMEOUT;
	
	public boolean canTransitionTo(PaymentStatus next) {
		return switch (this) {
			case  INITIATED -> next == PROCESSING;
			case PROCESSING -> next == SUCCESS
							|| next == FAILED
							|| next == TIMEOUT;
			
			default -> false;
		};
	}
}
