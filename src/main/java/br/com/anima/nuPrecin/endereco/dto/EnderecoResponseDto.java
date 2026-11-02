package br.com.anima.nuPrecin.endereco.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnderecoResponse")
public record EnderecoResponseDto(
        Long id,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String cep,
        @Schema(description = "Latitude em graus decimais; vazia enquanto não houver geocodificação", example = "-19.9167", nullable = true)
        Double latitude,
        @Schema(description = "Longitude em graus decimais; vazia enquanto não houver geocodificação", example = "-43.9345", nullable = true)
        Double longitude
) {
}
