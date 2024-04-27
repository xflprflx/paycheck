package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Parameters;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.services.exceptions.PaymentTermsUndefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class PaymentForecastCalculatorService {

    @Autowired
    private ParametersService parametersService;

    public LocalDate calculateNewPaymentForecastByScannedDate(TransportDocument transportDocument) {

        Parameters parameters = parametersService.getParams();
        parameters.getPaymentTerms();
        LocalDate latestScannedDate = transportDocument.getInvoices().stream()
                .map(Invoice::getScannedDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate paymentForecastDate = latestScannedDate.plusDays(parameters.getPaymentTerms());

        if (isFirstWorkingDayOfMonth(paymentForecastDate)) {
            return paymentForecastDate;
        }

        paymentForecastDate = paymentForecastDate.withDayOfMonth(1).plusMonths(1);
        while (!isWorkingDay(paymentForecastDate)) {
            paymentForecastDate = paymentForecastDate.plusDays(1);
        }

        return paymentForecastDate;
    }

    public LocalDate calculateNewPaymentForecastByPaymentApprovalDate(TransportDocument transportDocument) {
        Parameters parameters = parametersService.getParams();
        parameters.getPaymentTerms();
        LocalDate latestApprovalDate = transportDocument.getInvoices().stream()
                .map(Invoice::getPaymentApprovalDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate paymentForecastDate = latestApprovalDate.plusDays(parameters.getPaymentTerms());

        if (isFirstWorkingDayOfMonth(paymentForecastDate)) {
            return paymentForecastDate;
        }

        paymentForecastDate = paymentForecastDate.withDayOfMonth(1).plusMonths(1);
        while (!isWorkingDay(paymentForecastDate)) {
            paymentForecastDate = paymentForecastDate.plusDays(1);
        }

        return paymentForecastDate;
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return !dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY);
    }

    private boolean isFirstWorkingDayOfMonth(LocalDate date) {
        return date.getDayOfMonth() == 1 && isWorkingDay(date);
    }
}

