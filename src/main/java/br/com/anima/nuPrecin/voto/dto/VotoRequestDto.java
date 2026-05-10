package br.com.anima.nuPrecin.voto.dto;

import br.com.anima.nuPrecin.voto.VotoEnum;
import jakarta.validation.constraints.NotNull;

public record VotoRequestDto(
        @NotNull(message = "voto é obrigatório")
        VotoEnum voto,
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario,
        @NotNull(message = "idPromocao é obrigatório")
        Long idPromocao
) {
}
