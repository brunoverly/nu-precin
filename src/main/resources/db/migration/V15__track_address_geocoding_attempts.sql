ALTER TABLE enderecos
    ADD COLUMN geocodificacao_tentada_em TIMESTAMP,
    ADD COLUMN versao BIGINT NOT NULL DEFAULT 0;
