package com.atorres.nttdata.transactionms.controller;

import com.atorres.nttdata.transactionms.model.RequestTransaction;
import com.atorres.nttdata.transactionms.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

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
	/**.
	 * Metodo que trae todas las transferencias de un cliente
	 * @param clientId id del cliente
	 * @return Flux de TransactionDao
	 */
	@GetMapping(value = "/all/client/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<TransactionDto> allTransaction(@PathVariable String clientId) {
		return transactionService.getAllTransactionByClient(clientId)
						.doOnNext(v -> log.info("Transferencia encontrada: " + v.getId()));
	}
	/**.
	 * Metodo que trae todas las transferencias de un cliente
	 * @param accountId id del cliente
	 * @return Flux de TransactionDao
	 */
	@GetMapping(value = "/thismount/account/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<TransactionDto> thismountTranByAccount(@PathVariable String accountId) {
		return transactionService.getTransactionAccount(accountId)
						.doOnNext(v -> log.info("Transferencia encontrada: " + v.getId()));
	}
	/**.
	 * Metodo que trae todas las transferencia de este mes del cliente
	 * @param clientId id del cliente
	 * @return Flux de TransactionDao
	 */
	@GetMapping(value = "/thismounth/client/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<TransactionDto> allTransactionthisMounth(@PathVariable String clientId) {
		return transactionService.getCurrentMounthTrans(clientId)
						.doOnNext(v -> log.info("Transferencia de este mes: " + v.getId()));
	}
}
