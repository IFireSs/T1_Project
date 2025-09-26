package com.client_processing.repository;

import com.client_processing.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    boolean existsByEmail(String email);
    boolean existsByDocumentId(String documentId);
    boolean existsByPhone(String phone);
}