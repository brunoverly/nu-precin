package br.com.anima.nuPrecin.estabelecimento.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EstabelecimentoResponse")
public record EstabelecimentoResponseDto(
        Long id,
        String tipo,
        String nome,
        String foto,
        String telefone,
        Long idEndereco,
        Long idUsuario
) {
}
