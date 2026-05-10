package br.com.anima.nuPrecin.endereco.dto;

import jakarta.validation.constraints.NotBlank;

public record EnderecoRequestDto(
        @NotBlank(message = "logradouro é obrigatório")
        String logradouro,
        @NotBlank(message = "bairro é obrigatório")
        String bairro,
        @NotBlank(message = "cidade é obrigatória")
        String cidade,
        @NotBlank(message = "estado é obrigatório")
        String estado
) {
}
