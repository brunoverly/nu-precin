package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarResetSenhaRequestDto(
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email
) {
}
