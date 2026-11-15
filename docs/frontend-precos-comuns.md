# Integração de preços comuns

Todas as chamadas exigem `Authorization: Bearer <token>`. As consultas são comunitárias e incluem contribuições de outros usuários.

## Qual rota usar

| Objetivo | Endpoint |
|---|---|
| Registrar um preço comum | `POST /v1/precos` |
| Alimentar o catálogo com preços de referência | `GET /v1/precos/atuais` |
| Consultar contribuições e histórico geral | `GET /v1/precos` |
| Consultar histórico de um produto | `GET /v1/produtos/{idProduto}/historico-precos` |
| Comparar promoções vigentes | `GET /v1/produtos/{id}/comparativo-precos` |

## Preço de referência

`GET /v1/precos/atuais?page=0&size=100` seleciona uma observação manual por produto e estabelecimento.

- Seleciona a maior `observadoEm`, com desempate pelo maior `id`.
- A observação precisa ter ocorrido nos últimos sete dias, equivalentes a 168 horas, e não pode estar no futuro.
- O momento de envio (`registradoEm`) não substitui a data da observação nessa seleção.
- Exclui produtos e estabelecimentos inativos.
- `idUsuario` filtra o autor do registro vencedor. Se outro usuário forneceu a referência mais recente, a consulta filtrada não retorna a contribuição anterior.
- O registro continua no histórico após vencer o prazo de referência.
- A ausência de referência significa ausência de observação recente. A interface pode mostrar “Preço comum não informado”.

## Filtros e paginação

As duas consultas gerais aceitam filtros opcionais por igualdade.

| Parâmetro | Regra |
|---|---|
| `idProduto` | ID positivo |
| `idEstabelecimento` | ID positivo |
| `idUsuario` | ID positivo do autor |
| `origem` | Histórico aceita `MANUAL` ou `PROMOCAO`. Referência aceita apenas `MANUAL`, já usado quando omitido. |
| `page` | Inteiro a partir de 0, padrão 0 |
| `size` | Inteiro entre 1 e 100, padrão 20 |

Filtros são combináveis. IDs inexistentes retornam lista vazia. A ordenação é fixa por `observadoEm DESC, id DESC`; `sort` é ignorado. O histórico geral inclui registros de produtos e lojas inativos e aceita origens diferentes.

As páginas usam paginação por deslocamento. A ordem é determinística, mas novas contribuições entre requisições podem deslocar itens entre páginas. Ao atualizar o catálogo, reinicie a consulta na página 0 e identifique registros pelo ID.

## Exemplo de retorno

Chamada `GET /v1/precos/atuais?idProduto=2&page=0&size=100`.

Exemplo ilustrativo, com uma observação considerada recente no momento da consulta.

```json
{
  "content": [
    {
      "id": 123,
      "idProduto": 2,
      "idEstabelecimento": 497,
      "idUsuario": 12,
      "autor": {
        "id": 12,
        "nome": "Bruno",
        "foto": "https://example.com/perfil.jpg"
      },
      "valor": 8.49,
      "observadoEm": "2026-11-15T15:00:00Z",
      "registradoEm": "2026-11-15T15:01:00Z",
      "origem": "MANUAL",
      "idPromocao": null,
      "precoOriginal": null,
      "inicioVigencia": null,
      "fimVigencia": null,
      "enquantoDurarEstoque": false
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 100,
    "sort": { "empty": true, "sorted": false, "unsorted": true },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 100,
  "number": 0,
  "sort": { "empty": true, "sorted": false, "unsorted": true },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

`GET /v1/precos` usa o mesmo formato. Pode retornar várias observações para o mesmo produto e loja. Sem resultados, ambas retornam `200`, `content: []`, `totalElements: 0`, `totalPages: 0` e `empty: true`.

## Origens e promoções

- `MANUAL` identifica preço comum registrado pelo POST. O autor é obtido da sessão.
- `PROMOCAO` identifica um registro histórico gerado pelo cadastro ou alteração de uma promoção. Os campos promocionais guardam uma cópia daquela versão.
- Importação de cupom fiscal segue pendente. O valor `CUPOM` retorna `400` nestas consultas.

Carregue `/v1/precos/atuais` para o preço comum e consulte o comparativo para promoções. Associe por `idProduto` e `idEstabelecimento`, apresentando preço de referência e promoção com sua respectiva identificação. A oferta vigente pode ser exibida com destaque, preservando o significado do preço comum. A consulta de referência exclui registros `PROMOCAO`; o comparativo permanece restrito a promoções vigentes.

## Erros e sequência de integração

- `400` para origem, ID, tipo de parâmetro ou paginação inválidos.
- `401` para autenticação ausente ou inválida.
- Erros seguem `ErrorResponse`, com `timestamp,status,error,message,path`.

Implemente `loadRegularPrices()` usando `GET /v1/precos/atuais` e percorra as páginas até `last: true`. Depois de cadastrar um preço, recarregue a referência do par produto/loja, pois uma contribuição com data antiga pode não substituir o valor atual.

Os horários `observadoEm` e `registradoEm` incluem fuso. Preserve isso ao enviar e ler esses campos. Os preços comuns podem ser recuperados em nova sessão e em outro dispositivo autenticado.


## Autor público

Cada registro retornado por GET /v1/precos, GET /v1/precos/atuais, pelo histórico por produto e pelo POST de preço inclui `autor: {id, nome, foto}`. O ID corresponde a `idUsuario`. A foto pode ser nula. Use o nome e a foto retornados para representar o contribuinte, com a inicial como alternativa à foto.

Contas inativas ou indisponíveis retornam `{"id":12,"nome":"Usuário indisponível","foto":null}`. Nome e foto refletem o perfil atual. Os autores são consultados em lote por página, somente com seus campos públicos. A paginação e os demais campos mantêm o contrato existente.
