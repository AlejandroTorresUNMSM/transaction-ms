package com.atorres.nttdata.transactionms.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestRetiroDebit {
	private String debit;
	private BigDecimal amount;
	private String clientId;
}
