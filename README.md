<div align="center">

# 🛒 NuPrecin Backend

API colaborativa para cadastro, consulta e comparação de preços e promoções em estabelecimentos comerciais.

![Java](https://img.shields.io/badge/Java-17-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%234169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-%23CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI%2FSwagger-%2385EA2D?style=for-the-badge&logo=swagger&logoColor=black)

</div>

## Visão geral

O NuPrecin é um backend REST para explorar estabelecimentos e produtos, registrar promoções, montar um carrinho e colaborar com avaliações. A aplicação usa JWT, PostgreSQL, migrations Flyway e armazenamento de imagens no Supabase Storage.

O backend atual já oferece autenticação, cadastro confirmado por e-mail, recuperação de senha, CRUDs principais, imagens, promoções, carrinho, votos e documentação OpenAPI.

## Stack

- Java 17
- Spring Boot 3.5.13
- Spring Web, Spring Data JPA e Jakarta Validation
- Spring Security com JWT e BCrypt
- PostgreSQL e Flyway
- MapStruct e Lombok
- Springdoc OpenAPI/Swagger
- Brevo Transactional Email API
- Supabase Storage
- Maven Wrapper

## Executar e testar

Comandos principais:

```bash
./mvnw test
./mvnw -DskipTests compile
./mvnw clean verify
```

Os testes usam o perfil `test` com banco H2 em memória e não dependem do banco remoto.

Para executar localmente a aplicação, crie o arquivo ignorado pelo Git:

```text
src/main/resources/application-dev.yaml
```

Use `application-example.yaml` como referência. Nunca publique credenciais reais, chaves JWT, API keys ou a service role key do Supabase.

## Documentação OpenAPI

Com a aplicação em execução:

- Swagger UI local: `http://localhost:8080/swagger-ui.html`
- OpenAPI local: `http://localhost:8080/v3/api-docs`
- Swagger UI produção: `https://nu-precin.onrender.com/swagger-ui.html`
- OpenAPI produção: `https://nu-precin.onrender.com/v3/api-docs`

As páginas de documentação são públicas. As rotas de negócio continuam protegidas por JWT.

No Swagger, use **Authorize** e informe:

```text
Bearer <jwt retornado pelo login>
```

## Configuração de produção

As variáveis são configuradas no ambiente de execução, nunca no código:

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
KEY_TOKEN_SECRET
KEY_TOKEN_ISSUER
EMAIL_CONFIRMATION_SECRET
BREVO_API_KEY
BREVO_FROM_EMAIL
GEOAPIFY_API_KEY
SUPABASE_URL
SUPABASE_STORAGE_BUCKET
SUPABASE_SERVICE_ROLE_KEY
CORS_ALLOWED_ORIGINS
```

O Render executa as migrations Flyway automaticamente durante a inicialização. A service role key do Supabase deve permanecer somente no backend.

## Autenticação e conta

### Login

```http
POST /v1/auth/login
```

```json
{
  "email": "usuario@example.com",
  "senha": "123456"
}
```

Retorna `200 OK` com `id` do usuário, `nome`, `email`, `token` e `raioBuscaKm` salvo.

### Cadastro com confirmação por e-mail

1. Inicie o cadastro:

```http
POST /v1/auth/registro
```

```json
{
  "nome": "Maria Oliveira",
  "email": "maria@example.com",
  "foto": null,
  "senha": "123456"
}
```

Retorna `202 Accepted` e envia um código de quatro dígitos.

2. Confirme o código:

```http
POST /v1/auth/registro/confirmar
```

```json
{
  "email": "maria@example.com",
  "codigo": "4821"
}
```

Retorna `201 Created` com `id` do usuário, `nome`, `email`, `foto`, `dataCadastro` e `raioBuscaKm` (padrão 5). Depois, faça login para obter o JWT.

3. Reenvie o código, se necessário:

```http
POST /v1/auth/registro/reenviar
```

### Recuperação de senha

1. Solicite o código:

```http
POST /v1/auth/senha/esqueci
```

```json
{
  "email": "usuario@example.com"
}
```

2. Redefina a senha:

```http
POST /v1/auth/senha/resetar
```

```json
{
  "email": "usuario@example.com",
  "codigo": "4821",
  "novaSenha": "654321"
}
```

Retorna `204 No Content` quando o código for válido.

### Atualização do próprio usuário

```http
PUT /v1/usuarios/{id}
Authorization: Bearer <jwt>
```

```json
{
  "nome": "Maria Oliveira",
  "email": "maria@example.com",
  "foto": null,
  "senhaAtual": "123456",
  "novaSenha": "654321"
}
```

A `senhaAtual` precisa estar correta. Se `foto` for omitida, a foto atual é preservada.

### Preferência de distância

A preferência pertence ao usuário e aceita somente **1, 3, 5 ou 10 km**, enviados como número inteiro. Usuários novos e já existentes recebem **5 km** por padrão, pela migration `V16__add_user_search_radius.sql`.

```http
PATCH /v1/usuarios/{id}/preferencias
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "raioBuscaKm": 3
}
```

Retorna `200 OK` com o mesmo objeto. Essa chamada altera somente a preferência, sem exigir senha atual ou troca de senha. Valores fora das opções, decimais, textos, valores nulos ou campo ausente retornam `400 Bad Request`.

Para recuperar:

```http
GET /v1/usuarios/{id}/preferencias
Authorization: Bearer <jwt>
```

O campo `raioBuscaKm` também aparece no login e nas respostas de perfil de usuário, incluindo `GET /v1/usuarios/{id}`. O cadastro inicial pendente continua retornando somente e-mail e mensagem.

Na busca `GET /v1/estabelecimentos/proximos`, omitir `raioKm` usa a preferência salva do usuário autenticado. Enviar `raioKm` explicitamente vale apenas para aquela consulta e não altera a preferência.

### Estabelecimentos favoritos

Favoritos também pertencem ao usuário. A migration `V17__create_user_establishment_favorites.sql` cria o vínculo entre usuário e estabelecimento, impedindo duplicatas, inclusive em requisições concorrentes.

```http
PUT /v1/usuarios/{id}/favoritos/{idEstabelecimento}
Authorization: Bearer <jwt>
```

Não recebe corpo e retorna `204 No Content`, mesmo se já estiver favoritado. Aceita somente estabelecimento ativo; inexistente ou inativo retorna `404 Not Found`.

```http
GET /v1/usuarios/{id}/favoritos
Authorization: Bearer <jwt>
```

Retorna `200 OK` com um **array direto, sem paginação**, de estabelecimentos favoritos ativos, ordenado por nome e ID. Cada item contém os mesmos campos de `GET /v1/estabelecimentos/{id}`, incluindo `id`, `tipo`, `nome`, `foto`, `telefone`, `idEndereco`, `idUsuario` do proprietário do estabelecimento e o objeto `endereco` completo. Quando não há favoritos, retorna `[]`. Estabelecimentos desativados ficam fora dessa lista.

```http
DELETE /v1/usuarios/{id}/favoritos/{idEstabelecimento}
Authorization: Bearer <jwt>
```

Não recebe corpo e retorna `204 No Content`, mesmo se o vínculo não existir. Remove somente o favorito; o estabelecimento continua cadastrado. Também permite remover um favorito cujo estabelecimento foi desativado.

Preferências e favoritos exigem JWT. Usuários comuns acessam apenas os próprios dados; ADMIN pode gerenciar os de outros usuários. O frontend deve usar o `id` retornado no login como `{id}` e o ID da loja como `{idEstabelecimento}`. É possível favoritar uma loja pertencente a outro usuário; o vínculo é pessoal e não interfere nos favoritos dos demais.

### Produtos favoritos

As chamadas exigem JWT e são acessíveis ao próprio usuário ou ADMIN.

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/v1/usuarios/{id}/produtos-favoritos` | `200` com um array de produtos ativos, ordenados por nome e ID. Retorna `[]` quando não há favoritos. |
| PUT | `/v1/usuarios/{id}/produtos-favoritos/{idProduto}` | `204` sem corpo, mesmo se o produto já estiver favoritado. Produto inexistente ou inativo retorna `404`. |
| DELETE | `/v1/usuarios/{id}/produtos-favoritos/{idProduto}` | `204` sem corpo, mesmo se o vínculo já tiver sido removido. |

