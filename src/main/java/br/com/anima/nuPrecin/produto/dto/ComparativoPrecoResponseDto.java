package br.com.anima.nuPrecin.produto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ComparativoPrecoResponseDto(
        Long idEstabelecimento,
        String nomeEstabelecimento,
        Long idPromocao,
        BigDecimal precoOriginal,
        BigDecimal precoPromocao,
        LocalDateTime inicioVigencia,
        LocalDateTime fimVigencia,
        boolean enquantoDurarEstoque,
        LocalDateTime ultimaConfirmacaoEm
) {}
