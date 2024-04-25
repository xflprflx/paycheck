package com.xflprflx.paycheck.repositories;

import com.xflprflx.paycheck.domain.TransportDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransportDocumentRepository extends JpaRepository<TransportDocument, Integer>, JpaSpecificationExecutor<TransportDocument> {

    @Query("SELECT td FROM TransportDocument td JOIN FETCH td.payment WHERE td IN :transportDocuments")
    List<TransportDocument> findTransportDocumentPayments(@Param("transportDocuments") List<TransportDocument> transportDocuments);

    @Query("SELECT td FROM TransportDocument td LEFT JOIN FETCH td.payment")
    List<TransportDocument> findAllWithPayment();

}
