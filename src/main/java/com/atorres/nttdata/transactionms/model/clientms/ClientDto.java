package com.atorres.nttdata.transactionms.model.clientms;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ClientDto {
    @Id
    private String id;
    private String typeDocument;
    private String nroDocument;
    private String name;
    private ClientType typeClient;
}
