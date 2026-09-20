package br.com.anima.nuPrecin.carrinho.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CarrinhoRequestDto(
        @NotNull(message = "idPromocao é obrigatório")
        Long idPromocao,
        @NotNull(message = "quantidadeItem é obrigatório")
        @Positive(message = "quantidadeItem deve ser maior que zero")
        Integer quantidadeItem,
        /** Mantido por compatibilidade; o service usa o preço da promoção ativa. */
        @Positive(message = "precoItem deve ser maior que zero")
        BigDecimal precoItem,
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
