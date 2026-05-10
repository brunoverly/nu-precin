package br.com.anima.nuPrecin.voto.dto;

import br.com.anima.nuPrecin.voto.VotoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record VotoResponseDto(
        Long id,
        VotoEnum voto,
        @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
        LocalDateTime dataVoto,
        Long idUsuario,
        Long idPromocao
) {
}
