package br.com.anima.nuPrecin.estabelecimento.dto;

public record EstabelecimentoResponseDto(
        Long id,
        String tipo,
        String nome,
        String foto,
        String telefone,
        Long idEndereco,
        Long idUsuario
) {
}
