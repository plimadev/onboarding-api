package com.github.plimadev.onboarding_api.dto;

import com.github.plimadev.onboarding_api.model.ClientStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientResponse {

    private Long id;
    private String fullName;
    private String email;
    private ClientStatus status;
    private LocalDateTime createdAt;
}