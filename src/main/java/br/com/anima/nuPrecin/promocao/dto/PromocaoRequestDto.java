package br.com.anima.nuPrecin.promocao.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "PromocaoRequest", description = "Preço e período futuro de uma promoção")
public record PromocaoRequestDto(
        @Schema(example = "10.00")
        @NotNull(message = "preço original é obrigatório")
        @Positive(message = "preço original deve ser maior que zero")
        BigDecimal precoOriginal,
        @Schema(example = "7.99")
        @NotNull(message = "preço promocional é obrigatório")
        @Positive(message = "preço promocional deve ser maior que zero")
        BigDecimal precoPromocao,
        @Schema(example = "2026-12-01T08:00:00")
        @NotNull(message = "data de inicio é obrigatório")
        @Future(message = "data de inicio precisa ser futura")
        LocalDateTime dataInicio,
        @Schema(example = "2026-12-10T23:59:59")
        @NotNull(message = "data de término é obrigatório")
        @Future(message = "data de término precisa ser futura")
        LocalDateTime dataFim,
        @Schema(example = "7894900011517")
        @NotBlank(message = "código de barras é obrigatório")
        String codigoBarras,
        @Schema(example = "1")
        @NotNull(message = "idEstabelecimento é obrigatório")
        Long idEstabelecimento,
        @Schema(example = "1")
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
