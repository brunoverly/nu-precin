INSERT INTO exemplos (id, nome, numero, data_horario_criado, exemplo_enum, ativo) VALUES
(1, 'Exemplo A1', 10, NOW() - INTERVAL '15 day', 'A', TRUE),
(2, 'Exemplo B1', 20, NOW() - INTERVAL '12 day', 'B', TRUE),
(3, 'Exemplo C1', 30, NOW() - INTERVAL '9 day', 'C', TRUE),
(4, 'Exemplo D1', 40, NOW() - INTERVAL '6 day', 'D', TRUE),
(5, 'Exemplo E1', 50, NOW() - INTERVAL '3 day', 'E', TRUE);

INSERT INTO usuarios (id, nome, email, foto, senha, data_cadastro, ativo) VALUES
(1, 'Ana Souza', 'ana.souza@nuprecin.com', 'https://images.example.com/users/ana.jpg', 'senha_teste_ana', NOW() - INTERVAL '60 day', TRUE),
(2, 'Bruno Lima', 'bruno.lima@nuprecin.com', 'https://images.example.com/users/bruno.jpg', 'senha_teste_bruno', NOW() - INTERVAL '55 day', TRUE),
(3, 'Carla Mendes', 'carla.mendes@nuprecin.com', 'https://images.example.com/users/carla.jpg', 'senha_teste_carla', NOW() - INTERVAL '48 day', TRUE),
(4, 'Diego Alves', 'diego.alves@nuprecin.com', 'https://images.example.com/users/diego.jpg', 'senha_teste_diego', NOW() - INTERVAL '35 day', TRUE),
(5, 'Eduarda Rocha', 'eduarda.rocha@nuprecin.com', 'https://images.example.com/users/eduarda.jpg', 'senha_teste_eduarda', NOW() - INTERVAL '20 day', TRUE);

INSERT INTO enderecos (id, logradouro, bairro, cidade, estado) VALUES
(1, 'Av. Paulista, 1578', 'Bela Vista', 'Sao Paulo', 'SP'),
(2, 'Rua da Bahia, 1080', 'Centro', 'Belo Horizonte', 'MG'),
(3, 'Av. Dom Luis, 500', 'Aldeota', 'Fortaleza', 'CE'),
(4, 'Rua das Flores, 230', 'Centro', 'Curitiba', 'PR'),
(5, 'Av. Joao Pessoa, 900', 'Centro Historico', 'Porto Alegre', 'RS');

INSERT INTO estabelecimentos (id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario) VALUES
(1, 'Supermercado Boa Compra', 'SUPERMERCADO', 'https://images.example.com/estab/boa-compra.jpg', '1133331100', TRUE, 1, 1),
(2, 'Mercado do Bairro', 'MERCADO', 'https://images.example.com/estab/bairro.jpg', '3132224400', TRUE, 2, 2),
(3, 'Atacadao Preco Baixo', 'ATACADAO', 'https://images.example.com/estab/atacadao.jpg', '8532117700', TRUE, 3, 3),
(4, 'Supermercado Serra Azul', 'SUPERMERCADO', 'https://images.example.com/estab/serra-azul.jpg', '4131002500', TRUE, 4, 4),
(5, 'Mercado Estacao Sul', 'MERCADO', 'https://images.example.com/estab/estacao-sul.jpg', '5133889900', TRUE, 5, 5);

INSERT INTO produtos (id, nome, descricao, marca, codigo_de_barras, qr_code, imagem, categoria, ativo, id_usuario) VALUES
(1, 'Arroz Tipo 1 5kg', 'Arroz branco tipo 1 pacote de 5kg', 'Tio Joao', '7896006716112', 'QR-ARROZ-001', 'https://images.example.com/prod/arroz.jpg', 'ALIMENTO', TRUE, 1),
(2, 'Feijao Carioca 1kg', 'Feijao carioca pacote de 1kg', 'Camil', '7896006711100', 'QR-FEIJAO-002', 'https://images.example.com/prod/feijao.jpg', 'ALIMENTO', TRUE, 2),
(3, 'Refrigerante Cola 2L', 'Refrigerante sabor cola garrafa 2 litros', 'Coca-Cola', '7894900011517', 'QR-COLA-003', 'https://images.example.com/prod/cola.jpg', 'BEBIDA', TRUE, 3),
(4, 'Detergente Neutro 500ml', 'Detergente liquido neutro 500ml', 'Ype', '7896098900109', 'QR-DETERG-004', 'https://images.example.com/prod/detergente.jpg', 'LIMPEZA', TRUE, 4),
(5, 'Chocolate ao Leite 90g', 'Chocolate ao leite em barra 90g', 'Lacta', '7622210712785', 'QR-CHOC-005', 'https://images.example.com/prod/chocolate.jpg', 'ALIMENTO', TRUE, 5);

