package br.com.anima.nuPrecin.carrinho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarrinhoRequestDto(
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
