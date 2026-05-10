CREATE TABLE carrinhos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(250),
    data_cadastro TIMESTAMP,
    ativo BOOLEAN,
    id_usuario BIGINT,
    CONSTRAINT fk_carrinho_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
);

CREATE INDEX idx_carrinhos_usuario ON carrinhos (id_usuario);
