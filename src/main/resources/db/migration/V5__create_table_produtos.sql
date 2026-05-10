CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(250) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    marca VARCHAR(250) NOT NULL,
    codigo_de_barras VARCHAR(100) NOT NULL,
    qr_code VARCHAR(250),
    imagem VARCHAR(500),
    categoria VARCHAR(100) NOT NULL,
    ativo BOOLEAN,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_produto_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
);

CREATE INDEX idx_produtos_usuario ON produtos (id_usuario);
CREATE INDEX idx_produtos_codigo_barras ON produtos (codigo_de_barras);
