package com.xflprflx.paycheck.repositories;

import com.xflprflx.paycheck.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer>{
    Optional<Payment> findByNumber(String number);
}
