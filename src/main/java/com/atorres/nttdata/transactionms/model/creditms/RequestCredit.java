package com.atorres.nttdata.transactionms.model.creditms;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestCredit {
    private BigDecimal balance;
}
