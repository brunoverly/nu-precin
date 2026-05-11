package br.com.anima.nuPrecin.carrinho.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarrinhoResponseDto(
        Long id,
        List<CarrinhoItemResponseDto> itens,
        BigDecimal precoTotal,
        @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
        LocalDateTime dataCadastro,
        Long idUsuario
) {
}
