package com.xflprflx.paycheck.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xflprflx.paycheck.domain.TransportDocument;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransportDocumentRepository extends JpaRepository<TransportDocument, Integer>, JpaSpecificationExecutor<TransportDocument> {

    Optional<TransportDocument> findByNumberAndSerieAndIssueDate(String integer, String serie, LocalDate issueDate);

    Optional<TransportDocument> findByNumberAndSerieAndAmount(String integer, String serie, Double amount);

    @Query("SELECT obj FROM TransportDocument obj LEFT JOIN FETCH obj.payment")
    List<TransportDocument> findTransportDocumentsPayments();
}
