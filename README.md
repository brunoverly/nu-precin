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

Retorna `200 OK` com `id` do usuário, `nome`, `email` e `token`.

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

Retorna `201 Created` com `id` do usuário, `nome`, `email`, `foto` e `dataCadastro`. Depois, faça login para obter o JWT.

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

Todas as rotas abaixo, exceto `/v1/auth/**`, `/actuator/health` e a documentação OpenAPI, exigem JWT.

### Usuários

```text
POST   /v1/usuarios             Criação direta; somente ADMIN
GET    /v1/usuarios/{id}        Consulta o próprio usuário ou qualquer usuário para ADMIN
GET    /v1/usuarios             Lista; somente ADMIN
PUT    /v1/usuarios/{id}        Atualiza perfil e senha atual/nova
DELETE /v1/usuarios/{id}        Soft delete
POST   /v1/usuarios/{id}/foto   Upload de foto
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

Latitude deve estar entre -90 e 90, longitude entre -180 e 180, e o raio deve ser positivo. Os dois primeiros parâmetros são obrigatórios; o raio padrão é 5 km. Valores inválidos retornam `400 Bad Request`. A distância é calculada pela fórmula de Haversine no banco, em linha reta sobre a superfície terrestre, e não representa um trajeto por ruas.

### Produtos

```text
POST   /v1/produtos
GET    /v1/produtos/{id}
GET    /v1/produtos?nome=&marca=&categoria=
PUT    /v1/produtos/{id}
DELETE /v1/produtos/{id}
POST   /v1/produtos/{id}/imagem
```

### Promoções

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

- Integração externa de leitura de código de barras/QR Code.
- Histórico e contribuição independente de preços.
- Lista de compras comparável entre vários estabelecimentos.
- Cálculo de economia e itens sem preço.
- Geolocalização, distância e favoritos.
- Redis, OAuth2, refresh token e observabilidade avançada.

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