INSERT INTO promocoes (id, preco_original, preco_promocao, data_criacao, data_atualizacao, data_inicio, data_fim, ativo, id_produto, id_estabelecimento, id_usuario) VALUES
(1, 29.90, 24.99, NOW() - INTERVAL '7 day', NOW() - INTERVAL '7 day', NOW() - INTERVAL '6 day', NOW() + INTERVAL '10 day', TRUE, 1, 1, 1),
(2, 8.49, 6.99, NOW() - INTERVAL '6 day', NOW() - INTERVAL '6 day', NOW() - INTERVAL '5 day', NOW() + INTERVAL '8 day', TRUE, 2, 2, 2),
(3, 11.99, 9.49, NOW() - INTERVAL '5 day', NOW() - INTERVAL '4 day', NOW() - INTERVAL '4 day', NOW() + INTERVAL '7 day', TRUE, 3, 3, 3),
(4, 3.79, 2.99, NOW() - INTERVAL '4 day', NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day', NOW() + INTERVAL '6 day', TRUE, 4, 4, 4),
(5, 7.99, 5.99, NOW() - INTERVAL '3 day', NOW() - INTERVAL '2 day', NOW() - INTERVAL '2 day', NOW() + INTERVAL '5 day', TRUE, 5, 5, 5);

INSERT INTO carrinhos (id, nome, data_cadastro, ativo, id_usuario) VALUES
(1, 'Compra da Semana - Ana', NOW() - INTERVAL '5 day', TRUE, 1),
(2, 'Reposicao Mensal - Bruno', NOW() - INTERVAL '4 day', TRUE, 2),
(3, 'Churrasco de Sabado - Carla', NOW() - INTERVAL '3 day', TRUE, 3),
(4, 'Limpeza de Casa - Diego', NOW() - INTERVAL '2 day', TRUE, 4),
(5, 'Snacks da Empresa - Eduarda', NOW() - INTERVAL '1 day', TRUE, 5);

INSERT INTO itens_carrinho (id, quantidade, ativo, id_carrinho, id_produto) VALUES
(1, 2, TRUE, 1, 1),
(2, 3, TRUE, 2, 2),
(3, 4, TRUE, 3, 3),
(4, 5, TRUE, 4, 4),
(5, 6, TRUE, 5, 5);

INSERT INTO votos (id, voto, data_voto, ativo, id_usuario, id_promocao) VALUES
(1, 'POSITIVO', NOW() - INTERVAL '5 day', TRUE, 2, 1),
(2, 'POSITIVO', NOW() - INTERVAL '4 day', TRUE, 3, 1),
(3, 'NEGATIVO', NOW() - INTERVAL '3 day', TRUE, 1, 3),
(4, 'POSITIVO', NOW() - INTERVAL '2 day', TRUE, 5, 2),
(5, 'POSITIVO', NOW() - INTERVAL '1 day', TRUE, 4, 5);

SELECT setval('exemplos_id_seq', (SELECT MAX(id) FROM exemplos));
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('enderecos_id_seq', (SELECT MAX(id) FROM enderecos));
SELECT setval('estabelecimentos_id_seq', (SELECT MAX(id) FROM estabelecimentos));
SELECT setval('produtos_id_seq', (SELECT MAX(id) FROM produtos));
SELECT setval('promocoes_id_seq', (SELECT MAX(id) FROM promocoes));
SELECT setval('carrinhos_id_seq', (SELECT MAX(id) FROM carrinhos));
SELECT setval('itens_carrinho_id_seq', (SELECT MAX(id) FROM itens_carrinho));
SELECT setval('votos_id_seq', (SELECT MAX(id) FROM votos));
