CREATE TABLE usuarios_estabelecimentos_favoritos (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    id_estabelecimento BIGINT NOT NULL REFERENCES estabelecimentos(id) ON DELETE CASCADE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_usuario_estabelecimento_favorito UNIQUE (id_usuario, id_estabelecimento)
);

CREATE INDEX idx_favoritos_estabelecimento
    ON usuarios_estabelecimentos_favoritos (id_estabelecimento);
