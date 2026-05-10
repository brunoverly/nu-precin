
-- V7: create table itens_carrinho
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