O PUT e o DELETE dispensam corpo na requisição. Use o ID do produto cadastrado, retornado no catálogo. A lista contém os mesmos campos da consulta de produto, incluindo a medida da embalagem. A V20 garante um único vínculo por usuário e produto.

## Imagens

A imagem é opcional nos cadastros. É possível informar uma URL no JSON ou enviar um arquivo depois.

### Upload de arquivo

Todos os uploads usam:

```text
Content-Type: multipart/form-data
campo: file
```

Não defina manualmente o header `Content-Type` no Postman ou frontend; o cliente deve gerar o `boundary` do multipart.

Formatos aceitos: JPEG, PNG e WEBP. Limite: 5 MB.

Endpoints:

```text
POST /v1/usuarios/{id}/foto
POST /v1/produtos/{id}/imagem
POST /v1/estabelecimentos/{id}/foto
```

Depois do upload, a API salva a URL pública no banco e devolve o recurso atualizado:

- Usuário e estabelecimento: campo `foto`.
- Produto: campo `imagem`.

Se uma URL for enviada no JSON, ela também é salva diretamente. Em atualizações, omitir o campo preserva a URL atual.

## Endpoints

As rotas de negócio e `POST /v1/auth/google/vincular` exigem JWT. As demais rotas de autenticação, `/actuator/health` e a documentação OpenAPI são públicas.

