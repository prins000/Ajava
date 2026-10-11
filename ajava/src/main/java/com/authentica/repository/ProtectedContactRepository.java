package com.authentica.repository;

import com.authentica.model.ProtectedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProtectedContactRepository extends JpaRepository<ProtectedContact, Long> {
    List<ProtectedContact> findByUserId(Long userId);
    boolean existsByUserIdAndPhoneNumber(Long userId, String phoneNumber);
}
