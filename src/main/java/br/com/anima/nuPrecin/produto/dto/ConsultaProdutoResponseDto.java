package br.com.anima.nuPrecin.produto.dto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;
import br.com.anima.nuPrecin.produto.UnidadeEmbalagem;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

public record ConsultaProdutoResponseDto(
        Origem origem,
        ProdutoResponseDto produto,
        Sugestao sugestao
) {
    public enum Origem { LOCAL, OPEN_FOOD_FACTS }

    @Schema(description = "Dados para revisão no cadastro. Campos ausentes devem ser preenchidos pelo usuário.")
    public record Sugestao(
            String codigoDeBarras, String nome, String descricao, String marca,
            ProdutoEnum categoria, String imagem, String quantidade, String fonte,
            BigDecimal quantidadeEmbalagem, UnidadeEmbalagem unidadeEmbalagem
    ) {}
}
