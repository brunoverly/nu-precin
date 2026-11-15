# Contexto para o agente de integração do frontend NuPrecin

Sua tarefa é conectar as telas existentes ao backend do NuPrecin, respeitando o contrato abaixo. Examine a estrutura atual do frontend e reutilize os componentes, serviços HTTP e gerenciamento de sessão do projeto. Implemente o fluxo completo de busca, cadastro e exibição de ofertas, com estados de carregamento, vazio e erro.

## Referências e estado

- API de produção — https://nu-precin.onrender.com
- OpenAPI de produção — https://nu-precin.onrender.com/v3/api-docs
- Swagger — https://nu-precin.onrender.com/swagger-ui/index.html
- Coleção de exemplos no backend — `src/main/resources/postman/NuPrecin.postman_collection.json`
- Alterações publicadas no commit `6e867bf`. Saúde e novos contratos conferidos em produção.
- Consulte o OpenAPI para os detalhes de validação e rotas existentes.

## Autenticação

Use `Authorization: Bearer <token>` nas rotas de negócio. O login retorna `{id,nome,email,token,raioBuscaKm}`. Persista o ID da conta para preencher `idUsuario` e construir rotas de favoritos e preferências. Em uma operação de usuário comum, use seu próprio ID.

O cadastro inicial `POST /v1/auth/registro` retorna `202`. A confirmação `POST /v1/auth/registro/confirmar` retorna `201` com o ID definitivo, seguida do login para obter JWT. Sessões emitidas antes desta atualização precisam de novo login.

## Fluxo principal de publicação

1. O usuário encontra um produto por nome ou lê código de barras/QR com a câmera do frontend.
2. Se o produto existir no catálogo, exiba seus dados para confirmação.
3. Se houver sugestão externa, abra um formulário preenchido, permitindo revisar e completar. Salve o produto antes de publicar a promoção.
4. Se a consulta retornar `404`, ofereça cadastro manual. Se retornar `502`, ofereça nova tentativa ou cadastro manual.
5. O usuário seleciona a unidade do supermercado pelo nome e endereço.
6. O usuário informa preços e vigência ou marca “Enquanto durar o estoque”.
7. Publique a promoção e mostre confirmação. O backend registra o histórico automaticamente.

### Consulta e cadastro de produto

`GET /v1/produtos?nome=...&marca=...&categoria=...&page=0&size=20` pesquisa o catálogo. Os filtros são opcionais e combináveis.

`GET /v1/produtos/por-codigo/{codigo}` consulta por código de barras.

`GET /v1/produtos/por-qr-code?conteudo=...` recebe texto já decodificado pela câmera e codificado para URL. Aceita QR próprio cadastrado, GTIN numérico e GS1 Digital Link com identificador 01.

A resposta de consulta é `{origem,produto,sugestao}`. Quando `origem=LOCAL`, use `produto`. Quando `origem=OPEN_FOOD_FACTS`, use `sugestao` e faça cadastro após revisão. Campos da sugestão podem vir nulos. A leitura de QR de nota fiscal está pendente.

`POST /v1/produtos` recebe os campos abaixo e retorna `201` com o produto.

| Campo | Regra |
|---|---|
| `nome` | Obrigatório, até 250 caracteres |
| `descricao` | Obrigatório, até 500 caracteres |
| `marca` | Obrigatório, até 250 caracteres |
| `codigoDeBarras` | Obrigatório, texto de até 100 caracteres. Preserve zeros à esquerda. |
| `categoria` | Obrigatório, código da tabela de categorias |
| `idUsuario` | Obrigatório, ID da conta autenticada |
| `imagem` | URL opcional de até 500 caracteres |
| `qrCode` | Texto opcional, até 250 caracteres |
| `quantidadeEmbalagem` | Opcional, positivo, até três casas decimais |
| `unidadeEmbalagem` | `G`, `ML` ou `UN`, enviada junto com quantidade |

Para 2 L, envie quantidade 2000 e unidade ML. Para 500 g, envie 500 e G. O produto retornado também inclui `id` e `ativo`.

Para upload, use `POST /v1/produtos/{id}/imagem`, multipart com campo `file`, JPEG/PNG/WEBP, até 5 MB. Deixe o cliente HTTP gerar o boundary.

### Categorias para seleção e filtro

Cada produto tem uma categoria. Exiba o rótulo e envie o código. O filtro compara a categoria exata.

