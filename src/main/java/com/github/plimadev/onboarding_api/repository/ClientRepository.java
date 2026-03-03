package com.github.plimadev.onboarding_api.repository;

import com.github.plimadev.onboarding_api.model.Client;
import com.github.plimadev.onboarding_api.model.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByStatus(ClientStatus status);
    boolean existsByEmail(String email);
}