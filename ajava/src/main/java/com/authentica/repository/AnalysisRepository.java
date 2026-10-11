package com.authentica.repository;

import com.authentica.model.Analysis;
import com.authentica.model.enums.AnalysisStatus;
import com.authentica.model.enums.AnalysisType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Page<Analysis> findByUserIdAndIsArchivedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Analysis> findByUserIdAndTypeAndIsArchivedFalseOrderByCreatedAtDesc(Long userId, AnalysisType type, Pageable pageable);
    Optional<Analysis> findByIdAndUserId(Long id, Long userId);
    List<Analysis> findTop10ByUserIdAndIsArchivedFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsArchivedFalse(Long userId);

    @Query("SELECT a.type as type, COUNT(a) as count FROM Analysis a GROUP BY a.type")
    List<Map<String, Object>> countGroupedByType();
}
