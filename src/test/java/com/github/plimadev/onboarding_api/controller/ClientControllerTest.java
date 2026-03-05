package com.github.plimadev.onboarding_api.controller;


import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import com.github.plimadev.onboarding_api.dto.ClientRequest;

import com.github.plimadev.onboarding_api.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
    }

    // CREATE CLIENT TESTS
    @Test
    void createClient_shouldReturn201WithDraftStatus() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setFullName("John Doe");
        request.setEmail("john@test.com");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void createClient_withInvalidEmail_shouldReturn400() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setFullName("John Doe");
        request.setEmail("notanemail");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createClient_withBlankName_shouldReturn400() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setFullName("");
        request.setEmail("john@test.com");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_withDuplicateEmail_shouldReturn400() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setFullName("John Doe");
        request.setEmail("john@test.com");

        // Create first client
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create second client with same email
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // GET CLIENT TESTS
    @Test
    void getClient_whenExists_shouldReturn200() throws Exception {
        // First create a client
        ClientRequest request = new ClientRequest();
        request.setFullName("John Doe");
        request.setEmail("john@test.com");

        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/clients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getClient_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client not found with id 999"));
    }

    // ADVANCE STATUS TESTS
    @Test
    void advanceStatus_shouldMoveFromDraftToSubmitted() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setFullName("John Doe");
        request.setEmail("john@test.com");

        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(patch("/api/clients/{id}/advance", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void rejectClient_shouldReturn200WithRejectedStatus() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setFullName("John Doe");
        request.setEmail("john@test.com");

        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(patch("/api/clients/{id}/reject", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }
}