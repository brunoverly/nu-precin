CREATE TABLE itens_carrinho (
    id BIGSERIAL PRIMARY KEY,
    quantidade INTEGER,
    ativo BOOLEAN,
    id_carrinho BIGINT,
    id_produto BIGINT,
    CONSTRAINT fk_item_carrinho
        FOREIGN KEY (id_carrinho) REFERENCES carrinhos (id),
    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto) REFERENCES produtos (id)
);

CREATE INDEX idx_itens_carrinho ON itens_carrinho (id_carrinho);
CREATE INDEX idx_itens_produto ON itens_carrinho (id_produto);
