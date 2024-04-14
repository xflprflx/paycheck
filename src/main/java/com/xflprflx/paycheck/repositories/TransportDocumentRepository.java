package com.xflprflx.paycheck.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xflprflx.paycheck.domain.TransportDocument;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransportDocumentRepository extends JpaRepository<TransportDocument, Integer>, JpaSpecificationExecutor<TransportDocument> {

    Optional<TransportDocument> findByNumberAndSerie(String integer, String serie);
}
