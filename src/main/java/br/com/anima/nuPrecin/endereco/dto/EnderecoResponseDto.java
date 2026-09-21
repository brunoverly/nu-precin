package br.com.anima.nuPrecin.endereco.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnderecoResponse")
public record EnderecoResponseDto(
        Long id,
        String logradouro,
        String bairro,
        String cidade,
        String estado
) {
}
