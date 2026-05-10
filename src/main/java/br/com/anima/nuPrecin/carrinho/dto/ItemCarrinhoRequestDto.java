package br.com.anima.nuPrecin.carrinho.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemCarrinhoRequestDto(
        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "quantidade deve ser maior que zero")
        Integer quantidade,
        @NotNull(message = "idCarrinho é obrigatório")
        Long idCarrinho,
        @NotNull(message = "idProduto é obrigatório")
        Long idProduto
) {
}
