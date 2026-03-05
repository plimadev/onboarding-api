package com.github.plimadev.onboarding_api.service;

import com.github.plimadev.onboarding_api.dto.ClientMapper;
import com.github.plimadev.onboarding_api.dto.ClientRequest;
import com.github.plimadev.onboarding_api.dto.ClientResponse;
import com.github.plimadev.onboarding_api.exception.ResourceNotFoundException;
import com.github.plimadev.onboarding_api.model.Client;
import com.github.plimadev.onboarding_api.model.ClientStatus;
import com.github.plimadev.onboarding_api.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientResponse clientResponse;
    private ClientRequest clientRequest;

    @BeforeEach
    void setUp() {
        clientRequest = new ClientRequest();
        clientRequest.setFullName("John Doe");
        clientRequest.setEmail("john@test.com");

        client = new Client();
        client.setId(1L);
        client.setFullName("John Doe");
        client.setEmail("john@test.com");
        client.setStatus(ClientStatus.DRAFT);
        client.setCreatedAt(LocalDateTime.now());

        clientResponse = new ClientResponse();
        clientResponse.setId(1L);
        clientResponse.setFullName("John Doe");
        clientResponse.setEmail("john@test.com");
        clientResponse.setStatus(ClientStatus.DRAFT);
        clientResponse.setCreatedAt(LocalDateTime.now());
    }

    // CREATE CLIENT TESTS
    @Test
    void createClient_shouldReturnClientWithDraftStatus() {
        when(clientRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(clientMapper.toEntity(clientRequest)).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        ClientResponse response = clientService.createClient(clientRequest);

        assertEquals(ClientStatus.DRAFT, response.getStatus());
        assertEquals("John Doe", response.getFullName());
    }

    @Test
    void createClient_whenEmailExists_shouldThrowException() {
        when(clientRepository.existsByEmail("john@test.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clientService.createClient(clientRequest));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(clientRepository, never()).save(any());
    }

    // GET CLIENT TESTS
    @Test
    void getClient_whenClientExists_shouldReturnClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        ClientResponse response = clientService.getClient(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void getClient_whenClientNotFound_shouldThrowResourceNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clientService.getClient(99L));
    }

    // ADVANCE STATUS TESTS
    @Test
    void advanceStatus_fromDraft_shouldMovToSubmitted() {
        client.setStatus(ClientStatus.DRAFT);
        clientResponse.setStatus(ClientStatus.SUBMITTED);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        ClientResponse response = clientService.advanceStatus(1L);

        assertEquals(ClientStatus.SUBMITTED, response.getStatus());
    }

    @Test
    void advanceStatus_fromApproved_shouldThrowException() {
        client.setStatus(ClientStatus.APPROVED);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clientService.advanceStatus(1L));

        assertTrue(exception.getMessage().contains("cannot be advanced"));
    }

    // REJECT CLIENT TESTS
    @Test
    void rejectClient_whenUnderReview_shouldReturnRejected() {
        client.setStatus(ClientStatus.UNDER_REVIEW);
        clientResponse.setStatus(ClientStatus.REJECTED);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        ClientResponse response = clientService.rejectClient(1L);

        assertEquals(ClientStatus.REJECTED, response.getStatus());
    }

    @Test
    void rejectClient_whenAlreadyApproved_shouldThrowException() {
        client.setStatus(ClientStatus.APPROVED);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clientService.rejectClient(1L));

        assertTrue(exception.getMessage().contains("cannot be rejected"));
    }
}