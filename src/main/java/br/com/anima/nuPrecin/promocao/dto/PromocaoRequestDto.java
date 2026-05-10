package br.com.anima.nuPrecin.promocao.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromocaoRequestDto(
        @NotNull(message = "Campo obrigatório")
        @Positive(message = "Preço original deve ser maior que zero")
        BigDecimal precoOriginal,
        @NotNull(message = "Campo obrigatório")
        @Positive(message = "Preço promocional deve ser maior que zero")
        BigDecimal precoPromocao,
        @NotNull(message = "Campo obrigatório")
        @Future(message = "Data de inicio precisa ser futura")
        LocalDateTime dataInicio,
        @NotNull(message = "Campo obrigatório")
        @Future(message = "Data de término precisa ser futura")
        LocalDateTime dataFim,
        @NotBlank(message = "Campo obrigatório")
        String codigoBarras,
        @NotNull(message = "Campo obrigatório")
        Long idEstabelecimento,
        @NotNull(message = "Campo obrigatório")
        Long idUsuario
) {
}
