package com.atorres.nttdata.transactionms.client;

import com.atorres.nttdata.transactionms.model.accountms.AccountDto;
import com.atorres.nttdata.transactionms.model.accountms.RequestUpdateAccount;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

@ReactiveFeignClient(value = "product-pasive-ms", url = "${prodpasive.ms.url}")
public interface FeignApiProdPasive {
	@GetMapping(value = "{productId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<AccountDto> getAccount(@PathVariable String productId);

	@GetMapping(value = "client/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<AccountDto> getAllAccountClient(@PathVariable String id);

	@PutMapping(value="update",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<AccountDto> updateAccount(@RequestBody RequestUpdateAccount request);
}
