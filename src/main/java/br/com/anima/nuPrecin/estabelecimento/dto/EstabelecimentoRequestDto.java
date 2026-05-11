package br.com.anima.nuPrecin.estabelecimento.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import br.com.anima.nuPrecin.endereco.dto.EnderecoRequestDto;

public record EstabelecimentoRequestDto(
        @NotBlank(message = "tipo é obrigatório")
        String tipo,
        @NotBlank(message = "nome é obrigatório")
        String nome,
        String foto,
        @NotBlank(message = "telefone é obrigatório")
        String telefone,
        Long idEndereco,
        @Valid
        EnderecoRequestDto endereco,
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
