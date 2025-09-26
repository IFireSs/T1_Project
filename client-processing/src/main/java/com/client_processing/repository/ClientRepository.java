package com.client_processing.repository;

import com.client_processing.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByClientId(String clientId);
    Optional<Client> findByClientId(String clientId);
}