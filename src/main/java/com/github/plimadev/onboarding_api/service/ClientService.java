package com.github.plimadev.onboarding_api.service;

import com.github.plimadev.onboarding_api.model.Client;
import com.github.plimadev.onboarding_api.model.ClientStatus;
import com.github.plimadev.onboarding_api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(String fullName, String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new RuntimeException("Client with email " + email + " already exists");
        }
        Client client = new Client();
        client.setFullName(fullName);
        client.setEmail(email);
        return clientRepository.save(client);
    }

    public Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public List<Client> getClientsByStatus(ClientStatus status) {
        return clientRepository.findByStatus(status);
    }

    public Client advanceStatus(Long id) {
        Client client = getClient(id);
        switch (client.getStatus()) {
            case DRAFT -> client.setStatus(ClientStatus.SUBMITTED);
            case SUBMITTED -> client.setStatus(ClientStatus.UNDER_REVIEW);
            case UNDER_REVIEW -> client.setStatus(ClientStatus.APPROVED);
            default -> throw new RuntimeException("Client cannot be advanced from status: " + client.getStatus());
        }
        return clientRepository.save(client);
    }

    public Client rejectClient(Long id) {
        Client client = getClient(id);
        if (client.getStatus() == ClientStatus.APPROVED ||
                client.getStatus() == ClientStatus.REJECTED) {
            throw new RuntimeException("Client cannot be rejected from status: " + client.getStatus());
        }
        client.setStatus(ClientStatus.REJECTED);
        return clientRepository.save(client);
    }
}