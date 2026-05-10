package br.com.anima.nuPrecin.carrinho.dto;

import java.math.BigDecimal;

public record CarrinhoItemResponseDto(
        Long id,
        Long idPromocao,
        Integer quantidadeItem,
        BigDecimal precoItem,
        BigDecimal precoTotal
) {
}

