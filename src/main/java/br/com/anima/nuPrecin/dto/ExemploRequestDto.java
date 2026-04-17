package br.com.anima.nuPrecin.dto;

import br.com.anima.nuPrecin.enuns.ExemploEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExemploRequestDto(
                @NotBlank(message = "nome é obrigatório")
                String nome,
                @NotNull(message = "exemploEnum é obrigatório")
                ExemploEnum exemploEnum,
                @NotNull(message = "número é obrigatório")
                Integer numero) {
}
