package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.services.TransportDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/transportDocuments")
@CrossOrigin(origins = "*")
public class TransportDocumentController {

	@Autowired
	private TransportDocumentService transportDocumentService;

	@GetMapping
	public ResponseEntity<List<TransportDocumentDTO>> findAllTransportDocuments() {
		List<TransportDocumentDTO> result = transportDocumentService.findAllTransportDocuments();
		return ResponseEntity.ok().body(result);
	}

	@PostMapping(value = "/file")
	public ResponseEntity<List<TransportDocumentDTO>>  readTransportDocumentFile(@RequestParam("file")MultipartFile file) throws IOException {
		List<TransportDocumentDTO> transportDocuments = transportDocumentService.returnTransportDocumentListFromFile(file);
		return ResponseEntity.ok().body(transportDocuments);
	}


	@PostMapping(value = "/list")
	public ResponseEntity<String> postTransportDocumentList(@Valid @RequestBody List<TransportDocumentDTO> transportDocumentDTOS) {
		try {
			transportDocumentService.saveCteWithInvoice(transportDocumentDTOS);
			return ResponseEntity.ok().body("CT-es salvos com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao salvar CT-es: " + e.getMessage());
		}
	}

	@PutMapping(value = "/blockPayment/{id}")
	public ResponseEntity<String> blockPayment(@PathVariable("id") Integer id, @RequestBody String reasonReduction) {
		transportDocumentService.blockPayment(id, reasonReduction);
		return ResponseEntity.ok().body("Pagamento baixado com sucesso.");
	}

	@PutMapping(value = "/unlockPayment/{id}")
	public ResponseEntity<String> unlockPayment(@PathVariable("id") Integer id, @RequestBody Integer paymentStatus) {
		transportDocumentService.unlockPayment(id, paymentStatus);
		return ResponseEntity.ok().body("Pagamento reaberto com sucesso.");
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deletePaymentCascadeAll(@PathVariable("id") Integer id) {
		transportDocumentService.deletePaymentCascadeAll(id);
		return ResponseEntity.ok().body("requisição recebida");
	}

	@GetMapping(value = "/filtered")
	public ResponseEntity<List<TransportDocumentDTO>> findAllFiltered(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  issueEnd,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scannedStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  scannedEnd,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastScStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  forecastScEnd,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastApprStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastApprEnd,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalEnd,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentEnd,
			@RequestParam(required = false) List<Integer> paymentStatuses) {
		List<TransportDocumentDTO> transportDocumentDTOS =
				transportDocumentService.findAllFiltered(issueStart, issueEnd,
						scannedStart, scannedEnd, forecastScStart, forecastScEnd,
						forecastApprStart, forecastApprEnd, approvalStart, approvalEnd,
						paymentStart, paymentEnd, paymentStatuses);
		return ResponseEntity.ok().body(transportDocumentDTOS);
	}

}
