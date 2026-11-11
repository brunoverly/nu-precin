ALTER TABLE precos_observados
    ADD COLUMN id_promocao BIGINT REFERENCES promocoes(id),
    ADD COLUMN preco_original NUMERIC(12, 2),
    ADD COLUMN inicio_vigencia TIMESTAMP,
    ADD COLUMN fim_vigencia TIMESTAMP,
    ADD CONSTRAINT ck_preco_observado_promocao CHECK (
        (origem = 'MANUAL' AND id_promocao IS NULL AND preco_original IS NULL
            AND inicio_vigencia IS NULL AND fim_vigencia IS NULL)
        OR (origem = 'PROMOCAO' AND id_promocao IS NOT NULL
            AND preco_original IS NOT NULL AND preco_original >= valor
            AND inicio_vigencia IS NOT NULL AND fim_vigencia IS NOT NULL
            AND inicio_vigencia <= fim_vigencia)
    );

CREATE INDEX idx_preco_observado_promocao
    ON precos_observados (id_promocao, id DESC) WHERE id_promocao IS NOT NULL;
