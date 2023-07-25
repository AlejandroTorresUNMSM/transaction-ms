package com.atorres.nttdata.transactionms.controller;

import com.atorres.nttdata.transactionms.model.RequestTransaction;
import com.atorres.nttdata.transactionms.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transaction")
@Slf4j
public class TransactionController {
	/**.
	 * Servicio transacciones
	 */
	@Autowired
	TransactionService transactionService;

	/**.
	 * Metodo para hacer retiro desde un cajero
	 * @param request request
	 * @return transactionDao
	 */
	@PostMapping(value = "/account/retiro", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<TransactionDto> retiroCuenta(
					@RequestBody RequestTransactionAccount request) {
		return transactionService.retiroCuenta(request)
						.doOnSuccess(v -> log.info("Retiro de cajero exitoso"));
	}

	/**.
	 * Metodo para hacer deposito desde un cajero
	 * @param request request
	 * @return TransactionDao
	 */
	@PostMapping(value = "/account/deposito", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<TransactionDto> depositoCuenta(
					@RequestBody RequestTransactionAccount request) {
		return transactionService.depositoCuenta(request)
						.doOnSuccess(v -> log.info("Deposito de cajero exitoso"));
	}

	/**.
	 * Metodo para hacer transferencia entre mis cuentas
	 * @param request request
	 * @return TransactionDao
	 */
	@PostMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<TransactionDto> transferencia(@RequestBody RequestTransaction request) {
		return transactionService.postTransferencia(request)
						.doOnSuccess(v -> log.info("Transferencia entre tus cuentas exitosa"));
	}

	/**
	 * Metodo para hacer transferencias a tercerso
	 * @param request request
	 * @return transaction
	 */
	@PostMapping(value = "/terceros", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<TransactionDto> transferenciaTerceros(@RequestBody RequestTransaction request) {
		return transactionService.getTransferenciaTerceros(request)
						.doOnSuccess(v -> log.info("Transferencia a terceros exitosa"));
	}
}
