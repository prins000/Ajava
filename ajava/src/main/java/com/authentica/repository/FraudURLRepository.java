package com.authentica.repository;

import com.authentica.model.FraudURL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudURLRepository extends JpaRepository<FraudURL, Long> {
    Optional<FraudURL> findByUrl(String url);
    Optional<FraudURL> findByDomain(String domain);
    boolean existsByUrl(String url);
    boolean existsByDomain(String domain);
    Page<FraudURL> findAllByOrderByReportCountDesc(Pageable pageable);
}
