package com.atorres.nttdata.transactionms.model;

import com.atorres.nttdata.transactionms.model.accountms.AccountDto;
import com.atorres.nttdata.transactionms.model.clientms.ClientDto;
import com.atorres.nttdata.transactionms.model.creditms.CreditDto;
import com.atorres.nttdata.transactionms.model.debitms.DebitDto;
import lombok.Data;

import java.util.List;

@Data
public class ReportClient {
	private String clientId;
	private ClientDto clientDto;
	private List<CreditDto> creditDtoList;
	private List<AccountDto> accountDtoList;
	private List<DebitDto> debitDtoList;
}
