package br.com.anima.nuPrecin.estabelecimento.dto;

import br.com.anima.nuPrecin.endereco.dto.EnderecoResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EstabelecimentoProximoResponse", description = "Estabelecimento com distância geográfica em quilômetros")
public record EstabelecimentoProximoResponseDto(
        Long id,
        String tipo,
        String nome,
        String foto,
        String telefone,
        Long idEndereco,
        Long idUsuario,
        EnderecoResponseDto endereco,
        @Schema(description = "Distância geográfica em linha reta, em quilômetros; não representa trajeto por ruas", example = "1.25")
        double distanciaKm
) {}
