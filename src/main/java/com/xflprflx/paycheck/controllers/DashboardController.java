package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.projections.DashboardProjection;
import com.xflprflx.paycheck.services.PaymentService;
import com.xflprflx.paycheck.services.TransportDocumentService;
import com.xflprflx.paycheck.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransportDocumentService transportDocumentService;

    @GetMapping()
    public ResponseEntity<DashboardProjection> getDashboardProjection() {

        DashboardProjection dashboardProjection = new DashboardProjection();
        dashboardProjection.setPendingAmountValue(0.0);
        dashboardProjection.setPaidAmountValue(0.0);
        dashboardProjection.setDebateAmountValue(0.0);
        dashboardProjection.setScannedLeadTimeValue(0);
        dashboardProjection.setApprovalLeadTimeValue(0);

        dashboardProjection.getPayments().addAll(paymentService.findAllPaymentDTO());

        List<TransportDocumentDTO> transportDocuments = transportDocumentService.findAllTransportDocuments();

        dashboardProjection.getTransportDocuments().addAll(transportDocuments);

        dashboardProjection.increaseAmountByPaymentStatus();

        dashboardProjection.calculateLeadTime();

        return ResponseEntity.ok().body(dashboardProjection);
    }

    @GetMapping(value = "/filtered")
    public ResponseEntity<DashboardProjection> getDashboardProjectionFiltered(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scannedStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scannedEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastScStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastScEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastApprStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastApprEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentEnd,
            @RequestParam(required = false) Integer paymentStatus) {

        DashboardProjection dashboardProjection = new DashboardProjection();
        dashboardProjection.setPendingAmountValue(0.0);
        dashboardProjection.setPaidAmountValue(0.0);
        dashboardProjection.setDebateAmountValue(0.0);
        dashboardProjection.setScannedLeadTimeValue(0);
        dashboardProjection.setApprovalLeadTimeValue(0);

        dashboardProjection.getPayments().addAll(paymentService.findAllFiltered(issueStart, issueEnd,
                scannedStart, scannedEnd, forecastScStart, forecastScEnd,
                forecastApprStart, forecastApprEnd, approvalStart, approvalEnd,
                paymentStart, paymentEnd, paymentStatus));

        List<TransportDocumentDTO> transportDocuments =
                transportDocumentService.findAllFiltered(issueStart, issueEnd,
                scannedStart, scannedEnd, forecastScStart, forecastScEnd,
                forecastApprStart, forecastApprEnd, approvalStart, approvalEnd,
                paymentStart, paymentEnd, paymentStatus);

        if (dashboardProjection.getPayments().size() == 0 && transportDocuments.size() == 0) {
            throw new ObjectNotFoundException("Nenhum registro encontrado");
        }

        dashboardProjection.getTransportDocuments().addAll(transportDocuments);

        dashboardProjection.increaseAmountByPaymentStatus();

        dashboardProjection.calculateLeadTime();

        return ResponseEntity.ok().body(dashboardProjection);
    }
}
