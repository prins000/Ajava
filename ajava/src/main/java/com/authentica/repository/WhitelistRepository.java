package com.authentica.repository;

import com.authentica.model.Whitelist;
import com.authentica.model.enums.WhitelistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {
    List<Whitelist> findByType(WhitelistType type);
    Optional<Whitelist> findByTypeAndItemValue(WhitelistType type, String itemValue);
    boolean existsByTypeAndItemValue(WhitelistType type, String itemValue);
}
