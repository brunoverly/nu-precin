package br.com.anima.nuPrecin.promocao.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "PromocaoRequest", description = "Preço e período de uma promoção no horário de Brasília")
public record PromocaoRequestDto(
        @Schema(example = "10.00")
        @NotNull(message = "preço original é obrigatório")
        @Positive(message = "preço original deve ser maior que zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal precoOriginal,
        @Schema(example = "7.99")
        @NotNull(message = "preço promocional é obrigatório")
        @Positive(message = "preço promocional deve ser maior que zero")
        @Digits(integer = 10, fraction = 2)
        BigDecimal precoPromocao,
        @Schema(example = "2026-12-01T08:00:00")
        @NotNull(message = "data de inicio é obrigatório")
        LocalDateTime dataInicio,
        @Schema(example = "2026-12-10T23:59:59", description = "Opcional quando enquantoDurarEstoque é true", nullable = true)
        LocalDateTime dataFim,
        @Schema(example = "7894900011517")
        @NotBlank(message = "código de barras é obrigatório")
        String codigoBarras,
        @Schema(example = "1")
        @NotNull(message = "idEstabelecimento é obrigatório")
        Long idEstabelecimento,
        @Schema(example = "1")
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario,
        boolean enquantoDurarEstoque
) {
}
