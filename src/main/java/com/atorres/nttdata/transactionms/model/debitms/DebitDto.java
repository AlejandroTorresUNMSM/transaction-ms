package com.atorres.nttdata.transactionms.model.debitms;

import lombok.Data;

import java.util.List;

@Data
public class DebitDto {
	private String id;
	private String client;
	private String mainProduct;
	private List<String> productList;
}
