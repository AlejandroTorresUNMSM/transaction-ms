package com.atorres.nttdata.transactionms.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestTransactionAccount {
    private String accountId;
    private BigDecimal amount;
    private String clientId;
}
