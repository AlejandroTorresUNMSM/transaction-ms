package com.atorres.nttdata.transactionms.client;

import com.atorres.nttdata.transactionms.model.clientms.ClientDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

@ReactiveFeignClient(value = "client-ms", url = "${client.ms.url}/")
public interface FeignApiClient {
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<ClientDto> getAllClients();

	@GetMapping(value = "{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<ClientDto> getClient(@PathVariable String id);

}
