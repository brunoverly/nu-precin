package br.com.anima.nuPrecin.carrinho.dto;

public record ItemCarrinhoResponseDto(
        Long id,
        Integer quantidade,
        Long idCarrinho,
        Long idProduto
) {
}
