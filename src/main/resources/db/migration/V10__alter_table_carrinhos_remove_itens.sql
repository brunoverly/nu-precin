ALTER TABLE carrinhos ADD quantidade_item INTEGER;
ALTER TABLE carrinhos ADD preco_item NUMERIC(19, 2);
ALTER TABLE carrinhos ADD preco_total NUMERIC(19, 2);
ALTER TABLE carrinhos ADD id_promocao BIGINT;

ALTER TABLE carrinhos
    ADD CONSTRAINT fk_carrinho_promocao
        FOREIGN KEY (id_promocao) REFERENCES promocoes (id);

ALTER TABLE carrinhos
    ADD CONSTRAINT uq_carrinho_usuario UNIQUE (id_usuario);

CREATE INDEX idx_carrinhos_promocao ON carrinhos (id_promocao);

DROP TABLE IF EXISTS itens_carrinho CASCADE;