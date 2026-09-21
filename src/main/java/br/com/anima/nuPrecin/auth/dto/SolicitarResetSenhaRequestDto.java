package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SolicitarResetSenhaRequest")
public record SolicitarResetSenhaRequestDto(
        @Schema(description = "E-mail da conta que receberá o código", example = "admin@nuprecin.com.br")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email
) {
}
