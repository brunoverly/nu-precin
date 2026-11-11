CREATE TABLE precos_observados (
    id BIGSERIAL PRIMARY KEY,
    id_produto BIGINT NOT NULL REFERENCES produtos(id),
    id_estabelecimento BIGINT NOT NULL REFERENCES estabelecimentos(id),
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id),
    valor NUMERIC(12, 2) NOT NULL CHECK (valor > 0),
    observado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    registrado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    origem VARCHAR(30) NOT NULL,
    CONSTRAINT ck_preco_observado_datas CHECK (observado_em <= registrado_em)
);

CREATE INDEX idx_preco_observado_produto_data
    ON precos_observados (id_produto, observado_em DESC, id DESC);

CREATE INDEX idx_preco_observado_produto_loja_data
    ON precos_observados (id_produto, id_estabelecimento, observado_em DESC, id DESC);
