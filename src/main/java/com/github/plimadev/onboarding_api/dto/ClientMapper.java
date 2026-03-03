package com.github.plimadev.onboarding_api.dto;

import com.github.plimadev.onboarding_api.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest request) {
        Client client = new Client();
        client.setFullName(request.getFullName());
        client.setEmail(request.getEmail());
        return client;
    }

    public ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setFullName(client.getFullName());
        response.setEmail(client.getEmail());
        response.setStatus(client.getStatus());
        response.setCreatedAt(client.getCreatedAt());
        return response;
    }
}