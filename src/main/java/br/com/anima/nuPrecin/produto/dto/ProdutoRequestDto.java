package br.com.anima.nuPrecin.produto.dto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequestDto(
                @NotBlank(message = "nome é obrigatório")
                String nome,
                @NotNull(message = "marca é obrigatória")
                String marca,
                @NotNull(message = "categoria é obrigatória")
                ProdutoEnum categoria) {
}
