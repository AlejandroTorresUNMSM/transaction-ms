package com.atorres.nttdata.transactionms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionAccount extends RequestT {
	private String from;
	private String to;
}
