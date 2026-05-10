-- V1: create table usuarios (migrated from previous exemplos migration file location)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(250),
    email VARCHAR(250),
    foto VARCHAR(500),
    senha VARCHAR(250),
    data_cadastro TIMESTAMP,
    ativo BOOLEAN
);

CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios (email);
