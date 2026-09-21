package br.com.anima.nuPrecin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegistroResponse")
public record RegistroResponseDto(
        @Schema(example = "maria@example.com")
        String email,
        @Schema(example = "Código de confirmação enviado para o e-mail informado.")
        String mensagem
) {
}
