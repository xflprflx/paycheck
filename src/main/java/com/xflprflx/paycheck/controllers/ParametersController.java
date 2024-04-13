package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.Parameters;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.services.InvoiceService;
import com.xflprflx.paycheck.services.ParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/parameters")
@CrossOrigin(origins = "*")
public class ParametersController {

	@Autowired
	private ParametersService parametersService;

	@GetMapping
	public ResponseEntity<Integer> getParams() {
		Parameters parameters = parametersService.getParams();
		if (parameters!=null){
			return ResponseEntity.ok().body(parameters.getPaymentTerms());
		}
		return ResponseEntity.ok().body(0);
	}

	@PutMapping(value = "/new")
	public ResponseEntity<Parameters> updateParams(@RequestBody @Valid Parameters parameters) {
		parameters = parametersService.updateParams(parameters);
		return ResponseEntity.ok().body(parameters);
	}

}
