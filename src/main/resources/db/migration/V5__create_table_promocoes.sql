CREATE TABLE IF NOT EXISTS promocoes (
    id BIGSERIAL PRIMARY KEY,
    preco_original NUMERIC(12, 2),
    preco_promocao NUMERIC(12, 2),
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP,
    data_inicio TIMESTAMP,
    data_fim TIMESTAMP,
    ativo BOOLEAN,
    id_produto BIGINT,
    id_estabelecimento BIGINT,
    id_usuario BIGINT,
    CONSTRAINT fk_promocao_produto FOREIGN KEY (id_produto) REFERENCES produtos (id),
    CONSTRAINT fk_promocao_estabelecimento FOREIGN KEY (id_estabelecimento) REFERENCES estabelecimentos (id),
    CONSTRAINT fk_promocao_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
);

CREATE INDEX IF NOT EXISTS idx_promocoes_ativo ON promocoes (ativo);
CREATE INDEX IF NOT EXISTS idx_promocoes_produto ON promocoes (id_produto);
CREATE INDEX IF NOT EXISTS idx_promocoes_estabelecimento ON promocoes (id_estabelecimento);
CREATE INDEX IF NOT EXISTS idx_promocoes_usuario ON promocoes (id_usuario);

