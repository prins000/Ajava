package com.authentica.repository;

import com.authentica.model.CommunityReport;
import com.authentica.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {
    Page<CommunityReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
    Page<CommunityReport> findByTypeAndStatusOrderByCreatedAtDesc(String type, ReportStatus status, Pageable pageable);
    Page<CommunityReport> findByStatusOrderByUpvotesDesc(ReportStatus status, Pageable pageable);
    List<CommunityReport> findTop5ByStatusOrderByCreatedAtDesc(ReportStatus status);
    Page<CommunityReport> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    long countByStatus(ReportStatus status);
}
