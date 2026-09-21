package br.com.anima.nuPrecin.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UsuarioRequest", description = "Dados para criação administrativa de usuário")
public record UsuarioRequestDto(
        @Schema(example = "Maria Oliveira")
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @Schema(example = "maria@example.com")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        @Schema(description = "URL opcional da foto", example = "https://cdn.example.com/maria.jpg", nullable = true)
        String foto,
        @Schema(example = "123456", format = "password")
        @NotBlank(message = "senha é obrigatória")
        String senha
) {
}
