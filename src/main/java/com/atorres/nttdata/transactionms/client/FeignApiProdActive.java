package com.atorres.nttdata.transactionms.client;

import com.atorres.nttdata.transactionms.model.creditms.CreditDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

@ReactiveFeignClient(value = "product-active-ms", url = "${prodactive.ms.url}/")
public interface FeignApiProdActive {
	@GetMapping( value = "client/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<CreditDto> getAllCreditClient(@PathVariable String id);

	@GetMapping( value = "deudas-vencidas/{clientId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Boolean> getDeuda(@PathVariable String clientId);
}
