-- Esta migration não remove nem altera os dados de teste da V9.
-- Ela normaliza e valida os dados existentes antes de aplicar as garantias
-- que já são exigidas pelo domínio e pelo código Java.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM usuarios
        WHERE nome IS NULL
           OR btrim(nome) = ''
           OR email IS NULL
           OR btrim(email) = ''
           OR senha IS NULL
           OR data_cadastro IS NULL
           OR ativo IS NULL
           OR role IS NULL
    ) THEN
        RAISE EXCEPTION 'V10 abortada: usuarios possui campos obrigatórios nulos ou vazios';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM usuarios
        WHERE role NOT IN ('USER', 'ADMIN')
    ) THEN
        RAISE EXCEPTION 'V10 abortada: usuarios possui role inválida';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM usuarios
        GROUP BY lower(btrim(email))
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'V10 abortada: existem e-mails duplicados após normalização';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM enderecos
        WHERE logradouro IS NULL OR btrim(logradouro) = ''
           OR bairro IS NULL OR btrim(bairro) = ''
           OR cidade IS NULL OR btrim(cidade) = ''
           OR estado IS NULL OR btrim(estado) = ''
    ) THEN
        RAISE EXCEPTION 'V10 abortada: enderecos possui campos obrigatórios nulos ou vazios';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM estabelecimentos
        WHERE nome IS NULL OR btrim(nome) = ''
           OR tipo IS NULL OR btrim(tipo) = ''
           OR telefone IS NULL OR btrim(telefone) = ''
           OR ativo IS NULL
           OR id_endereco IS NULL
           OR id_usuario IS NULL
    ) THEN
        RAISE EXCEPTION 'V10 abortada: estabelecimentos possui campos obrigatórios nulos ou vazios';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM estabelecimentos
        WHERE tipo NOT IN ('MERCADO', 'SUPERMERCADO', 'ATACADAO')
    ) THEN
        RAISE EXCEPTION 'V10 abortada: estabelecimentos possui tipo inválido';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM estabelecimentos
        GROUP BY id_endereco
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'V10 abortada: um mesmo endereco está associado a mais de um estabelecimento';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM produtos
        WHERE nome IS NULL OR btrim(nome) = ''
           OR descricao IS NULL OR btrim(descricao) = ''
           OR marca IS NULL OR btrim(marca) = ''
           OR codigo_de_barras IS NULL OR btrim(codigo_de_barras) = ''
           OR categoria IS NULL OR btrim(categoria) = ''
           OR ativo IS NULL
           OR id_usuario IS NULL
    ) THEN
        RAISE EXCEPTION 'V10 abortada: produtos possui campos obrigatórios nulos ou vazios';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM produtos
        WHERE categoria NOT IN ('BEBIDA', 'ALIMENTO', 'LIMPEZA', 'ELETRONICO', 'LAZER', 'FERRAMENTA', 'VESTIMENTA', 'OUTRO')
    ) THEN
        RAISE EXCEPTION 'V10 abortada: produtos possui categoria inválida';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM promocoes
        WHERE preco_original IS NULL OR preco_original <= 0
           OR preco_promocao IS NULL OR preco_promocao <= 0
           OR data_criacao IS NULL
           OR data_inicio IS NULL
           OR data_fim IS NULL
           OR ativo IS NULL
           OR id_produto IS NULL
           OR id_estabelecimento IS NULL
           OR id_usuario IS NULL
           OR preco_promocao > preco_original
           OR data_fim < data_inicio
    ) THEN
        RAISE EXCEPTION 'V10 abortada: promocoes possui dados inválidos ou relacionamentos incompletos';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM carrinhos
        WHERE data_cadastro IS NULL
           OR ativo IS NULL
           OR id_usuario IS NULL
           OR preco_total IS NULL
           OR preco_total < 0
    ) THEN
        RAISE EXCEPTION 'V10 abortada: carrinhos possui campos obrigatórios nulos ou inválidos';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM itens_carrinho
        WHERE quantidade_item IS NULL OR quantidade_item <= 0
           OR preco_item IS NULL OR preco_item <= 0
           OR preco_total IS NULL OR preco_total <= 0
           OR id_promocao IS NULL
           OR id_carrinho IS NULL
    ) THEN
        RAISE EXCEPTION 'V10 abortada: itens_carrinho possui dados inválidos ou incompletos';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM votos
        WHERE voto IS NULL
           OR data_voto IS NULL
           OR ativo IS NULL
           OR id_usuario IS NULL
           OR id_promocao IS NULL
    ) THEN
        RAISE EXCEPTION 'V10 abortada: votos possui campos obrigatórios nulos';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM votos
        WHERE voto NOT IN ('POSITIVO', 'NEGATIVO')
    ) THEN
        RAISE EXCEPTION 'V10 abortada: votos possui tipo inválido';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM votos
        WHERE ativo = TRUE
        GROUP BY id_usuario, id_promocao
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'V10 abortada: existem votos ativos duplicados para usuario e promocao';
    END IF;
