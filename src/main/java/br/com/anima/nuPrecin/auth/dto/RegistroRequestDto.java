package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegistroRequest", description = "Dados para iniciar o cadastro público")
public record RegistroRequestDto(
        @Schema(example = "Maria Oliveira")
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @Schema(example = "maria@example.com")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        @Schema(description = "URL opcional da foto. Também pode ser enviada depois por multipart.", example = "https://cdn.example.com/maria.jpg", nullable = true)
        String foto,
        @Schema(example = "123456", format = "password")
        @NotBlank(message = "senha é obrigatória")
        String senha
) {
}
