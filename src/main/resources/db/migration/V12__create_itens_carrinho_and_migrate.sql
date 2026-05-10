-- V12: Create itens_carrinho table and migrate existing single-item data into items

CREATE TABLE IF NOT EXISTS itens_carrinho (
    id BIGSERIAL PRIMARY KEY,
    quantidade_item INTEGER,
    preco_item NUMERIC(19,2),
    preco_total NUMERIC(19,2),
    id_promocao BIGINT,
    id_carrinho BIGINT,
    CONSTRAINT fk_item_promocao FOREIGN KEY (id_promocao) REFERENCES promocoes (id),
    CONSTRAINT fk_item_carrinho FOREIGN KEY (id_carrinho) REFERENCES carrinhos (id)
);

CREATE INDEX IF NOT EXISTS idx_itens_carrinho_promocao ON itens_carrinho (id_promocao);
CREATE INDEX IF NOT EXISTS idx_itens_carrinho_carrinho ON itens_carrinho (id_carrinho);

-- Migrate existing data from carrinhos single-item columns into itens_carrinho
INSERT INTO itens_carrinho (quantidade_item, preco_item, preco_total, id_promocao, id_carrinho)
SELECT quantidade_item, preco_item, preco_total, id_promocao, id
FROM carrinhos
WHERE id_promocao IS NOT NULL;

-- Recalculate cart-level preco_total as sum of item totals
UPDATE carrinhos c
SET preco_total = sub.sum_total
FROM (
    SELECT id_carrinho, COALESCE(SUM(preco_total), 0) AS sum_total
    FROM itens_carrinho
    GROUP BY id_carrinho
) sub
WHERE c.id = sub.id_carrinho;

-- If there are carts without items, ensure preco_total is at least zero
UPDATE carrinhos
SET preco_total = COALESCE(preco_total, 0)
WHERE preco_total IS NULL;

-- Remove old single-item columns from carrinhos
ALTER TABLE carrinhos DROP COLUMN IF EXISTS quantidade_item;
ALTER TABLE carrinhos DROP COLUMN IF EXISTS preco_item;
ALTER TABLE carrinhos DROP COLUMN IF EXISTS id_promocao;

-- Keep preco_total and id_usuario columns (id_usuario remains unique constraint as before)

-- End of migration

