-- V9: insert sample data (idempotent) - excludes exemplos domain

-- Usuarios
INSERT INTO usuarios (id, nome, email, foto, senha, data_cadastro, ativo)
SELECT 1, 'Ana Souza', 'ana.souza@nuprecin.com', 'https://images.example.com/users/ana.jpg', 'senha_teste_ana', NOW() - INTERVAL '60 day', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE id = 1);

INSERT INTO usuarios (id, nome, email, foto, senha, data_cadastro, ativo)
SELECT 2, 'Bruno Lima', 'bruno.lima@nuprecin.com', 'https://images.example.com/users/bruno.jpg', 'senha_teste_bruno', NOW() - INTERVAL '55 day', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE id = 2);

INSERT INTO usuarios (id, nome, email, foto, senha, data_cadastro, ativo)
SELECT 3, 'Carla Mendes', 'carla.mendes@nuprecin.com', 'https://images.example.com/users/carla.jpg', 'senha_teste_carla', NOW() - INTERVAL '48 day', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE id = 3);

INSERT INTO usuarios (id, nome, email, foto, senha, data_cadastro, ativo)
SELECT 4, 'Diego Alves', 'diego.alves@nuprecin.com', 'https://images.example.com/users/diego.jpg', 'senha_teste_diego', NOW() - INTERVAL '35 day', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE id = 4);

INSERT INTO usuarios (id, nome, email, foto, senha, data_cadastro, ativo)
SELECT 5, 'Eduarda Rocha', 'eduarda.rocha@nuprecin.com', 'https://images.example.com/users/eduarda.jpg', 'senha_teste_eduarda', NOW() - INTERVAL '20 day', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE id = 5);

-- Enderecos
INSERT INTO enderecos (id, logradouro, bairro, cidade, estado)
SELECT 1, 'Av. Paulista, 1578', 'Bela Vista', 'Sao Paulo', 'SP'
WHERE NOT EXISTS (SELECT 1 FROM enderecos WHERE id = 1);

INSERT INTO enderecos (id, logradouro, bairro, cidade, estado)
SELECT 2, 'Rua da Bahia, 1080', 'Centro', 'Belo Horizonte', 'MG'
WHERE NOT EXISTS (SELECT 1 FROM enderecos WHERE id = 2);

INSERT INTO enderecos (id, logradouro, bairro, cidade, estado)
SELECT 3, 'Av. Dom Luis, 500', 'Aldeota', 'Fortaleza', 'CE'
WHERE NOT EXISTS (SELECT 1 FROM enderecos WHERE id = 3);

INSERT INTO enderecos (id, logradouro, bairro, cidade, estado)
SELECT 4, 'Rua das Flores, 230', 'Centro', 'Curitiba', 'PR'
WHERE NOT EXISTS (SELECT 1 FROM enderecos WHERE id = 4);

INSERT INTO enderecos (id, logradouro, bairro, cidade, estado)
SELECT 5, 'Av. Joao Pessoa, 900', 'Centro Historico', 'Porto Alegre', 'RS'
WHERE NOT EXISTS (SELECT 1 FROM enderecos WHERE id = 5);

-- Estabelecimentos
INSERT INTO estabelecimentos (id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario)
SELECT 1, 'Supermercado Boa Compra', 'SUPERMERCADO', 'https://images.example.com/estab/boa-compra.jpg', '1133331100', TRUE, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM estabelecimentos WHERE id = 1);

INSERT INTO estabelecimentos (id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario)
SELECT 2, 'Mercado do Bairro', 'MERCADO', 'https://images.example.com/estab/bairro.jpg', '3132224400', TRUE, 2, 2
WHERE NOT EXISTS (SELECT 1 FROM estabelecimentos WHERE id = 2);

INSERT INTO estabelecimentos (id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario)
SELECT 3, 'Atacadao Preco Baixo', 'ATACADAO', 'https://images.example.com/estab/atacadao.jpg', '8532117700', TRUE, 3, 3
WHERE NOT EXISTS (SELECT 1 FROM estabelecimentos WHERE id = 3);

INSERT INTO estabelecimentos (id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario)
SELECT 4, 'Supermercado Serra Azul', 'SUPERMERCADO', 'https://images.example.com/estab/serra-azul.jpg', '4131002500', TRUE, 4, 4
WHERE NOT EXISTS (SELECT 1 FROM estabelecimentos WHERE id = 4);

INSERT INTO estabelecimentos (id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario)
SELECT 5, 'Mercado Estacao Sul', 'MERCADO', 'https://images.example.com/estab/estacao-sul.jpg', '5133889900', TRUE, 5, 5
WHERE NOT EXISTS (SELECT 1 FROM estabelecimentos WHERE id = 5);

