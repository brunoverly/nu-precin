ALTER TABLE promocoes
    ALTER COLUMN data_fim DROP NOT NULL,
    ADD COLUMN enquanto_durar_estoque BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN ultima_confirmacao_em TIMESTAMP,
    ADD CONSTRAINT ck_promocoes_disponibilidade CHECK (
        (data_fim IS NOT NULL AND ultima_confirmacao_em IS NULL)
        OR (data_fim IS NULL AND enquanto_durar_estoque AND ultima_confirmacao_em IS NOT NULL)
    );

ALTER TABLE precos_observados
    DROP CONSTRAINT ck_preco_observado_promocao,
    ADD COLUMN enquanto_durar_estoque BOOLEAN NOT NULL DEFAULT FALSE,
    ADD CONSTRAINT ck_preco_observado_promocao CHECK (
        (origem = 'MANUAL' AND id_promocao IS NULL AND preco_original IS NULL
            AND inicio_vigencia IS NULL AND fim_vigencia IS NULL AND NOT enquanto_durar_estoque)
        OR (origem = 'PROMOCAO' AND id_promocao IS NOT NULL
            AND preco_original IS NOT NULL AND preco_original >= valor
            AND inicio_vigencia IS NOT NULL
            AND ((fim_vigencia IS NULL AND enquanto_durar_estoque)
                OR (fim_vigencia IS NOT NULL AND inicio_vigencia <= fim_vigencia)))
    );
