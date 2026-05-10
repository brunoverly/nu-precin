package br.com.anima.nuPrecin.carrinho.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CarrinhoResponseDto(
        Long id,
        String nome,
        @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
        LocalDateTime dataCadastro,
        Long idUsuario
) {
}
