package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequest", description = "Credenciais para autenticação")
public record LoginRequestDto(
        @Schema(description = "E-mail da conta", example = "admin@nuprecin.com.br")
        @NotBlank(message = "Campo obrigatório")
        String email,
        @Schema(description = "Senha da conta", example = "123", format = "password")
        @NotBlank(message = "Campo obrigatório")
        String senha
) {
}
