package com.newpecunia.creditcard.impl;

import java.math.BigDecimal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.newpecunia.creditcard.CreditCardAcquiringService;

@Singleton
public class CreditCardAcquiringServiceImpl implements CreditCardAcquiringService {

	@Inject
	CreditCardAcquiringServiceImpl() {
	}

	@Override
	public String startCardPaymentTransaction(BigDecimal amount) {
		// TODO implement me
		return "123456789";
	}

	@Override
	public TransactionStatus getTransactionStatus(String txId) {
		// TODO implement me
		return null;
	}

	@Override
	public CancelStatus cancelTransaction(String txId) {
		// TODO implement me
		return null;
	}

	@Override
	public CloseDayStatus closeDay() {
		// TODO implement me
		return null;
	}
	
	
}