-- Produtos
INSERT INTO produtos (id, nome, descricao, marca, codigo_de_barras, qr_code, imagem, categoria, ativo, id_usuario)
SELECT 1, 'Arroz Tipo 1 5kg', 'Arroz branco tipo 1 pacote de 5kg', 'Tio Joao', '7896006716112', 'QR-ARROZ-001', 'https://images.example.com/prod/arroz.jpg', 'ALIMENTO', TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE id = 1);

INSERT INTO produtos (id, nome, descricao, marca, codigo_de_barras, qr_code, imagem, categoria, ativo, id_usuario)
SELECT 2, 'Feijao Carioca 1kg', 'Feijao carioca pacote de 1kg', 'Camil', '7896006711100', 'QR-FEIJAO-002', 'https://images.example.com/prod/feijao.jpg', 'ALIMENTO', TRUE, 2
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE id = 2);

INSERT INTO produtos (id, nome, descricao, marca, codigo_de_barras, qr_code, imagem, categoria, ativo, id_usuario)
SELECT 3, 'Refrigerante Cola 2L', 'Refrigerante sabor cola garrafa 2 litros', 'Coca-Cola', '7894900011517', 'QR-COLA-003', 'https://images.example.com/prod/cola.jpg', 'BEBIDA', TRUE, 3
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE id = 3);

INSERT INTO produtos (id, nome, descricao, marca, codigo_de_barras, qr_code, imagem, categoria, ativo, id_usuario)
SELECT 4, 'Detergente Neutro 500ml', 'Detergente liquido neutro 500ml', 'Ype', '7896098900109', 'QR-DETERG-004', 'https://images.example.com/prod/detergente.jpg', 'LIMPEZA', TRUE, 4
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE id = 4);

INSERT INTO produtos (id, nome, descricao, marca, codigo_de_barras, qr_code, imagem, categoria, ativo, id_usuario)
SELECT 5, 'Chocolate ao Leite 90g', 'Chocolate ao leite em barra 90g', 'Lacta', '7622210712785', 'QR-CHOC-005', 'https://images.example.com/prod/chocolate.jpg', 'ALIMENTO', TRUE, 5
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE id = 5);

-- Promocoes
INSERT INTO promocoes (id, preco_original, preco_promocao, data_criacao, data_atualizacao, data_inicio, data_fim, ativo, id_produto, id_estabelecimento, id_usuario)
SELECT 1, 29.90, 24.99, NOW() - INTERVAL '7 day', NOW() - INTERVAL '7 day', NOW() - INTERVAL '6 day', NOW() + INTERVAL '10 day', TRUE, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM promocoes WHERE id = 1);

INSERT INTO promocoes (id, preco_original, preco_promocao, data_criacao, data_atualizacao, data_inicio, data_fim, ativo, id_produto, id_estabelecimento, id_usuario)
SELECT 2, 8.49, 6.99, NOW() - INTERVAL '6 day', NOW() - INTERVAL '6 day', NOW() - INTERVAL '5 day', NOW() + INTERVAL '8 day', TRUE, 2, 2, 2
WHERE NOT EXISTS (SELECT 1 FROM promocoes WHERE id = 2);

INSERT INTO promocoes (id, preco_original, preco_promocao, data_criacao, data_atualizacao, data_inicio, data_fim, ativo, id_produto, id_estabelecimento, id_usuario)
SELECT 3, 11.99, 9.49, NOW() - INTERVAL '5 day', NOW() - INTERVAL '4 day', NOW() - INTERVAL '4 day', NOW() + INTERVAL '7 day', TRUE, 3, 3, 3
WHERE NOT EXISTS (SELECT 1 FROM promocoes WHERE id = 3);

INSERT INTO promocoes (id, preco_original, preco_promocao, data_criacao, data_atualizacao, data_inicio, data_fim, ativo, id_produto, id_estabelecimento, id_usuario)
SELECT 4, 3.79, 2.99, NOW() - INTERVAL '4 day', NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day', NOW() + INTERVAL '6 day', TRUE, 4, 4, 4
WHERE NOT EXISTS (SELECT 1 FROM promocoes WHERE id = 4);

INSERT INTO promocoes (id, preco_original, preco_promocao, data_criacao, data_atualizacao, data_inicio, data_fim, ativo, id_produto, id_estabelecimento, id_usuario)
SELECT 5, 7.99, 5.99, NOW() - INTERVAL '3 day', NOW() - INTERVAL '2 day', NOW() - INTERVAL '2 day', NOW() + INTERVAL '5 day', TRUE, 5, 5, 5
WHERE NOT EXISTS (SELECT 1 FROM promocoes WHERE id = 5);

