CREATE TABLE exemplos(
    id BIGSERIAL PRIMARY KEY,
    nome varchar(250),
    numero int,
    data_horario_criado TIMESTAMP,
    exemplo_enum varchar(50),
    ativo boolean
)