### Usuários

```text
POST   /v1/usuarios             Criação direta; somente ADMIN
GET    /v1/usuarios/{id}        Consulta o próprio usuário ou qualquer usuário para ADMIN
GET    /v1/usuarios             Lista; somente ADMIN
PUT    /v1/usuarios/{id}        Atualiza perfil e senha atual/nova
DELETE /v1/usuarios/{id}        Soft delete
POST   /v1/usuarios/{id}/foto   Upload de foto
GET    /v1/usuarios/{id}/preferencias
PATCH  /v1/usuarios/{id}/preferencias
GET    /v1/usuarios/{id}/favoritos
PUT    /v1/usuarios/{id}/favoritos/{idEstabelecimento}
DELETE /v1/usuarios/{id}/favoritos/{idEstabelecimento}
```

### Estabelecimentos

```text
POST   /v1/estabelecimentos
GET    /v1/estabelecimentos/{id}
GET    /v1/estabelecimentos?nome=&tipo=&idUsuario=
PUT    /v1/estabelecimentos/{id}
DELETE /v1/estabelecimentos/{id}
POST   /v1/estabelecimentos/{id}/foto
POST   /v1/estabelecimentos/{id}/geocodificar
GET    /v1/estabelecimentos/proximos?latitude=&longitude=&raioKm=5
```

Na criação/atualização, informe `idEndereco` ou `endereco`, nunca os dois. O objeto `endereco` recebe `logradouro`, `bairro`, `cidade` e `estado` obrigatórios, além de `numero` e `cep` opcionais. O CEP deve conter oito dígitos sem pontuação. Os clientes existentes podem continuar enviando o número junto com o logradouro.

Todas as respostas que retornam estabelecimentos (listagem paginada, busca por ID, cadastro, atualização e upload de foto) incluem `endereco`, mantendo `idEndereco` para compatibilidade:

```json
{
  "idEndereco": 1,
  "endereco": {
    "id": 1,
    "logradouro": "Avenida Antônio Carlos",
    "numero": "4500",
    "bairro": "Pampulha",
    "cidade": "Belo Horizonte",
    "estado": "MG",
    "cep": "31270010",
    "latitude": null,
    "longitude": null
  }
}
```

O exemplo mostra os campos de endereço; os demais campos do estabelecimento continuam presentes. Na listagem, o objeto aparece em cada item de `content`.

