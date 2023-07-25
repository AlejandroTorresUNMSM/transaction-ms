package com.atorres.nttdata.transactionms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableDiscoveryClient
@EnableReactiveFeignClients
@SpringBootApplication
public class TransactionMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionMsApplication.class, args);
	}

}
