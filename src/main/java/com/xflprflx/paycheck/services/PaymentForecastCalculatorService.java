package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class PaymentForecastCalculatorService {

    public LocalDate calculateNewPaymentForecast(TransportDocument transportDocument) {
        // Encontra o scannedDate mais recente entre todos os Invoices relacionados ao TransportDocument
        LocalDate latestScannedDate = transportDocument.getInvoices().stream()
                .map(Invoice::getScannedDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        // Adiciona 28 dias ao scannedDate mais recente
        LocalDate paymentForecastDate = latestScannedDate.plusDays(28);

        // Avança para o primeiro dia útil do mês seguinte
        paymentForecastDate = paymentForecastDate.withDayOfMonth(1).plusMonths(1);
        while (!isWorkingDay(paymentForecastDate)) {
            paymentForecastDate = paymentForecastDate.plusDays(1);
        }

        return paymentForecastDate;
    }

    private boolean isWorkingDay(LocalDate date) {
        // Verifica se o dia é um dia útil (segunda a sexta-feira)
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return !dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY);
    }
}

