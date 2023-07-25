package com.atorres.nttdata.transactionms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PagoDebit extends RequestT {
	private String debit;
	private String to;
}
