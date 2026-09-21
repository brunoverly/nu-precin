package br.com.anima.nuPrecin.usuario.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "UsuarioResponse")
public record UsuarioResponseDto(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Administrador NuPrecin")
        String nome,
        @Schema(example = "admin@nuprecin.com.br")
        String email,
        @Schema(description = "URL pública da foto", example = "https://project.supabase.co/storage/v1/object/public/bucket/usuarios/1/avatar/image.jpg", nullable = true)
        String foto,
        @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
        LocalDateTime dataCadastro
) {
}
