package com.xflprflx.paycheck.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xflprflx.paycheck.domain.Invoice;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>{

    Optional<Invoice> findByNumber(String number);
}
