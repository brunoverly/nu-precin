package br.com.anima.nuPrecin.promocao.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromocaoResponseDto(
        Long id,
        BigDecimal precoOriginal,
        BigDecimal precoPromocao,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Long idProduto,
        Long idEstabelecimento,
        Long idUsuario
) {
}
