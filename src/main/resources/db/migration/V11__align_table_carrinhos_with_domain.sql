ALTER TABLE carrinhos
    DROP COLUMN IF EXISTS nome;

ALTER TABLE carrinhos
    ADD COLUMN IF NOT EXISTS quantidade_item INTEGER,
    ADD COLUMN IF NOT EXISTS preco_item NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS preco_total NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS id_promocao BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'carrinhos'
          AND constraint_name = 'fk_carrinho_promocao'
    ) THEN
        ALTER TABLE carrinhos
            ADD CONSTRAINT fk_carrinho_promocao
                FOREIGN KEY (id_promocao) REFERENCES promocoes (id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'carrinhos'
          AND constraint_name = 'uq_carrinho_usuario'
    ) THEN
        ALTER TABLE carrinhos
            ADD CONSTRAINT uq_carrinho_usuario UNIQUE (id_usuario);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_carrinhos_promocao ON carrinhos (id_promocao);

UPDATE carrinhos c
SET quantidade_item = COALESCE(c.quantidade_item, 1),
    preco_item = COALESCE(c.preco_item, p.preco_promocao),
    id_promocao = COALESCE(c.id_promocao, p.id)
FROM (
    SELECT DISTINCT ON (id_usuario)
        id_usuario,
        id,
        preco_promocao
    FROM promocoes
    WHERE ativo = TRUE
    ORDER BY id_usuario, data_criacao DESC
) p
WHERE p.id_usuario = c.id_usuario;

UPDATE carrinhos
SET preco_total = COALESCE(preco_total, preco_item * quantidade_item)
WHERE preco_item IS NOT NULL
  AND quantidade_item IS NOT NULL;

DROP TABLE IF EXISTS itens_carrinho CASCADE;