package com.xflprflx.paycheck.repositories;

import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.TransportDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Payment>  {
    Optional<Payment> findByNumber(String number);

    @Query(value = "SELECT p.* FROM tb_payment p LEFT JOIN tb_transport_document td ON p.number = td.number WHERE td.number IS NULL", nativeQuery = true)
    List<Payment> findWhereDocIsNUll();
}
