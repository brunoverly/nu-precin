-- V2: create table enderecos
CREATE TABLE IF NOT EXISTS enderecos (
    id BIGSERIAL PRIMARY KEY,
    logradouro VARCHAR(250),
    bairro VARCHAR(250),
    cidade VARCHAR(250),
    estado VARCHAR(100)
);


