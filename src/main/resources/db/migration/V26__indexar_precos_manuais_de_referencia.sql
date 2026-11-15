CREATE INDEX idx_preco_manual_produto_loja_data
    ON precos_observados (id_produto, id_estabelecimento, observado_em DESC, id DESC)
    WHERE origem = 'MANUAL';
