package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.projections.DashboardProjection;
import com.xflprflx.paycheck.services.PaymentService;
import com.xflprflx.paycheck.services.TransportDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public DashboardProjection getDashBoardProjection() {
        DashboardProjection dashboardProjection = new DashboardProjection();
        dashboardProjection.setPendingAmountValue(0.0);
        dashboardProjection.setPaidAmountValue(0.0);
        dashboardProjection.setDebateAmountValue(0.0);
        dashboardProjection.setScannedLeadTimeValue(0);
        dashboardProjection.setApprovalLeadTimeValue(0);

        dashboardProjection.getPayments().addAll(paymentService.findAll());

        List<TransportDocumentDTO> transportDocuments = transportDocumentService.findAllTransportDocuments();
        dashboardProjection.getTransportDocuments().addAll(transportDocuments);

        for (TransportDocumentDTO transportDocument : transportDocuments) {
            dashboardProjection.increaseAmountByPaymentStatus(transportDocument);
            dashboardProjection.calculateLeadTime(transportDocument);
        }
        return dashboardProjection;
    }
}
