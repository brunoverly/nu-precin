CREATE TABLE votos (
    id BIGSERIAL PRIMARY KEY,
    voto VARCHAR(50),
    data_voto TIMESTAMP,
    ativo BOOLEAN,
    id_usuario BIGINT,
    id_promocao BIGINT,
    CONSTRAINT fk_voto_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id),
    CONSTRAINT fk_voto_promocao
        FOREIGN KEY (id_promocao) REFERENCES promocoes (id)
);

CREATE INDEX idx_votos_promocao ON votos (id_promocao);
CREATE INDEX idx_votos_usuario ON votos (id_usuario);