| Rótulo | Código |
|---|---|
| Carnes | CARNES |
| Peixes e frutos do mar | PEIXES_E_FRUTOS_DO_MAR |
| Hortifruti | HORTIFRUTI |
| Padaria | PADARIA |
| Frios e embutidos | FRIOS_E_EMBUTIDOS |
| Laticínios e ovos | LATICINIOS_E_OVOS |
| Congelados | CONGELADOS |
| Mercearia | MERCEARIA |
| Doces e snacks | DOCES_E_SNACKS |
| Cafés e chás | CAFES_E_CHAS |
| Cervejas | CERVEJAS |
| Vinhos e destilados | VINHOS_E_DESTILADOS |
| Refrigerantes | REFRIGERANTES |
| Sucos | SUCOS |
| Águas | AGUAS |
| Limpeza | LIMPEZA |
| Higiene pessoal | HIGIENE_PESSOAL |
| Pet shop | PET_SHOP |
| Eletrônicos | ELETRONICO |
| Lazer | LAZER |
| Ferramentas | FERRAMENTA |
| Vestimenta | VESTIMENTA |
| Outros | OUTRO |
| Alimentos em geral | ALIMENTO |
| Bebidas em geral | BEBIDA |

Produtos antigos em ALIMENTO ou BEBIDA precisam de reclassificação para aparecer nos filtros específicos.

### Seleção da loja e proximidade

`GET /v1/estabelecimentos` retorna lista paginada. Cada loja inclui `id,nome,foto,telefone,idEndereco,idUsuario,endereco` e tipo.

O objeto `endereco` contém `id,logradouro,numero,bairro,cidade,estado,cep,latitude,longitude`. Número, CEP e coordenadas podem ser nulos.

`GET /v1/estabelecimentos/proximos?latitude=...&longitude=...&raioKm=5` retorna um array ordenado pela distância, com `distanciaKm`. A distância é em linha reta. Lojas sem coordenadas ficam fora da busca. Se a localização for recusada, ofereça seleção pela listagem.

Omitir `raioKm` usa a preferência salva do usuário. Um raio explícito vale para a chamada. Consulte `GET /v1/usuarios/{id}/preferencias` e salve com `PATCH` na mesma rota, corpo `{"raioBuscaKm":3}`. As opções são 1, 3, 5 e 10, com padrão 5.

### Cadastro e edição de promoção

`POST /v1/promocoes` retorna `201`. Para edição, use `PUT /v1/promocoes/{id}` com o corpo completo.

```json
{
  "codigoBarras": "7894900011517",
  "idEstabelecimento": 497,
  "idUsuario": 12,
  "precoOriginal": 5.99,
  "precoPromocao": 4.49,
  "dataInicio": "2026-12-01T08:00:00",
  "dataFim": "2026-12-10T23:59:59",
  "enquantoDurarEstoque": false
}
```

Troque IDs e datas pelos valores reais. Os campos são obrigatórios, exceto `enquantoDurarEstoque`, que assume false, e `dataFim`, que pode ser omitido se `enquantoDurarEstoque=true`.

Use `produto.codigoDeBarras` para preencher `codigoBarras`. A resposta contém `id,idProduto,idEstabelecimento,idUsuario,precoOriginal,precoPromocao,dataCriacao,dataAtualizacao,dataInicio,dataFim,enquantoDurarEstoque,ultimaConfirmacaoEm`. Para nome, categoria e imagem, use o produto associado. Para nome e endereço da loja, use o estabelecimento associado.

Preços são números positivos em reais, com até duas casas decimais. O promocional pode ser igual ou menor que o original. Converta a entrada localizada “4,49” para o número JSON 4.49.

A vigência usa horário de Brasília no formato `AAAA-MM-DDTHH:mm:ss`, sem Z ou deslocamento. Preserve a hora local de Brasília ao montar os dados. No cadastro comum, início e fim precisam ser futuros, e o fim deve ser igual ou posterior ao início. Ao editar uma promoção vigente, preserve o início original para alterar seu preço.

### Enquanto durar o estoque

Exiba um checkbox com esse rótulo. Ao marcar, permita omitir a data final.

