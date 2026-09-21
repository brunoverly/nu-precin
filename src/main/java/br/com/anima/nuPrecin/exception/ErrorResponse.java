package br.com.anima.nuPrecin.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(name = "ErrorResponse", description = "Formato padrão de erro da API")
public record ErrorResponse(
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        @Schema(example = "20-09-2026 22:00:00")
        LocalDateTime timestamp,
        @Schema(example = "400")
        int status,
        @Schema(example = "BAD_REQUEST")
        String error,
        @Schema(example = "codigo: deve possuir 4 dígitos")
        String message,
        @Schema(example = "/v1/auth/registro/confirmar")
        String path
) {
}
