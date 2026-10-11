package com.authentica.repository;

import com.authentica.model.FraudQR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudQRRepository extends JpaRepository<FraudQR, Long> {
    Optional<FraudQR> findByQrContent(String qrContent);
    Optional<FraudQR> findByUpiHandle(String upiHandle);
}
