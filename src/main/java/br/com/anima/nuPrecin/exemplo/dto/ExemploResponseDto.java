package br.com.anima.nuPrecin.exemplo.dto;

import br.com.anima.nuPrecin.exemplo.ExemploEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ExemploResponseDto(
         Long id,
         String nome,
         Integer numero,
         ExemploEnum exemploEnum,
         @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
         LocalDateTime dataHorarioCriado) {
}
