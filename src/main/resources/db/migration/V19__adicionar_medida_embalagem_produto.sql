ALTER TABLE produtos
    ADD COLUMN quantidade_embalagem NUMERIC(15, 3),
    ADD COLUMN unidade_embalagem VARCHAR(3),
    ADD CONSTRAINT ck_produto_medida_embalagem CHECK (
        (quantidade_embalagem IS NULL AND unidade_embalagem IS NULL)
        OR (quantidade_embalagem IS NOT NULL AND quantidade_embalagem > 0
            AND unidade_embalagem IS NOT NULL AND unidade_embalagem IN ('G', 'ML', 'UN'))
    );
