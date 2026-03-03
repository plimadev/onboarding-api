package com.github.plimadev.onboarding_api.controller;

import com.github.plimadev.onboarding_api.model.Client;
import com.github.plimadev.onboarding_api.model.ClientStatus;
import com.github.plimadev.onboarding_api.service.ClientService;
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
    public ResponseEntity<Client> createClient(@RequestParam String fullName,
                                               @RequestParam String email) {
        Client client = clientService.createClient(fullName, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClient(id));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(
            @RequestParam(required = false) ClientStatus status) {
        if (status != null) {
            return ResponseEntity.ok(clientService.getClientsByStatus(status));
        }
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PatchMapping("/{id}/advance")
    public ResponseEntity<Client> advanceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.advanceStatus(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Client> rejectClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.rejectClient(id));
    }
}