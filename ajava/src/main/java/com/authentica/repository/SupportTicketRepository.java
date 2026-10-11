package com.authentica.repository;

import com.authentica.model.SupportTicket;
import com.authentica.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Page<SupportTicket> findByStatusOrderByCreatedAtDesc(TicketStatus status, Pageable pageable);
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByStatus(TicketStatus status);
}
