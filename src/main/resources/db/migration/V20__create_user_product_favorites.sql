CREATE TABLE usuarios_produtos_favoritos (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    id_produto BIGINT NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_usuario_produto_favorito UNIQUE (id_usuario, id_produto)
);

CREATE INDEX idx_favoritos_produto
    ON usuarios_produtos_favoritos (id_produto);
