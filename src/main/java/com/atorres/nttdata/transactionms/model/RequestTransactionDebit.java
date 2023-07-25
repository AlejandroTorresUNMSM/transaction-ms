package com.atorres.nttdata.transactionms.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestTransactionDebit {
	private String debit;
	private BigDecimal amount;
}
