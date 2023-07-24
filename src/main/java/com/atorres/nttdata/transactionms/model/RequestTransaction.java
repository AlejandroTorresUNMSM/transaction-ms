package com.atorres.nttdata.transactionms.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestTransaction {
	private String from;
	private String to;
	private BigDecimal amount;
	private String clientId;
}
