package com.atorres.nttdata.transactionms.client;

import com.atorres.nttdata.transactionms.model.debitms.DebitDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@ReactiveFeignClient(value = "debit-ms", url = "${debit.ms.url}/")
public interface FeignApiDebit {
	@GetMapping(value = "{debitId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<DebitDto> getDebit(@PathVariable String debitId);

	@GetMapping(value = "main-balance/{debitId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Mono<BigDecimal> getMainProduct(@PathVariable String debitId);

	@GetMapping(value = "all-balance/{debitId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Mono<BigDecimal> getAllBalance(@PathVariable String debitId);
}
