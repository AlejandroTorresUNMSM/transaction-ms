package com.atorres.nttdata.transactionms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ResponseAvgAmount {
	private String clientId;
	private BigDecimal avgAmount;
}
