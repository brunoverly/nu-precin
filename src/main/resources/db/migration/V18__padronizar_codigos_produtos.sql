UPDATE produtos
SET codigo_de_barras = regexp_replace(codigo_de_barras, '^[[:space:]]+|[[:space:]]+$', '', 'g'),
    qr_code = NULLIF(regexp_replace(qr_code, '^[[:space:]]+|[[:space:]]+$', '', 'g'), '');

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM produtos WHERE ativo GROUP BY
            CASE WHEN codigo_de_barras ~ '^([0-9]{8}|[0-9]{12,14})$'
                 THEN lpad(codigo_de_barras, 14, '0')
                 ELSE codigo_de_barras END
        HAVING count(*) > 1
    ) THEN
        RAISE EXCEPTION 'Há produtos ativos com GTIN/código duplicado. Revise os vínculos antes de aplicar a migration V18.';
    END IF;
END $$;

CREATE UNIQUE INDEX uq_produto_ativo_codigo_normalizado
    ON produtos ((CASE WHEN codigo_de_barras ~ '^([0-9]{8}|[0-9]{12,14})$'
                       THEN lpad(codigo_de_barras, 14, '0')
                       ELSE codigo_de_barras END))
    WHERE ativo;

CREATE UNIQUE INDEX uq_produto_ativo_qr_code
    ON produtos (qr_code) WHERE ativo AND qr_code IS NOT NULL;
