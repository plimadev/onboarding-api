package com.github.plimadev.onboarding_api.service;

import com.github.plimadev.onboarding_api.dto.ClientMapper;
import com.github.plimadev.onboarding_api.dto.ClientRequest;
import com.github.plimadev.onboarding_api.dto.ClientResponse;
import com.github.plimadev.onboarding_api.model.ClientStatus;
import com.github.plimadev.onboarding_api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponse createClient(ClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Client with email " + request.getEmail() + " already exists");
        }
        return clientMapper.toResponse(
                clientRepository.save(clientMapper.toEntity(request))
        );
    }

    public ClientResponse getClient(Long id) {
        return clientMapper.toResponse(
                clientRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Client not found with id " + id))
        );
    }

    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    public List<ClientResponse> getClientsByStatus(ClientStatus status) {
        return clientRepository.findByStatus(status)
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    public ClientResponse advanceStatus(Long id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));
        switch (client.getStatus()) {
            case DRAFT -> client.setStatus(ClientStatus.SUBMITTED);
            case SUBMITTED -> client.setStatus(ClientStatus.UNDER_REVIEW);
            case UNDER_REVIEW -> client.setStatus(ClientStatus.APPROVED);
            default -> throw new RuntimeException("Client cannot be advanced from status: " + client.getStatus());
        }
        return clientMapper.toResponse(clientRepository.save(client));
    }

    public ClientResponse rejectClient(Long id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));
        if (client.getStatus() == ClientStatus.APPROVED ||
                client.getStatus() == ClientStatus.REJECTED) {
            throw new RuntimeException("Client cannot be rejected from status: " + client.getStatus());
        }
        client.setStatus(ClientStatus.REJECTED);
        return clientMapper.toResponse(clientRepository.save(client));
    }
}