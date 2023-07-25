package com.atorres.nttdata.transactionms.service;

import com.atorres.nttdata.transactionms.model.ResponseAvgAmount;
import com.atorres.nttdata.transactionms.model.ResponseComission;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.repository.TransaccionRepository;
import com.atorres.nttdata.transactionms.utils.MapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Slf4j
public class ReportService {
	@Autowired
	TransaccionRepository transaccionRepository;
	@Autowired
	MapperTransaction mapper;

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

	public Flux<TransactionDto> getCurrentMounthTrans(String clientId) {
		return transaccionRepository.findTransactionAnyMounth(2023, LocalDate.now().getMonthValue())
						.filter(trans -> trans.getClientId().equals(clientId))
						.map(mapper::toTransDto);
	}
}
