package br.com.anima.nuPrecin.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequestDto(
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        String foto,
        @NotNull(message = "senha é obrigatória")
        String senha
) {
}
