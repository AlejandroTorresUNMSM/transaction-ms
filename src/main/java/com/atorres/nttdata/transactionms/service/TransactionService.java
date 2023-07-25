package com.atorres.nttdata.transactionms.service;

import com.atorres.nttdata.transactionms.client.FeignApiDebit;
import com.atorres.nttdata.transactionms.client.FeignApiProdPasive;
import com.atorres.nttdata.transactionms.exception.CustomException;
import com.atorres.nttdata.transactionms.model.RequestTransaction;
import com.atorres.nttdata.transactionms.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionms.model.RequestRetiroDebit;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.model.accountms.AccountDto;
import com.atorres.nttdata.transactionms.model.debitms.DebitDto;
import com.atorres.nttdata.transactionms.model.dto.RetiroDebit;
import com.atorres.nttdata.transactionms.repository.TransaccionRepository;
import com.atorres.nttdata.transactionms.utils.ComissionCalculator;
import com.atorres.nttdata.transactionms.utils.MapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {
	@Autowired
	FeignApiProdPasive feignApiProdPasive;
	@Autowired
	FeignApiDebit feignApiDebit;
	@Autowired
	MapperTransaction mapper;
	@Autowired
	TransaccionRepository transaccionRepository;
	@Autowired
	ComissionCalculator comissionCalculator;
	private BigDecimal comisionTransferencia;

	/**
	 * Metodo que hace un retiro de una cuenta
	 * @param request request transaction
	 * @return transactiondto
	 */
	public Mono<TransactionDto> retiroCuenta(RequestTransactionAccount request) {
		return getValidAccount(request.getClientId(), request.getAccountId(), request.getAmount())
						.flatMap(account -> comissionCalculator.getComission(request.getClientId(), request.getAccountId(), request.getAmount(), getTransactionAccount(request.getAccountId()))
										.map(value -> {
											comisionTransferencia = value;
											log.info("La comision asciende a: " + value);
											return account.getBalance().subtract(request.getAmount()).subtract(value);
										})).single()
						.flatMap(balanceNuevo -> {
							if (balanceNuevo.doubleValue() < 0) {
								return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El monto del retiro supera el saldo disponible en la cuenta"));
							}
							return feignApiProdPasive.updateAccount(mapper.toUpdateAccount(balanceNuevo, request.getAccountId()))
											.single()
											.flatMap(ac -> transaccionRepository.save(mapper.retiroRequestToDao(request, request.getAmount(), comisionTransferencia)))
											.map(mapper::toTransDto);
						});
	}

	/**
	 * Metodo que simula un deposito por cajero
	 * @param request request
	 * @return Mono transactionDao
	 */
	public Mono<TransactionDto> depositoCuenta(RequestTransactionAccount request) {
		return getValidAccount(request.getClientId(), request.getAccountId(), request.getAmount())
						.flatMap(account -> comissionCalculator.getComission(request.getClientId(), request.getAccountId(), request.getAmount(), getTransactionAccount(request.getAccountId()))
										.map(value -> {
											comisionTransferencia = value;
											log.info("La comision asciende a: " + value);
											return account.getBalance().add(request.getAmount()).subtract(value);
										})).single()
						.flatMap(balance -> feignApiProdPasive.updateAccount(mapper.toUpdateAccount(balance, request.getAccountId()))
										.single()
										.flatMap(ac -> transaccionRepository.save(mapper.depositoRequestToDao(request, request.getAmount(), comisionTransferencia)))
										.map(mapper::toTransDto));
	}

	/**
	 * Metodo que simula una transferencia entre cuenta de un mismo cliente
	 * @param request request
	 * @return Mono transactionDao
	 */
	public Mono<TransactionDto> postTransferencia(RequestTransaction request) {
		return feignApiProdPasive.getAllAccountClient(request.getClientId())
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No hay cuentas ligadas a este cliente")))
						.filter(account -> account.getId().equals(request.getTo()) || account.getId().equals(request.getFrom()))
						.collectList()
						.map(listAccount -> listAccount.stream().collect(Collectors.toMap(AccountDto::getId, cuenta -> cuenta)))
						.flatMap(mapAccount -> transactionProccess(request, mapAccount));
	}

	/**
	 * Metodo para transferencia a terceros
	 * @param request request
	 * @return transactionDao
	 */
	public Mono<TransactionDto> getTransferenciaTerceros(RequestTransaction request) {
		return feignApiProdPasive.getAllAccountClient(request.getClientId())
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No hay cuentas ligadas a este cliente")))
						.filter(account -> account.getId().equals(request.getFrom()))
						.single()
						.concatWith(feignApiProdPasive.getAccount(request.getTo()))
						.collectList()
						.map(listAccount -> listAccount.stream().collect(Collectors.toMap(AccountDto::getId, cuenta -> cuenta)))
						.flatMap(mapAccount -> transactionProccess(request, mapAccount));
	}

	/**
	 * Metodo que simula un retiro desde un debito
	 * @param request request
	 * @return transaction
	 */
	public Mono<TransactionDto> retiroDebit(RequestRetiroDebit request) {
		return verificaAmountDebit(request)
						.flatMap(debitDto -> comissionCalculator.getComission(request.getClientId(), debitDto.getMainProduct(), request.getAmount(), getTransactionAccount(debitDto.getMainProduct()))
										.map(value -> {
											comisionTransferencia = value;
											log.info("La comision de la cuenta principal asciende a: " + value);
											return debitDto.getProductList();
										})).single()
						.flatMap(productList -> Flux.fromIterable(productList)
										.flatMap(accountId -> feignApiProdPasive.getAccount(accountId))
										.collectList())
						.map(accountList -> updateAccountDebit(accountList,request))
						.flatMap(cuentaActualizadas -> Flux.fromIterable(cuentaActualizadas)
										.flatMap(account -> feignApiProdPasive.updateAccount(mapper.toUpdateAccount(account.getBalance(), account.getId())))
										.then(transaccionRepository.save(mapper.toTransDao(request, comisionTransferencia)))
						)
						.map(mapper::toTransDto);
	}

	/**
	 * Metodo que actualiza la lista de cuentas debito
	 * @param accountList lista cuentas
	 * @param request request
	 * @return lista actualizada
	 */
	private List<AccountDto> updateAccountDebit(List<AccountDto> accountList, RequestRetiroDebit request){
		BigDecimal remainingAmount = request.getAmount().add(comisionTransferencia);
		List<AccountDto> updatedAccounts = new ArrayList<>();
		//Lista de cuenta debito
		for (AccountDto account : accountList) {
			BigDecimal balance = account.getBalance();
			if (remainingAmount.doubleValue() > balance.doubleValue()) {
				remainingAmount = remainingAmount.subtract(balance);
				account.setBalance(BigDecimal.ZERO);
				updatedAccounts.add(account);
			} else {
				account.setBalance(balance.subtract(remainingAmount));
				updatedAccounts.add(account);
				break;
			}
		}
		return updatedAccounts;
	}

	private Mono<DebitDto> verificaAmountDebit(RequestRetiroDebit request) {
		return feignApiDebit.getAllBalance(request.getDebit())
						.single()
						.flatMap(balanceTotal -> {
							if (balanceTotal.doubleValue() >= request.getAmount().doubleValue())
								return feignApiDebit.getDebit(request.getDebit()).single();
							else
								return Mono.error(new CustomException(HttpStatus.CONFLICT, "Monto supera al balance debito"));
						});
	}

	/**
	 * Metodo que realiza la transferencia entre las cuentas
	 * @param request    request transaction
	 * @param mapAccount map de cuentas
	 * @return transactionDto
	 */
	private Mono<TransactionDto> transactionProccess(RequestTransaction request, Map<String, AccountDto> mapAccount) {
		AccountDto accountFrom = mapAccount.get(request.getFrom());
		AccountDto accountTo = mapAccount.get(request.getTo());
		return comissionCalculator.getComission(request.getClientId(), accountFrom.getId(), request.getAmount(), getTransactionAccount(request.getFrom()))
						.map(value -> {
							comisionTransferencia = value;
							log.info("La comision asciende a: " + value);
							return modifyMapAccount(accountFrom, accountTo, value, request.getAmount());
						})
						.map(mapacc -> new ArrayList<>(mapacc.values()))
						.flatMap(listAccount -> Flux.fromIterable(listAccount)
										.flatMap(account -> feignApiProdPasive.updateAccount(mapper.toUpdateAccount(account.getBalance(), account.getId())))
										.then(transaccionRepository.save(mapper.transRequestToTransDao(request, comisionTransferencia))))
						.map(mapper::toTransDto);
	}

	/**
	 * Metodo que obtiene todas las transacciones de un cliente
	 * @param clientId id del cliente
	 * @return Flux transactionDao
	 */
	public Flux<TransactionDto> getAllTransactionByClient(String clientId) {
		return transaccionRepository.findAll()
						.filter(trans -> trans.getClientId().equals(clientId))
						.map(mapper::toTransDto);

	}

	/**
	 * Metodo que obtiene todas las transacciones de una cuenta de este mes
	 * @param accountId id cuenta
	 * @return Flux transactionDao
	 */
	public Flux<TransactionDto> getTransactionAccount(String accountId) {
		return feignApiProdPasive.getAccount(accountId)
						.flatMap(account -> getCurrentMounthTrans(account.getClient()))
						.filter(transaction -> transaction.getFrom().equals(accountId));

	}

	/**
	 * Metodo que traer las transacciones del cliente durante el mes
	 * @param clientId client id
	 * @return transacciones
	 */
	public Flux<TransactionDto> getCurrentMounthTrans(String clientId) {
		return transaccionRepository.findTransactionAnyMounth(2023, LocalDate.now().getMonthValue())
						.filter(trans -> trans.getClientId().equals(clientId))
						.map(mapper::toTransDto);
	}

	/**
	 * Metodo que valida la cuenta y el monto
	 * @param clientId  client id
	 * @param accountId cuenta id
	 * @param amount    monto
	 * @return accountdto
	 */
	private Mono<AccountDto> getValidAccount(String clientId, String accountId, BigDecimal amount) {
		return feignApiProdPasive.getAllAccountClient(clientId)
						.filter(account -> account.getId().equals(accountId))
						.single()
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe cuenta para ese cliente")))
						.filter(accountDao -> amount.doubleValue() > 0)
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Ingreso un monto invalido")));
	}

	/**
	 * Metodo para actualizar el Map de cuentas
	 * @param accountFrom cuenta salida
	 * @param accountTo   cuenta destino
	 * @param comision    comision
	 * @param amount      monto
	 * @return map
	 */
	private Map<String, AccountDto> modifyMapAccount(AccountDto accountFrom, AccountDto accountTo, BigDecimal comision, BigDecimal amount) {
		Map<String, AccountDto> mapAccount = new HashMap<>();
		accountFrom.setBalance(accountFrom.getBalance().subtract(amount).subtract(comision));
		accountTo.setBalance(accountTo.getBalance().add(amount));
		//Seteamos las cuentas actualizadas en el Map
		mapAccount.put(accountFrom.getId(), accountFrom);
		mapAccount.put(accountTo.getId(), accountTo);
		return mapAccount;
	}
}
