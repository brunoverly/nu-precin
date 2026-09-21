package br.com.anima.nuPrecin.voto.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VotoPromocaoRankingResponse")
public record VotoPromocaoRankingResponseDto(
        Long idPromocao,
        Long totalVotos
) {
}
