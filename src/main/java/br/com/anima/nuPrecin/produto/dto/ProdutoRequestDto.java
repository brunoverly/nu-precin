package br.com.anima.nuPrecin.produto.dto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequestDto(
                @NotBlank(message = "nome é obrigatório")
                String nome,
                @NotBlank(message = "descrição é obrigatória")
                String descricao,
                @NotBlank(message = "marca é obrigatória")
                String marca,
                @NotBlank(message = "código de barras é obrigatório")
                String codigoDeBarras,
                @NotNull(message = "categoria é obrigatória")
                ProdutoEnum categoria,
                @NotNull(message = "idUsuario é obrigatório")
                Long idUsuario) {
}
