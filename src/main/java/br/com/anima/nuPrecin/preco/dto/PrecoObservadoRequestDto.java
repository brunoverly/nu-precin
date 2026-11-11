package br.com.anima.nuPrecin.preco.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record PrecoObservadoRequestDto(
        @NotNull @Positive Long idProduto,
        @NotNull @Positive Long idEstabelecimento,
        @NotNull @Positive @Digits(integer = 10, fraction = 2)
        @Schema(description = "Preço observado em reais", example = "8.49")
        BigDecimal valor,
        @NotNull @PastOrPresent
        @Schema(description = "Data e hora da observação em ISO 8601 com fuso horário")
        Instant observadoEm
) {}
