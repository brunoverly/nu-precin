package br.com.anima.nuPrecin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MensagemResponse")
public record MensagemResponseDto(String mensagem) {
}