A migration `V14__add_address_details_and_coordinates.sql` adiciona os campos opcionais sem modificar os dados existentes. Latitude e longitude são campos de saída preenchidos pelo backend, não são recebidos no cadastro/atualização. Quando os dados do endereço mudam, as coordenadas antigas são limpas e a API tenta obter novas; sem mudança, coordenadas existentes são preservadas.

### Geocodificação com Geoapify

Configure `GEOAPIFY_API_KEY` no ambiente do backend. No Render, adicione a variável em **Environment**; o arquivo de exemplo não é carregado como configuração de produção. Localmente, o backend também carrega um `.env` na raiz, ignorado pelo Git e pelo Docker:

```dotenv
GEOAPIFY_API_KEY=sua_chave
```

A chave nunca deve ser enviada ao frontend nem gravada em arquivos versionados. O `application-example.yaml` contém somente a referência à variável.

Ao cadastrar ou atualizar um estabelecimento, a API tenta localizar seu endereço quando não existem coordenadas. A consulta inclui os dados disponíveis e restringe os resultados ao Brasil. São aceitos resultados de prédio, estabelecimento ou rua com confiança mínima de 0,7; resultados genéricos de cidade/CEP e coordenadas inválidas são descartados. Uma localização de rua pode ser aproximada.

O cliente aguarda até 3 segundos para conectar e até 10 segundos pela resposta do Geoapify. Timeout, limite de uso, chave ausente ou endereço não localizado não impedem salvar o estabelecimento. Nesses casos as coordenadas permanecem nulas. Não há novas consultas ao Geoapify durante listagens ou buscas por distância.

Para tentar novamente, o proprietário ou um ADMIN pode chamar:

```http
POST /v1/estabelecimentos/{id}/geocodificar
Authorization: Bearer <jwt>
```

Retorna `200 OK` com o estabelecimento e `endereco` completo. Se a tentativa falhar, as coordenadas continuam nulas. Coordenadas já preenchidas são preservadas.

A migration `V15__track_address_geocoding_attempts.sql` registra a data da última tentativa e adiciona controle de versão para impedir que um reprocessamento sobrescreva uma edição concorrente do endereço.

Com uma chave configurada, o backend também processa endereços antigos de estabelecimentos ativos: até 25 endereços por execução, 15 segundos após iniciar e depois a cada hora. Prioriza os nunca tentados e espera seis horas antes de repetir uma falha. Cada endereço é processado em sua própria transação; erros não interrompem o restante do lote. Defina `GEOCODING_BACKFILL_ENABLED=false` para desativar. O agendamento só funciona enquanto a instância está em execução; no Render gratuito ela pode ficar suspensa.

