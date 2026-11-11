package br.com.anima.nuPrecin.preco.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record PrecoObservadoResponseDto(
        Long id, Long idProduto, Long idEstabelecimento, Long idUsuario,
        BigDecimal valor, Instant observadoEm, Instant registradoEm, String origem,
        Long idPromocao, BigDecimal precoOriginal, LocalDateTime inicioVigencia, LocalDateTime fimVigencia, boolean enquantoDurarEstoque
) {}
