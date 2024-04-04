package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class PaymentForecastCalculatorService {

    public LocalDate calculateNewPaymentForecastByScannedDate(TransportDocument transportDocument) {
        LocalDate latestScannedDate = transportDocument.getInvoices().stream()
                .map(Invoice::getScannedDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate paymentForecastDate = latestScannedDate.plusDays(28);

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
        LocalDate latestApprovalDate = transportDocument.getInvoices().stream()
                .map(Invoice::getPaymentApprovalDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate paymentForecastDate = latestApprovalDate.plusDays(28);

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

