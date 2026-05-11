CREATE TABLE IF NOT EXISTS estabelecimentos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(250),
    tipo VARCHAR(100),
    foto VARCHAR(500),
    telefone VARCHAR(50),
    ativo BOOLEAN,
    id_endereco BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_estabelecimento_endereco FOREIGN KEY (id_endereco) REFERENCES enderecos (id),
    CONSTRAINT fk_estabelecimento_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
);

CREATE INDEX IF NOT EXISTS idx_estabelecimentos_endereco ON estabelecimentos (id_endereco);
CREATE INDEX IF NOT EXISTS idx_estabelecimentos_usuario ON estabelecimentos (id_usuario);

