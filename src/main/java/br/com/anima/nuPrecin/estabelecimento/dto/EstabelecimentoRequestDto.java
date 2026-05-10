package br.com.anima.nuPrecin.estabelecimento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstabelecimentoRequestDto(
        @NotBlank(message = "tipo é obrigatório")
        String tipo,
        @NotBlank(message = "nome é obrigatório")
        String nome,
        String foto,
        @NotBlank(message = "telefone é obrigatório")
        String telefone,
        @NotNull(message = "idEndereco é obrigatório")
        Long idEndereco,
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
