CREATE INDEX idx_votos_ativos_data_voto
    ON votos (data_voto DESC)
    WHERE ativo = TRUE;

CREATE INDEX idx_votos_ativos_tipo_data
    ON votos (voto, data_voto DESC)
    WHERE ativo = TRUE;

CREATE INDEX idx_promocoes_ativos_produto_estabelecimento
    ON promocoes (id_produto, id_estabelecimento)
    WHERE ativo = TRUE;

CREATE INDEX idx_produtos_ativos_codigo_barras
    ON produtos (codigo_de_barras)
    WHERE ativo = TRUE;
