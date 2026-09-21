package br.com.anima.nuPrecin.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnderecoRequest")
public record EnderecoRequestDto(
        @Schema(example = "Rua da Bahia, 1080")
        @NotBlank(message = "logradouro é obrigatório")
        String logradouro,
        @Schema(example = "Centro")
        @NotBlank(message = "bairro é obrigatório")
        String bairro,
        @Schema(example = "Belo Horizonte")
        @NotBlank(message = "cidade é obrigatória")
        String cidade,
        @Schema(example = "MG")
        @NotBlank(message = "estado é obrigatório")
        String estado
) {
}
