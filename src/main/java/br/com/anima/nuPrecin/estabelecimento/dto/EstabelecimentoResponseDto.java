package br.com.anima.nuPrecin.estabelecimento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import br.com.anima.nuPrecin.endereco.dto.EnderecoResponseDto;

@Schema(name = "EstabelecimentoResponse")
public record EstabelecimentoResponseDto(
        Long id,
        String tipo,
        String nome,
        String foto,
        String telefone,
        @Schema(description = "ID do endereço, mantido para compatibilidade")
        Long idEndereco,
        Long idUsuario,
        @Schema(description = "Endereço completo do estabelecimento")
        EnderecoResponseDto endereco
) {
}
