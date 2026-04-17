package br.com.anima.nuPrecin.dto;

import br.com.anima.nuPrecin.enuns.ExemploEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ExemploResponseDto(
         Long id,
         String nome,
         Integer numero,
         ExemploEnum exemploEnum,
         @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
         LocalDateTime dataHorarioCriado) {
}
