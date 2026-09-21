package br.com.anima.nuPrecin.carrinho.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CarrinhoItemResponse")
public record CarrinhoItemResponseDto(
        Long id,
        Long idPromocao,
        Integer quantidadeItem,
        BigDecimal precoItem,
        BigDecimal precoTotal
) {
}
