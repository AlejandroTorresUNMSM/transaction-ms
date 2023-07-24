package com.atorres.nttdata.transactionms.model.creditms;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
public class CreditDto {
    @Id
    private String id;
    private BigDecimal balance;
    private BigDecimal debt;
}
