package br.com.anima.nuPrecin.produto.dto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;

public record ProdutoResponseDto(
         Long id,
         String nome,
         String descricao,
         String marca,
         String codigoDeBarras,
         String qrCode,
         String imagem,
         ProdutoEnum categoria,
         Long idUsuario,
         boolean ativo) {

    
}
