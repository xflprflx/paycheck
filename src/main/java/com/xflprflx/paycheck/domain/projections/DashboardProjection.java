package com.xflprflx.paycheck.domain.projections;

import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DashboardProjection {

    private Double pendingAmountValue;
    private Double paidAmountValue;
    private Double debateAmountValue;
    private Integer scannedLeadTimeValue;
    private Integer approvalLeadTimeValue;
    private List<PaymentDTO> payments = new ArrayList<>();
    private List<TransportDocumentDTO> transportDocuments = new ArrayList<>();

    public DashboardProjection() {
    }

    public Double getPendingAmountValue() {
        return pendingAmountValue;
    }

    public void setPendingAmountValue(Double pendingAmountValue) {
        this.pendingAmountValue = pendingAmountValue;
    }

    public Double getPaidAmountValue() {
        return paidAmountValue;
    }

    public void setPaidAmountValue(Double paidAmountValue) {
        this.paidAmountValue = paidAmountValue;
    }

    public Double getDebateAmountValue() {
        return debateAmountValue;
    }

    public void setDebateAmountValue(Double debateAmountValue) {
        this.debateAmountValue = debateAmountValue;
    }

    public Integer getScannedLeadTimeValue() {
        return scannedLeadTimeValue;
    }

    public void setScannedLeadTimeValue(Integer scannedLeadTimeValue) {
        this.scannedLeadTimeValue = scannedLeadTimeValue;
    }

    public Integer getApprovalLeadTimeValue() {
        return approvalLeadTimeValue;
    }

    public void setApprovalLeadTimeValue(Integer approvalLeadTimeValue) {
        this.approvalLeadTimeValue = approvalLeadTimeValue;
    }

    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public List<TransportDocumentDTO> getTransportDocuments() {
        return transportDocuments;
    }

    private boolean isPending(TransportDocumentDTO obj){
        return !obj.getPaymentStatus().equals("Pago no prazo") &&
            !obj.getPaymentStatus().equals("Pago em atraso") &&
            !obj.getPaymentStatus().equals("Pagamento abatido");
    }

    private boolean isPaid(TransportDocumentDTO obj) {
        return obj.getPaymentStatus().equals("Pago no prazo") ||
                obj.getPaymentStatus().equals("Pago em atraso");
    }

    private boolean isDebated(TransportDocumentDTO obj) {
        return obj.getPaymentStatus().equals("Pagamento abatido");
    }

    public void increaseAmountByPaymentStatus() {
        for (TransportDocumentDTO transportDocument : this.transportDocuments) {
            if (isPending(transportDocument)) {
                this.pendingAmountValue += transportDocument.getAmount();
            } else if(isPaid(transportDocument)) {
                this.paidAmountValue += transportDocument.getAmount();
            } else if (isDebated(transportDocument)) {
                this.debateAmountValue += transportDocument.getAmount();
            }
        }
    }

    public void calculateLeadTime() {
        var totalLeadTimeScanned = 0;
        var numberOfLeadTimeScanned = 0;
        var totalLeadTimeApproval = 0;
        var numberOfLeadTimeApproval = 0;

        for(TransportDocumentDTO transportDocument : this.transportDocuments) {
            for (InvoiceDTO invoice : transportDocument.getInvoices()) {
                if (invoice.getScannedDate() != null) {
                    long leadTimeScannedInDays = ChronoUnit.DAYS.between(transportDocument.getIssueDate(), invoice.getScannedDate());
                    totalLeadTimeScanned += leadTimeScannedInDays;
                    numberOfLeadTimeScanned++;

                    if(invoice.getPaymentApprovalDate() != null) {
                        long leadTimeApprovalInDays = ChronoUnit.DAYS.between(invoice.getScannedDate(), invoice.getPaymentApprovalDate());
                        totalLeadTimeApproval += leadTimeApprovalInDays;
                        numberOfLeadTimeApproval++;
                    }
                }
            }
        }

        double averageScannedLeadTime = numberOfLeadTimeScanned > 0 ? totalLeadTimeScanned / (double) numberOfLeadTimeScanned : 0;
        this.setScannedLeadTimeValue((int) Math.round(averageScannedLeadTime));

        double averageApprovalLeadTime = numberOfLeadTimeApproval > 0 ? totalLeadTimeApproval / (double) numberOfLeadTimeApproval : 0;
        this.setApprovalLeadTimeValue((int) Math.round(averageApprovalLeadTime));
    }
}
