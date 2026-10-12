package com.authentica.repository;

import com.authentica.model.HeuristicRule;
import com.authentica.model.enums.RuleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeuristicRuleRepository extends JpaRepository<HeuristicRule, Long> {

    List<HeuristicRule> findByCategoryAndIsActiveTrue(RuleCategory category);

    List<HeuristicRule> findByIsActiveTrue();

    List<HeuristicRule> findAllByOrderByCategoryAscCreatedAtDesc();

    List<HeuristicRule> findByCategoryOrderByCreatedAtDesc(RuleCategory category);
}
