CREATE TABLE IF NOT EXISTS cadastros_pendentes (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   nome VARCHAR(250) NOT NULL,
    email VARCHAR(250) NOT NULL,
    foto VARCHAR(500),
    senha_hash VARCHAR(250) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_em TIMESTAMP NOT NULL,
    consumido_em TIMESTAMP
    );

CREATE UNIQUE INDEX uq_cadastros_pendentes_email_ativo
    ON cadastros_pendentes (LOWER(email))
    WHERE consumido_em IS NULL;

CREATE INDEX idx_cadastros_pendentes_expira_em
    ON cadastros_pendentes (expira_em);


CREATE TABLE IF NOT EXISTS desafios_verificacao (
                                                    id BIGSERIAL PRIMARY KEY,
                                                    tipo VARCHAR(50) NOT NULL,
    id_cadastro_pendente BIGINT,
    id_usuario BIGINT,
    email_destino VARCHAR(250) NOT NULL,
    codigo_hash VARCHAR(128) NOT NULL,
    tentativas INTEGER NOT NULL DEFAULT 0,
    max_tentativas INTEGER NOT NULL DEFAULT 5,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_em TIMESTAMP NOT NULL,
    usado_em TIMESTAMP,
    invalidado_em TIMESTAMP,

    CONSTRAINT fk_desafio_cadastro_pendente
    FOREIGN KEY (id_cadastro_pendente)
    REFERENCES cadastros_pendentes (id),

    CONSTRAINT fk_desafio_usuario
    FOREIGN KEY (id_usuario)
    REFERENCES usuarios (id),

    CONSTRAINT ck_desafio_tipo
    CHECK (tipo IN ('REGISTRO_CONTA', 'RESET_SENHA', 'ALTERACAO_EMAIL')),

    CONSTRAINT ck_desafio_alvo_unico
    CHECK (
(id_cadastro_pendente IS NOT NULL AND id_usuario IS NULL)
    OR
(id_cadastro_pendente IS NULL AND id_usuario IS NOT NULL)
    ),

    CONSTRAINT ck_desafio_tentativas
    CHECK (tentativas >= 0 AND max_tentativas > 0)
    );

CREATE INDEX idx_desafios_verificacao_email
    ON desafios_verificacao (LOWER(email_destino));

CREATE INDEX idx_desafios_verificacao_expira_em
    ON desafios_verificacao (expira_em);

CREATE UNIQUE INDEX uq_desafio_pendente_ativo
    ON desafios_verificacao (tipo, id_cadastro_pendente)
    WHERE usado_em IS NULL
        AND invalidado_em IS NULL
        AND id_cadastro_pendente IS NOT NULL;

CREATE UNIQUE INDEX uq_desafio_usuario_ativo
    ON desafios_verificacao (tipo, id_usuario)
    WHERE usado_em IS NULL
        AND invalidado_em IS NULL
        AND id_usuario IS NOT NULL;