# Produtos e promoções no frontend

Use `Authorization: Bearer <token>` e envie o `id` retornado pelo login como `idUsuario`.

## Cadastrar uma promoção

1. Encontre o produto por nome em `GET /v1/produtos?nome=...`, pelo código em `GET /v1/produtos/por-codigo/{codigo}` ou pelo QR em `GET /v1/produtos/por-qr-code?conteudo=...`. No QR, envie o texto decodificado e codificado para URL.
2. Se a consulta retornar `LOCAL`, use o objeto `produto`. Se retornar `OPEN_FOOD_FACTS`, preencha o formulário com `sugestao` e deixe o usuário revisar. Se retornar `404`, ofereça cadastro manual.
3. Para cadastrar um produto, envie `POST /v1/produtos` com `nome`, `descricao`, `marca`, `codigoDeBarras` (texto), `categoria` e `idUsuario`. São opcionais `imagem` (URL), `qrCode` e o par `quantidadeEmbalagem` e `unidadeEmbalagem` (`G`, `ML` ou `UN)).
4. Selecione a loja em `GET /v1/estabelecimentos` e use o `id` dela em `POST /v1/promocoes`.

Em uma promoção com data final anunciada, envie os campos deste exemplo. Copie `produto.codigoDeBarras` para `codigoBarras`.

```json
{
  "codigoBarras": "7894900011517",
  "idEstabelecimento": 497,
  "idUsuario": 12,
  "precoOriginal": 5.99,
  "precoPromocao": 4.49,
  "dataInicio": "2026-12-01T08:00:00",
  "dataFim": "2026-12-10T23:59:59"
}
```

A **duração da promoção** é definida por `dataInicio` e `dataFim`. Use datas futuras no cadastro, no horário de Brasília, e termine na mesma data e hora do início ou depois. Os preços são números positivos em reais, com até duas casas decimais. O promocional pode ser igual ou menor que o original. O cadastro retorna `201` com o ID da promoção e grava o preço no histórico.

## Enquanto durar o estoque

Envie `"enquantoDurarEstoque": true` e omita `dataFim` se a loja não anunciou uma data final. Para uma oferta sem data final, informe em `dataInicio` quando viu a oferta, no horário de Brasília. A API retorna `ultimaConfirmacaoEm` e inclui a oferta no comparativo por 24 horas a partir dessa confirmação.

Qualquer usuário autenticado pode confirmar que a oferta continua disponível com `POST /v1/promocoes/{id}/confirmar-disponibilidade`, sem corpo. Isso renova as 24 horas. Na tela, mostre **Enquanto durar o estoque** e a última confirmação. Se também houver `dataFim`, o comparativo usa essa data como limite. O responsável pode retirar a oferta com `DELETE /v1/promocoes/{id}`.

## Pesquisar promoções

| Objetivo | Chamada |
|---|---|
| Buscar um produto por nome ou categoria | `GET /v1/produtos?nome=coca&categoria=REFRIGERANTES` |
| Ver ofertas vigentes do produto | `GET /v1/produtos/{idProduto}/comparativo-precos` |
| Ver promoções cadastradas | `GET /v1/promocoes?idProduto=42&idEstabelecimento=497` |
| Ver o histórico de preços | `GET /v1/produtos/{idProduto}/historico-precos` |

O comparativo retorna a menor oferta vigente por loja, em ordem crescente de preço. Promoções futuras ou expiradas podem aparecer na listagem geral. Em listas paginadas, leia os itens em `content`.

Veja a [lista de categorias aceitas](../README.md#categorias-de-supermercado). O filtro usa o código exato da categoria.
