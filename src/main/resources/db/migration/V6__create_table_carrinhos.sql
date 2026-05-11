CREATE TABLE IF NOT EXISTS carrinhos (
    id BIGSERIAL PRIMARY KEY,
    data_cadastro TIMESTAMP,
    ativo BOOLEAN,
    id_usuario BIGINT,
    preco_total NUMERIC(19,2),
    CONSTRAINT fk_carrinho_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_carrinhos_usuario ON carrinhos (id_usuario);
CREATE INDEX IF NOT EXISTS idx_carrinhos_usuario ON carrinhos (id_usuario);