- Sem data final, a oferta precisa já ter começado. Use como início o horário em que o usuário viu a oferta. A API registra `ultimaConfirmacaoEm` no cadastro.
- A oferta entra no comparativo por 24 horas após a última confirmação. Ela continua acessível na listagem e no histórico quando essa janela vence.
- Exiba “Enquanto durar o estoque” e “Confirmada há X horas”.
- O botão “Ainda está disponível” chama `POST /v1/promocoes/{id}/confirmar-disponibilidade`, sem corpo, com autenticação. Retorna `200` com a promoção atualizada e renova as 24 horas. Qualquer usuário autenticado pode confirmar.
- Se também existir data final, a vigência anunciada define a presença no comparativo.
- O responsável pode retirar uma oferta com `DELETE /v1/promocoes/{id}`, retorno `204`.

## Pesquisa e detalhe

1. Busque produtos por nome e categoria no catálogo.
2. Ao selecionar um produto, carregue `GET /v1/produtos/{idProduto}/comparativo-precos`.
3. Exiba as ofertas retornadas na ordem da API. Cada item representa a menor oferta vigente de uma loja ativa.

O comparativo retorna um array com `idEstabelecimento,nomeEstabelecimento,idPromocao,precoOriginal,precoPromocao,inicioVigencia,fimVigencia,enquantoDurarEstoque,ultimaConfirmacaoEm`. Quando retorna `[]`, mostre “Sem ofertas disponíveis”.

Para filtrar o comparativo pelo raio do usuário, cruze os IDs das lojas com o resultado de estabelecimentos próximos.

`GET /v1/promocoes` aceita `idProduto,idEstabelecimento,idUsuario,page,size`. Essa listagem inclui promoções cadastradas que podem estar futuras ou expiradas. O filtro por categoria é feito no catálogo de produtos. A rota de promoções aceita filtros por IDs.

`GET /v1/promocoes/{id}` carrega os dados de uma oferta para o detalhe.

### Histórico e favoritos

`GET /v1/produtos/{idProduto}/historico-precos?page=0&size=20` retorna preços em `content`, do mais recente ao mais antigo. `idEstabelecimento` é um filtro opcional.

Cada observação tem `id,idProduto,idEstabelecimento,idUsuario,valor,observadoEm,registradoEm,origem,idPromocao,precoOriginal,inicioVigencia,fimVigencia,enquantoDurarEstoque`. Use `observadoEm` e `valor` no gráfico. Observações manuais têm campos promocionais nulos. O registro no histórico acontece ao cadastrar ou alterar os termos da oferta. A confirmação de disponibilidade renova a janela da oferta. As datas de observação e registro incluem fuso e podem ser convertidas para exibição local.

Produtos favoritos usam `/v1/usuarios/{id}/produtos-favoritos`. GET retorna um array de produtos ativos. PUT e DELETE no caminho com `/{idProduto}` adicionam e removem o favorito, sem corpo, com retorno 204. Repetir a operação é permitido.

Estabelecimentos favoritos usam o mesmo fluxo em `/v1/usuarios/{id}/favoritos/{idEstabelecimento}`, com GET da lista em `/v1/usuarios/{id}/favoritos`. Os favoritos pertencem ao usuário autenticado.

## Google e estados da interface

A integração Google depende de criar o OAuth Client ID. Quando configurada, envie `{"idToken":"..."}` para `POST /v1/auth/google`. O retorno segue o login por senha. O vínculo automático exige e-mail local confirmado sob autoridade do Google. Para uma conta que precise de vínculo manual, faça login no NuPrecin e envie o mesmo corpo para `POST /v1/auth/google/vincular` com JWT, retorno 204.

Trate carregamento, formulário inválido, resultados vazios e falha de rede. Em respostas 204, finalize a operação sem tentar ler JSON. Nas listas paginadas, use `content` e os metadados. Em erros, use `message` quando disponível e preserve o formulário. Consulte o contrato da rota para diferenciar 401 de sessão inválida de 401 de falha de autenticação Google.

Alertas de queda de preço seguem pendentes. Integre primeiro sessão e catálogo, depois publicação de promoção, comparativo, histórico e favoritos. Faça a integração Google quando o Client ID estiver configurado.

## Consulta comunitária de preços comuns

Implemente `loadRegularPrices()` com `GET /v1/precos/atuais`, seguindo o [contrato de preços comuns](frontend-precos-comuns.md). Essa rota usa a última observação manual por produto e loja ativos dentro de sete dias. `GET /v1/precos` fornece o histórico geral. O comparativo existente usa promoções vigentes.
