-- Seed final mínimo do MVP.
-- Esta migration é deliberadamente destrutiva: remove os dados atuais de teste
-- e recria somente os registros solicitados para o ambiente inicial.

TRUNCATE TABLE
    desafios_verificacao,
    cadastros_pendentes,
    votos,
    itens_carrinho,
    carrinhos,
    promocoes,
    produtos,
    estabelecimentos,
    enderecos,
    usuarios
RESTART IDENTITY CASCADE;

INSERT INTO usuarios (
    id, nome, email, foto, senha, data_cadastro, ativo, role
)
VALUES (
    1,
    'Administrador NuPrecin',
    'admin@nuprecin.com.br',
    'https://nyuuqqxfdmeodpeoymgv.supabase.co/storage/v1/object/public/nuprecin-media/usuarios/1/avatar/53e9c8cd-8472-4bde-8101-e836919d76a5.jpg',
    '$2a$12$eFaR4ZY1O.BMk/mDmlSOj.YaTKt/Fcxw15zR6lRXfjkrUX9f0.KlK',
    CURRENT_TIMESTAMP,
    TRUE,
    'ADMIN'
);

INSERT INTO enderecos (
    id, logradouro, bairro, cidade, estado
)
VALUES (
    1,
    'Rua da Bahia, 1080',
    'Centro',
    'Belo Horizonte',
    'MG'
);

INSERT INTO produtos (
    id, nome, descricao, marca, codigo_de_barras, qr_code,
    imagem, categoria, ativo, id_usuario
)
VALUES (
    1,
    'Coca-Cola 350ml',
    'Refrigerante sabor cola em lata de 350ml',
    'Coca-Cola',
    '7894900011517',
    'QR-COCA-350-001',
    'https://nyuuqqxfdmeodpeoymgv.supabase.co/storage/v1/object/public/nuprecin-media/produtos/1/imagens/1487741d-3f77-459e-87e9-0f3222d8e6ea.png',
    'BEBIDA',
    TRUE,
    1
);

INSERT INTO estabelecimentos (
    id, nome, tipo, foto, telefone, ativo, id_endereco, id_usuario
)
VALUES (
    1,
    'Supermercado BH',
    'SUPERMERCADO',
    'https://nyuuqqxfdmeodpeoymgv.supabase.co/storage/v1/object/public/nuprecin-media/estabelecimentos/1/fotos/43bf8c4e-9a20-440b-bacf-934976b9c89e.jpg',
    '3132224400',
    TRUE,
    1,
    1
);

SELECT setval('usuarios_id_seq', 1, TRUE);
SELECT setval('enderecos_id_seq', 1, TRUE);
SELECT setval('produtos_id_seq', 1, TRUE);
SELECT setval('estabelecimentos_id_seq', 1, TRUE);
