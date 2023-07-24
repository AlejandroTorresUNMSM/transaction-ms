package com.atorres.nttdata.transactionms.client;

import reactivefeign.spring.config.ReactiveFeignClient;

@ReactiveFeignClient(value = "debit-ms", url = "${debit.ms.url}/")
public interface FeignApiDebit {
}
