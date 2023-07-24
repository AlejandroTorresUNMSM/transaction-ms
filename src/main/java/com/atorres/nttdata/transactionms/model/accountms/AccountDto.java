package com.atorres.nttdata.transactionms.model.accountms;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class AccountDto {
	@Id
	private String id;
	private AccountType type;
	private BigDecimal balance;
	private AccountCategory accountCategory;
	private String client;
}
