package br.com.anima.nuPrecin.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        String estado,
        @Schema(description = "Número opcional, separado do logradouro", example = "4500", nullable = true)
        String numero,
        @Schema(description = "CEP opcional com oito dígitos, sem pontuação", example = "31270010", nullable = true)
        @Pattern(regexp = "\\d{8}", message = "cep deve possuir 8 dígitos, sem pontuação")
        String cep
) {
}
