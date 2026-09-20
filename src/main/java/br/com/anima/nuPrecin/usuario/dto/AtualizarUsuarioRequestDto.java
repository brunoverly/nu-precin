package br.com.anima.nuPrecin.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarUsuarioRequestDto(
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        String foto,
        @NotBlank(message = "senhaAtual é obrigatória")
        String senhaAtual,
        @NotBlank(message = "novaSenha é obrigatória")
        String novaSenha
) {
}