END;
$$;

UPDATE usuarios
SET email = lower(btrim(email));

ALTER TABLE usuarios
    ALTER COLUMN nome SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN senha SET NOT NULL,
    ALTER COLUMN data_cadastro SET NOT NULL,
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN role SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE,
    ALTER COLUMN role SET DEFAULT 'USER';

ALTER TABLE enderecos
    ALTER COLUMN logradouro SET NOT NULL,
    ALTER COLUMN bairro SET NOT NULL,
    ALTER COLUMN cidade SET NOT NULL,
    ALTER COLUMN estado SET NOT NULL;

ALTER TABLE estabelecimentos
    ALTER COLUMN nome SET NOT NULL,
    ALTER COLUMN tipo SET NOT NULL,
    ALTER COLUMN telefone SET NOT NULL,
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN id_endereco SET NOT NULL,
    ALTER COLUMN id_usuario SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE;

ALTER TABLE produtos
    ALTER COLUMN nome SET NOT NULL,
    ALTER COLUMN descricao SET NOT NULL,
    ALTER COLUMN marca SET NOT NULL,
    ALTER COLUMN codigo_de_barras SET NOT NULL,
    ALTER COLUMN categoria SET NOT NULL,
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN id_usuario SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE;

ALTER TABLE promocoes
    ALTER COLUMN preco_original SET NOT NULL,
    ALTER COLUMN preco_promocao SET NOT NULL,
    ALTER COLUMN data_criacao SET NOT NULL,
    ALTER COLUMN data_inicio SET NOT NULL,
    ALTER COLUMN data_fim SET NOT NULL,
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN id_produto SET NOT NULL,
    ALTER COLUMN id_estabelecimento SET NOT NULL,
    ALTER COLUMN id_usuario SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE;

ALTER TABLE carrinhos
    ALTER COLUMN data_cadastro SET NOT NULL,
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN id_usuario SET NOT NULL,
    ALTER COLUMN preco_total SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE,
    ALTER COLUMN preco_total SET DEFAULT 0;

ALTER TABLE itens_carrinho
    ALTER COLUMN quantidade_item SET NOT NULL,
    ALTER COLUMN preco_item SET NOT NULL,
    ALTER COLUMN preco_total SET NOT NULL,
    ALTER COLUMN id_promocao SET NOT NULL,
    ALTER COLUMN id_carrinho SET NOT NULL;

ALTER TABLE votos
    ALTER COLUMN voto SET NOT NULL,
    ALTER COLUMN data_voto SET NOT NULL,
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN id_usuario SET NOT NULL,
    ALTER COLUMN id_promocao SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE;

ALTER TABLE usuarios
    ADD CONSTRAINT ck_usuarios_role
        CHECK (role IN ('USER', 'ADMIN'));

ALTER TABLE estabelecimentos
    ADD CONSTRAINT ck_estabelecimentos_tipo
        CHECK (tipo IN ('MERCADO', 'SUPERMERCADO', 'ATACADAO'));

ALTER TABLE produtos
    ADD CONSTRAINT ck_produtos_categoria
        CHECK (categoria IN ('BEBIDA', 'ALIMENTO', 'LIMPEZA', 'ELETRONICO', 'LAZER', 'FERRAMENTA', 'VESTIMENTA', 'OUTRO'));

ALTER TABLE promocoes
    ADD CONSTRAINT ck_promocoes_precos
        CHECK (preco_original > 0 AND preco_promocao > 0 AND preco_promocao <= preco_original),
    ADD CONSTRAINT ck_promocoes_periodo
        CHECK (data_fim >= data_inicio);

ALTER TABLE carrinhos
    ADD CONSTRAINT ck_carrinhos_preco_total
        CHECK (preco_total >= 0);

ALTER TABLE itens_carrinho
    ADD CONSTRAINT ck_itens_carrinho_valores
        CHECK (quantidade_item > 0 AND preco_item > 0 AND preco_total > 0);

ALTER TABLE votos
    ADD CONSTRAINT ck_votos_tipo
        CHECK (voto IN ('POSITIVO', 'NEGATIVO'));

DROP INDEX IF EXISTS idx_usuarios_email;

CREATE UNIQUE INDEX uq_usuarios_email_lower
    ON usuarios (LOWER(email));

CREATE UNIQUE INDEX uq_estabelecimentos_endereco
    ON estabelecimentos (id_endereco);

CREATE UNIQUE INDEX uq_votos_usuario_promocao_ativo
    ON votos (id_usuario, id_promocao)
    WHERE ativo = TRUE;

-- A V9 não inseria itens de carrinho, mas a sequência precisa estar
-- preparada para o primeiro item criado pela API.
SELECT setval(
    'itens_carrinho_id_seq',
    COALESCE((SELECT MAX(id) FROM itens_carrinho), 0) + 1,
    FALSE
);
