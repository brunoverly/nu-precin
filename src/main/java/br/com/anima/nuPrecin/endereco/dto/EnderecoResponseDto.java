package br.com.anima.nuPrecin.endereco.dto;

public record EnderecoResponseDto(
        Long id,
        String logradouro,
        String bairro,
        String cidade,
        String estado
) {
}
