CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(250),
    email VARCHAR(250),
    foto VARCHAR(500),
    senha VARCHAR(250),
    data_cadastro TIMESTAMP,
    ativo BOOLEAN
);

CREATE INDEX idx_usuarios_email ON usuarios (email);
