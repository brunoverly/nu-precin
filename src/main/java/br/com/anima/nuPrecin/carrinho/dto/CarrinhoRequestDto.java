package br.com.anima.nuPrecin.carrinho.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "CarrinhoRequest", description = "Item a adicionar ao carrinho. O preço efetivo vem da promoção ativa.")
public record CarrinhoRequestDto(
        @Schema(example = "1")
        @NotNull(message = "idPromocao é obrigatório")
        Long idPromocao,
        @Schema(example = "2")
        @NotNull(message = "quantidadeItem é obrigatório")
        @Positive(message = "quantidadeItem deve ser maior que zero")
        Integer quantidadeItem,
        /** Mantido por compatibilidade; o service usa o preço da promoção ativa. */
        @Schema(description = "Mantido por compatibilidade; o backend usa o preço da promoção", example = "2.49", nullable = true)
        @Positive(message = "precoItem deve ser maior que zero")
        BigDecimal precoItem,
        @Schema(example = "1")
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