-- Carrinhos (insert with minimal columns consistent with current schema)
INSERT INTO carrinhos (id, data_cadastro, ativo, id_usuario, preco_total)
SELECT 1, NOW() - INTERVAL '5 day', TRUE, 1, COALESCE((SELECT SUM(preco_total) FROM itens_carrinho WHERE id_carrinho = 1), 0)
WHERE NOT EXISTS (SELECT 1 FROM carrinhos WHERE id = 1);

INSERT INTO carrinhos (id, data_cadastro, ativo, id_usuario, preco_total)
SELECT 2, NOW() - INTERVAL '4 day', TRUE, 2, COALESCE((SELECT SUM(preco_total) FROM itens_carrinho WHERE id_carrinho = 2), 0)
WHERE NOT EXISTS (SELECT 1 FROM carrinhos WHERE id = 2);

INSERT INTO carrinhos (id, data_cadastro, ativo, id_usuario, preco_total)
SELECT 3, NOW() - INTERVAL '3 day', TRUE, 3, COALESCE((SELECT SUM(preco_total) FROM itens_carrinho WHERE id_carrinho = 3), 0)
WHERE NOT EXISTS (SELECT 1 FROM carrinhos WHERE id = 3);

INSERT INTO carrinhos (id, data_cadastro, ativo, id_usuario, preco_total)
SELECT 4, NOW() - INTERVAL '2 day', TRUE, 4, COALESCE((SELECT SUM(preco_total) FROM itens_carrinho WHERE id_carrinho = 4), 0)
WHERE NOT EXISTS (SELECT 1 FROM carrinhos WHERE id = 4);

INSERT INTO carrinhos (id, data_cadastro, ativo, id_usuario, preco_total)
SELECT 5, NOW() - INTERVAL '1 day', TRUE, 5, COALESCE((SELECT SUM(preco_total) FROM itens_carrinho WHERE id_carrinho = 5), 0)
WHERE NOT EXISTS (SELECT 1 FROM carrinhos WHERE id = 5);

-- Votos
INSERT INTO votos (id, voto, data_voto, ativo, id_usuario, id_promocao)
SELECT 1, 'POSITIVO', NOW() - INTERVAL '5 day', TRUE, 2, 1
WHERE NOT EXISTS (SELECT 1 FROM votos WHERE id = 1);

INSERT INTO votos (id, voto, data_voto, ativo, id_usuario, id_promocao)
SELECT 2, 'POSITIVO', NOW() - INTERVAL '4 day', TRUE, 3, 1
WHERE NOT EXISTS (SELECT 1 FROM votos WHERE id = 2);

INSERT INTO votos (id, voto, data_voto, ativo, id_usuario, id_promocao)
SELECT 3, 'NEGATIVO', NOW() - INTERVAL '3 day', TRUE, 1, 3
WHERE NOT EXISTS (SELECT 1 FROM votos WHERE id = 3);

INSERT INTO votos (id, voto, data_voto, ativo, id_usuario, id_promocao)
SELECT 4, 'POSITIVO', NOW() - INTERVAL '2 day', TRUE, 5, 2
WHERE NOT EXISTS (SELECT 1 FROM votos WHERE id = 4);

INSERT INTO votos (id, voto, data_voto, ativo, id_usuario, id_promocao)
SELECT 5, 'POSITIVO', NOW() - INTERVAL '1 day', TRUE, 4, 5
WHERE NOT EXISTS (SELECT 1 FROM votos WHERE id = 5);

-- Reset sequences to max(id)
SELECT setval('usuarios_id_seq', (SELECT COALESCE(MAX(id), 0) FROM usuarios));
SELECT setval('enderecos_id_seq', (SELECT COALESCE(MAX(id), 0) FROM enderecos));
SELECT setval('estabelecimentos_id_seq', (SELECT COALESCE(MAX(id), 0) FROM estabelecimentos));
SELECT setval('produtos_id_seq', (SELECT COALESCE(MAX(id), 0) FROM produtos));
SELECT setval('promocoes_id_seq', (SELECT COALESCE(MAX(id), 0) FROM promocoes));
SELECT setval('carrinhos_id_seq', (SELECT COALESCE(MAX(id), 0) FROM carrinhos));
SELECT setval('votos_id_seq', (SELECT COALESCE(MAX(id), 0) FROM votos));
