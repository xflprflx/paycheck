package com.xflprflx.paycheck.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xflprflx.paycheck.domain.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>{

}
