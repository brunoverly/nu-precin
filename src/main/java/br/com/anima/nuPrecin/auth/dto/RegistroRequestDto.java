package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroRequestDto(
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        String foto,
        @NotBlank(message = "senha é obrigatória")
        String senha
) {
}