As chamadas são limitadas a menos de cinco por segundo por instância. O consumo do plano é compartilhado com os demais usos da mesma chave; monitore a cota no painel do fornecedor. A integração segue a [API oficial de geocodificação](https://apidocs.geoapify.com/docs/geocoding/forward-geocoding/). Ao exibir informações derivadas no frontend, inclua o link **Powered by Geoapify** e a atribuição às fontes, conforme as [regras do plano gratuito](https://www.geoapify.com/pricing/).

### Busca por proximidade

```http
GET /v1/estabelecimentos/proximos?latitude=-19.92&longitude=-43.94&raioKm=5
Authorization: Bearer <jwt>
```

Retorna uma lista JSON, sem paginação, de estabelecimentos ativos dentro do raio, ordenada por `distanciaKm` crescente. Cada item contém os mesmos campos do estabelecimento, incluindo `endereco` completo, mais `distanciaKm`. Endereços sem coordenadas ficam fora da busca; sem resultados, retorna `[]`.

Latitude deve estar entre -90 e 90, longitude entre -180 e 180, e o raio deve ser positivo. Os dois primeiros parâmetros são obrigatórios. Se `raioKm` for omitido, usa `raioBuscaKm` salvo do usuário autenticado (5 km por padrão); um raio explícito pode ser qualquer número positivo finito, vale só para essa consulta e não altera a preferência. Valores inválidos retornam `400 Bad Request`. A distância é calculada pela fórmula de Haversine no banco, em linha reta sobre a superfície terrestre, e não representa um trajeto por ruas.

### Produtos

Veja o [guia para o frontend](docs/frontend-produtos-promocoes.md) para os fluxos de cadastro e pesquisa de produtos e promoções.

```text
POST   /v1/produtos
GET    /v1/produtos/{id}
GET    /v1/produtos/por-codigo/{codigo}
GET    /v1/produtos/por-qr-code?conteudo=
GET    /v1/produtos?nome=&marca=&categoria=
PUT    /v1/produtos/{id}
DELETE /v1/produtos/{id}
POST   /v1/produtos/{id}/imagem
```

A consulta requer JWT e recebe o texto decodificado pela câmera do aplicativo. Use `GET /v1/produtos/por-codigo/7891000100103` para códigos de barras e `GET /v1/produtos/por-qr-code?conteudo=7891000100103` para QR. Codifique o parâmetro `conteudo` como componente de URL. O QR aceita o conteúdo cadastrado de um produto, um GTIN numérico ou um GS1 Digital Link HTTPS com o identificador `01`. O backend extrai o GTIN do caminho, sem acessar a URL recebida. Importação de notas fiscais não faz parte desse fluxo.

- `200` com `origem: "LOCAL"` e `produto` quando o item está cadastrado. O outro campo, `sugestao`, vem nulo.
- `200` com `origem: "OPEN_FOOD_FACTS"` e `sugestao` quando o GTIN válido é encontrado externamente. `produto` vem nulo. Revise os dados e preencha campos ausentes antes de enviar ao cadastro manual.
- `400` para QR incompatível ou parâmetro inválido, `404` para produto não encontrado e `502` para indisponibilidade do provedor ou limite de consultas. O formulário manual permanece disponível.

A sugestão contém código, nome, descrição, marca, categoria, imagem, quantidade informativa e link da fonte, conforme disponibilidade. O usuário responsável pelo cadastro é informado no POST como `idUsuario`, seguindo a autorização existente. O texto `quantidade` e o link `fonte` são informativos na consulta. Quando o Open Food Facts fornece uma medida normalizada válida, a sugestão inclui também `quantidadeEmbalagem` e `unidadeEmbalagem`.

A V19 permite cadastrar a medida total da embalagem como um número positivo e uma unidade `G` (gramas), `ML` (mililitros) ou `UN` (unidades). Envie os dois campos juntos no POST ou PUT. Exemplo para uma garrafa de 2 L: `{"quantidadeEmbalagem":2000,"unidadeEmbalagem":"ML"}`. No PUT, omitir ambos preserva a medida salva. Os produtos anteriores à migration começam com a medida vazia. O produto e o preço da loja continuam com seus contratos próprios. Dados e imagens do provedor têm condições de reutilização próprias. [Licenças do Open Food Facts](https://openfoodfacts.github.io/documentation/docs/Product-Opener/api/tutorials/license-be-on-the-legal-side/)

O Open Food Facts é consultado somente para GTINs válidos ausentes no catálogo local. A instância limita as chamadas a 15 por minuto. Configure `OPEN_FOOD_FACTS_USER_AGENT` para identificar a aplicação e seu contato. A consulta externa dispensa token.

A migration V18 remove espaços nas extremidades e garante unicidade entre códigos de produtos ativos, incluindo representações equivalentes de GTIN preenchidas com zeros. Os códigos continuam sendo armazenados como texto. Se houver duplicatas antigas, a migration falha para que os registros e vínculos sejam revisados. O cadastro aceita `qrCode` opcional e sua unicidade também é garantida entre produtos ativos. No PUT, omitir o campo preserva o QR e enviar vazio o remove.

#### Categorias de supermercado

| Grupo | Valores para o campo `categoria` |
|---|---|
| Perecíveis | `CARNES`, `PEIXES_E_FRUTOS_DO_MAR`, `HORTIFRUTI`, `PADARIA`, `FRIOS_E_EMBUTIDOS`, `LATICINIOS_E_OVOS`, `CONGELADOS` |
| Mercearia | `MERCEARIA`, `DOCES_E_SNACKS`, `CAFES_E_CHAS` |
| Bebidas | `CERVEJAS`, `VINHOS_E_DESTILADOS`, `REFRIGERANTES`, `SUCOS`, `AGUAS` |
| Outros departamentos | `LIMPEZA`, `HIGIENE_PESSOAL`, `PET_SHOP`, `ELETRONICO`, `LAZER`, `FERRAMENTA`, `VESTIMENTA`, `OUTRO` |
| Categorias genéricas | `ALIMENTO`, `BEBIDA` |

Cada produto tem uma categoria. Use `GET /v1/produtos?categoria=REFRIGERANTES` para filtrar por igualdade. O Open Food Facts sugere categorias específicas quando reconhece as tags recebidas. A V24 preserva as classificações existentes, inclusive `ALIMENTO` e `BEBIDA`. Para aparecer em um filtro mais específico, um produto legado pode ser reclassificado pelo PUT do produto.

### Histórico de preços

Use `POST /v1/precos` com JWT para registrar um preço em reais para um produto e uma loja ativos. O autor vem da conta autenticada.

```json
{
  "idProduto": 42,
  "idEstabelecimento": 3,
  "valor": 8.49,
  "observadoEm": "2026-11-01T14:30:00-03:00"
}
```

O retorno `201 Created` contém a observação, incluindo `idUsuario`, `registradoEm` e `origem: "MANUAL"`. Use uma data ISO 8601 com fuso horário e um valor positivo com até duas casas decimais. Datas futuras e valores inválidos retornam `400`.

Consulte `GET /v1/produtos/{idProduto}/historico-precos` com JWT. Passe `idEstabelecimento` para filtrar por loja e `page` e `size` para paginar. A resposta usa `content` e os metadados de paginação, com ordenação fixa pela data observada decrescente e pelo ID decrescente em caso de empate.

A V21 cria uma tabela dedicada. Cada POST acrescenta um registro e as observações anteriores ficam disponíveis, inclusive após a desativação do produto ou da loja.

O cadastro de uma promoção também grava uma observação automaticamente, na mesma transação. Uma edição que altere o preço, a vigência, o produto ou a loja acrescenta uma versão. Repetir a mesma oferta preserva a versão existente.

Esses registros têm `origem: "PROMOCAO"`, `idPromocao`, `precoOriginal`, `inicioVigencia` e `fimVigencia`. `valor` guarda o preço promocional e `observadoEm` registra quando a versão foi criada. O início e o fim da vigência usam o mesmo formato de data local do cadastro de promoção. Promoções futuras ficam identificadas pela vigência. O histórico registra versões da oferta. Para saber se uma oferta ainda está ativa, consulte o cadastro de promoções.

A V22 vincula as novas observações às promoções. A mudança se aplica aos cadastros e às alterações a partir do deploy.

### Promoções

Promoções podem ter `enquantoDurarEstoque: true`. Nesse caso, `dataFim` é opcional. Uma oferta já iniciada e sem data final fica no comparativo por 24 horas após `ultimaConfirmacaoEm`. O cadastro registra a primeira confirmação e `POST /v1/promocoes/{id}/confirmar-disponibilidade` renova a janela, sem acrescentar um preço ao histórico. Se a oferta tiver data final, o comparativo respeita a vigência anunciada.


```text
POST   /v1/promocoes
GET    /v1/promocoes/{id}
GET    /v1/promocoes?idProduto=&idEstabelecimento=&idUsuario=
PUT    /v1/promocoes/{id}
DELETE /v1/promocoes/{id}
```

O cadastro de promoção localiza o produto pelo `codigoBarras`. Os preços precisam ser positivos, o preço promocional não pode superar o original e as datas enviadas pelo endpoint precisam ser futuras.

### Carrinhos

```text
POST   /v1/carrinhos
GET    /v1/carrinhos/{id}
GET    /v1/carrinhos/usuario/{idUsuario}
PUT    /v1/carrinhos/{id}
DELETE /v1/carrinhos/{id}
```

Existe um carrinho ativo por usuário. O preço efetivo do item vem da promoção ativa; `precoItem` é mantido no request por compatibilidade.

### Votos

```text
POST   /v1/votos
GET    /v1/votos/{id}
GET    /v1/votos
DELETE /v1/votos/{id}
```

O cadastro de voto cria ou atualiza o voto ativo do usuário. Também existe compatibilidade com a rota legada `/votos`.

Para ranking:

```text
GET /v1/votos?agruparPor=promocao&dataInicio=2026-01-01T00:00:00&dataFim=2026-12-31T23:59:59&voto=POSITIVO&ordenacao=desc
```

## Modelo atual e próximos módulos

O modelo atual representa preço principalmente por `Promocao` e o carrinho por itens de promoção. Ainda estão fora do escopo implementado:

- Importação de itens e preços a partir do QR Code de notas fiscais.
- Lista de compras comparável entre vários estabelecimentos.
- Cálculo de economia e itens sem preço.
- Alertas de queda de preço.
- Redis, refresh token e observabilidade avançada.

## Migrations

As migrations ficam em `src/main/resources/db/migration` e são aplicadas em ordem pelo Flyway.

A migration `V13__reset_mvp_seed.sql` prepara o seed mínimo do MVP. Ela é destrutiva e deve ser aplicada somente em um banco cujo conteúdo atual possa ser substituído.

## Estrutura do projeto

```text
src/main/java/br/com/anima/nuPrecin
├── auth             autenticação, cadastro e recuperação de senha
├── usuario          contas, roles e perfil
├── estabelecimento  estabelecimentos e endereços
├── produto          catálogo e imagens
├── promocao        promoções/preços
├── carrinho        carrinho e itens
├── voto            votos e ranking
├── storage         integração com Supabase Storage
├── email           integração com Brevo
├── security        JWT, CORS e autorização
└── exception       respostas padronizadas de erro
```

## Testes

```bash
./mvnw test
```

Os testes usam H2 em memória no perfil `test`, incluindo teste de carregamento do contexto e validação do contrato OpenAPI. O banco remoto não é usado pelos testes padrão.

### Comparativo de preços entre supermercados

Consulte `GET /v1/produtos/{idProduto}/comparativo-precos` com JWT. A resposta lista a promoção vigente de menor preço por estabelecimento ativo, do menor para o maior preço. Cada item inclui o ID e o nome do estabelecimento, o ID da promoção, o preço original, o preço promocional e o período de vigência.

O comparativo interpreta as datas de vigência no horário de Brasília (`America/Sao_Paulo`). Uma promoção em vigor pode ter o preço editado mantendo a data original de início.

Um produto ativo sem ofertas vigentes retorna `[]`. Um produto ausente ou inativo retorna `404`. O comparativo usa as promoções cadastradas, inclusive as anteriores à implantação do histórico.

### Login com Google

O JWT do NuPrecin identifica a conta pelo ID interno. Após implantar esta versão, sessões emitidas pela versão anterior precisam fazer login novamente.

Configure `GOOGLE_CLIENT_ID` no backend com o OAuth Client ID usado para emitir o ID token. O backend valida a assinatura, o emissor, a validade e a audiência do token. Veja a [documentação do Google](https://developers.google.com/identity/gsi/web/guides/verify-google-id-token).

Envie `{"idToken":"<ID token do Google>"}` para `POST /v1/auth/google`. A resposta `200` segue o login por senha e inclui `id`, `nome`, `email`, `token` e `raioBuscaKm`.

O primeiro login vincula automaticamente uma conta ativa quando o NuPrecin tem confirmação registrada para o e-mail atual e o Google é autoridade sobre o endereço verificado (Gmail ou Google Workspace). Para endereços de terceiros e contas antigas sem esse registro, entre primeiro no NuPrecin e envie o mesmo corpo para `POST /v1/auth/google/vincular` com o JWT do NuPrecin. O e-mail verificado precisa coincidir com o da conta e o sucesso retorna `204`. Os próximos logins identificam a conta pelo identificador estável do Google. Novos usuários concluem o cadastro com confirmação de e-mail antes de usar o login com Google.
