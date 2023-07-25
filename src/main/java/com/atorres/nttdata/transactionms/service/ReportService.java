package com.atorres.nttdata.transactionms.service;

import com.atorres.nttdata.transactionms.client.FeignApiClient;
import com.atorres.nttdata.transactionms.client.FeignApiDebit;
import com.atorres.nttdata.transactionms.client.FeignApiProdActive;
import com.atorres.nttdata.transactionms.client.FeignApiProdPasive;
import com.atorres.nttdata.transactionms.exception.CustomException;
import com.atorres.nttdata.transactionms.model.ReportClient;
import com.atorres.nttdata.transactionms.model.ResponseAvgAmount;
import com.atorres.nttdata.transactionms.model.ResponseComission;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.model.accountms.AccountDto;
import com.atorres.nttdata.transactionms.model.clientms.ClientDto;
import com.atorres.nttdata.transactionms.model.creditms.CreditDto;
import com.atorres.nttdata.transactionms.model.debitms.DebitDto;
import com.atorres.nttdata.transactionms.repository.TransaccionRepository;
import com.atorres.nttdata.transactionms.utils.MapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ReportService {
	@Autowired
	TransaccionRepository transaccionRepository;
	@Autowired
	MapperTransaction mapper;
	@Autowired
	FeignApiClient feignApiClient;
	@Autowired
	FeignApiProdActive feignApiProdActive;
	@Autowired
	FeignApiProdPasive feignApiProdPasive;
	@Autowired
	FeignApiDebit feignApiDebit;

	/**
	 * Metodo que calcula la suma de las comision de un producto durante un mes
	 * @param clientId  id cliente
	 * @param productId id producto
	 * @return ResponseComision
	 */
	public Mono<ResponseComission> getComissionReport(String clientId, String productId) {
		return this.getCurrentMounthTrans(clientId)
						.filter(trans -> trans.getFrom().equals(productId))
						.collectList()
						.flatMap(transList -> Mono.just(transList.stream()))
						.flatMap(stream -> {
							BigDecimal totalComission = stream
											.map(trans -> Objects.requireNonNullElse(trans.getComission(), BigDecimal.ZERO))
											.reduce(BigDecimal.ZERO, BigDecimal::add);
							return Mono.just(new ResponseComission(clientId, totalComission));
						});
	}

	/**.
	 * Metodo que calcula el promedio que se transfirio por dia durante el mes
	 * @param clientId id cliente
	 * @return ResponseAvgAmount
	 */
	public Mono<ResponseAvgAmount> getAvgAmount(String clientId) {
		return this.getCurrentMounthTrans(clientId)
						.map(TransactionDto::getBalance)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
						.flatMap(totalmonto -> {
							int numeroDias = LocalDate.now().getDayOfMonth();
							log.info("Cantidad de dias: " + numeroDias);
							return Mono.just(totalmonto).map((totalMonto -> {
												log.info("Transfirio en total " + totalMonto);
												return totalMonto.divide(BigDecimal.valueOf(numeroDias), RoundingMode.HALF_UP);
											}))
											.map(mount -> new ResponseAvgAmount(clientId, mount));
						});
	}

	public Mono<ReportClient> getReportClient(String clientId) {
		ReportClient report = new ReportClient();

		// Llamada al servicio feignApiClient para obtener el cliente por su id
		Mono<ClientDto> clientMono = feignApiClient.getClient(clientId)
						.single()
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No existe el cliente")));

		// Llamada al servicio feignApiProdPasive para obtener las cuentas del cliente
		Mono<List<AccountDto>> accountListMono = feignApiProdPasive.getAllAccountClient(clientId).collectList();

		// Llamada al servicio feignApiProdActive para obtener los créditos del cliente
		Mono<List<CreditDto>> creditListMono = feignApiProdActive.getAllCreditClient(clientId).collectList();

		// Llamada al servicio feignApiDebit para obtener los débitos del cliente
		Mono<List<DebitDto>> debitListMono = feignApiDebit.getDebitClient(clientId).collectList();

		return Mono.zip(clientMono, accountListMono, creditListMono, debitListMono)
						.flatMap(tuple -> {
							ClientDto client = tuple.getT1();
							List<AccountDto> accountList = tuple.getT2();
							List<CreditDto> creditList = tuple.getT3();
							List<DebitDto> debitList = tuple.getT4();

							report.setClientId(clientId);
							report.setClientDto(client);
							report.setAccountDtoList(accountList);
							report.setCreditDtoList(creditList);
							report.setDebitDtoList(debitList);

							return Mono.just(report);
						})
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No se encontró el cliente")));
	}

	public Flux<TransactionDto> getCurrentMounthTrans(String clientId) {
		return transaccionRepository.findTransactionAnyMounth(2023, LocalDate.now().getMonthValue())
						.filter(trans -> trans.getClientId().equals(clientId))
						.map(mapper::toTransDto);
	}
}
