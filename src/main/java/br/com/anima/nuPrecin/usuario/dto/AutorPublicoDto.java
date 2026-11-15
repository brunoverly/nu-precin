package br.com.anima.nuPrecin.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identificação pública atual do autor. Contas inativas ou indisponíveis aparecem como Usuário indisponível, com foto nula.")
public record AutorPublicoDto(
        Long id,
        String nome,
        @Schema(description = "URL da foto pública do autor", nullable = true)
        String foto
) {}
