package com.xflprflx.paycheck.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xflprflx.paycheck.domain.TransportDocument;

import java.util.Optional;

public interface TransportDocumentRepository extends JpaRepository<TransportDocument, Integer>{

    Optional<TransportDocument> findByNumber(String integer);
}
