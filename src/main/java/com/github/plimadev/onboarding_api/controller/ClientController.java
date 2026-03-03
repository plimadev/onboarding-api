package com.github.plimadev.onboarding_api.controller;

import com.github.plimadev.onboarding_api.dto.ClientRequest;
import com.github.plimadev.onboarding_api.dto.ClientResponse;
import com.github.plimadev.onboarding_api.model.ClientStatus;
import com.github.plimadev.onboarding_api.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClient(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients(
            @RequestParam(required = false) ClientStatus status) {
        if (status != null) {
            return ResponseEntity.ok(clientService.getClientsByStatus(status));
        }
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PatchMapping("/{id}/advance")
    public ResponseEntity<ClientResponse> advanceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.advanceStatus(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ClientResponse> rejectClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.rejectClient(id));
    }
}