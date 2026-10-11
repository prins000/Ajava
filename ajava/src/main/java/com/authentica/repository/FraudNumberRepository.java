package com.authentica.repository;

import com.authentica.model.FraudNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudNumberRepository extends JpaRepository<FraudNumber, Long> {
    Optional<FraudNumber> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    Page<FraudNumber> findAllByOrderByReportCountDesc(Pageable pageable);
}
