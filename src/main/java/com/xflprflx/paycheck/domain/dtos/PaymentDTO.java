package com.xflprflx.paycheck.domain.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xflprflx.paycheck.domain.Payment;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String number;
    private String serie;
    private String invoiceSG;
    private String myInvoice;
    private String docCompensation;
    private Double amount;
    private String text;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate paymentDate;

    public PaymentDTO() {
    }

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.number = payment.getNumber();
        this.serie = payment.getSerie();
        this.invoiceSG = payment.getInvoiceSG();
        this.myInvoice = payment.getMyInvoice();
        this.docCompensation = payment.getDocCompensation();
        this.paymentDate = payment.getPaymentDate();
        this.amount = payment.getAmount();
        this.text = payment.getText();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getInvoiceSG() {
        return invoiceSG;
    }

    public void setInvoiceSG(String invoiceSG) {
        this.invoiceSG = invoiceSG;
    }

    public String getMyInvoice() {
        return myInvoice;
    }

    public void setMyInvoice(String myInvoice) {
        this.myInvoice = myInvoice;
    }

    public String getDocCompensation() {
        return docCompensation;
    }

    public void setDocCompensation(String docCompensation) {
        this.docCompensation = docCompensation;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setPaymentDate(String paymentDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        this.paymentDate = LocalDate.parse(paymentDateStr, formatter);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        PaymentDTO payment = (PaymentDTO) object;

        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", serie='" + serie + '\'' +
                ", invoiceSG='" + invoiceSG + '\'' +
                ", myInvoice='" + myInvoice + '\'' +
                ", docCompensation='" + docCompensation + '\'' +
                ", amount=" + amount +
                ", text='" + text + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
