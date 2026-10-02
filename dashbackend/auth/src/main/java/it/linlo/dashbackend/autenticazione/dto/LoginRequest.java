package it.linlo.dashbackend.autenticazione.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{validation.notBlank}") String username,
        @NotBlank(message = "{validation.notBlank}") String password) {
}
