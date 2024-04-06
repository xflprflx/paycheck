package com.xflprflx.paycheck.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_payment")
public class Payment  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "payment")
    @Fetch(FetchMode.SUBSELECT)
    private Set<TransportDocument> transportDocument;

    public Payment() {
    }

    public Payment(Integer id, String number, String serie, String invoiceSG, String myInvoice, String docCompensation, Double amount, String text) {
        this.id = id;
        this.number = number;
        this.serie = serie;
        this.invoiceSG = invoiceSG;
        this.myInvoice = myInvoice;
        this.docCompensation = docCompensation;
        this.amount = amount;
        this.text = text;
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

        Payment payment = (Payment) object;

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
