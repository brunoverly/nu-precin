CREATE TABLE exemplos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(250),
    numero INTEGER,
    data_horario_criado TIMESTAMP,
    exemplo_enum VARCHAR(50),
    ativo BOOLEAN
);
