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

Retorna `200 OK` com `nome`, `email` e `token`.

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

Retorna `201 Created`. Depois, faça login para obter o JWT.

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
```

Na criação/atualização, informe `idEndereco` ou `endereco`, nunca os dois.

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
