package com.atorres.nttdata.transactionms.controller;
import com.atorres.nttdata.transactionms.model.ReportClient;
import com.atorres.nttdata.transactionms.model.ResponseAvgAmount;
import com.atorres.nttdata.transactionms.model.ResponseComission;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.service.ReportService;
import com.atorres.nttdata.transactionms.service.TransactionService;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/report")
@Slf4j
public class ReportController {
	@Autowired
	ReportService reportService;
	@GetMapping(value = "/client/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<ReportClient> getReportCliente(@PathVariable String clientId){
		return reportService.getReportClient(clientId)
						.doOnSuccess(v -> log.info("Report obtenido con exito"));
	}

	/**.
	 * Metodo que retorna toda la comision cobrada a un producto durante el mes actual
	 * @param clientId  id del cliento
	 * @param productId id del producto
	 * @return ResponseComission
	 */
	@GetMapping(value = "/comission/{clientId}/{productId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<ResponseComission> getComissionProduct(
					@PathVariable String clientId,
					@PathVariable String productId) {
		return reportService.getComissionReport(clientId, productId)
						.doOnSuccess(v -> log.info("Comision del mes asciende a: {}", v.getComissionTotal()));
	}

	/**.
	 * Metodo que calcula el promedio de montos transferidos por dia para todos los producto del cliente
	 * @param clientId id cliente
	 * @return ResponseAvgAmount
	 */
	@GetMapping(value = "/avg/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<ResponseAvgAmount> getAvgAmount(
					@PathVariable String clientId) {
		return reportService.getAvgAmount(clientId)
						.doOnSuccess(v -> log.info("Transfirio en promedio " + v.getAvgAmount() + " por dia"));
	}

}
