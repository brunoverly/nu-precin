package br.com.anima.nuPrecin.promocao.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromocaoRequestDto(
        @NotNull(message = "preço original é obrigatório")
        @Positive(message = "preço original deve ser maior que zero")
        BigDecimal precoOriginal,
        @NotNull(message = "preço promocional é obrigatório")
        @Positive(message = "preço promocional deve ser maior que zero")
        BigDecimal precoPromocao,
        @NotNull(message = "data de inicio é obrigatório")
        @Future(message = "data de inicio precisa ser futura")
        LocalDateTime dataInicio,
        @NotNull(message = "data de término é obrigatório")
        @Future(message = "data de término precisa ser futura")
        LocalDateTime dataFim,
        @NotBlank(message = "código de barras é obrigatório")
        String codigoBarras,
        @NotNull(message = "idEstabelecimento é obrigatório")
        Long idEstabelecimento,
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
