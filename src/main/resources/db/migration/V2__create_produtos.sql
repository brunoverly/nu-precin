CREATE TABLE produtos(
    id BIGSERIAL PRIMARY KEY,
    nome varchar(250),
    descricao varchar(250),
    marca varchar(250),
    codigo_de_barras varchar(250),
    qr_code varchar(250),
    categoria varchar(250),
    imagem varchar(250),
    ativo boolean

 
)