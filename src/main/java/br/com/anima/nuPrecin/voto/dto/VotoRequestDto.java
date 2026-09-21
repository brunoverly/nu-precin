package br.com.anima.nuPrecin.voto.dto;

import br.com.anima.nuPrecin.voto.VotoEnum;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VotoRequest")
public record VotoRequestDto(
        @Schema(example = "POSITIVO")
        @NotNull(message = "voto é obrigatório")
        VotoEnum voto,
        @Schema(example = "1")
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario,
        @Schema(example = "1")
        @NotNull(message = "idPromocao é obrigatório")
        Long idPromocao
) {
}
