package br.com.anima.nuPrecin.promocao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "PromocaoResponse")
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
