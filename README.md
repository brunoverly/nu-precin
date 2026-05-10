<div align="center">

# 🛒 nuPrecin - Backend
### APP colaborativo para cadastro e comparação de preços e promoções

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23C1272D?style=for-the-badge&logo=swagger&logoColor=white)

</div>
 
<br>

## 📌 Objetivo do Sistema
O **nuPrecin** é um sistema colaborativo (crowdsourcing) de preços e promoções em estabelecimentos comerciais. O MVP do backend provê APIs para cadastro e consulta de estabelecimentos, produtos e promoções, além de controle de carrinho, voto e autenticação com JWT, viabilizando comparação de preços e descoberta de ofertas em tempo real.

---

## 🏗 Arquitetura do Backend
A aplicação segue uma arquitetura em camadas, segmentada por domínio:

- **Controllers**: expõem endpoints REST e fazem validação básica de entrada (DTOs).
- **Services**: concentram regras de negócio, validações de consistência e orquestração de domínios.
- **Repositories**: acesso a dados via Spring Data JPA, com `JpaRepository` e `JpaSpecificationExecutor`.
- **Entities**: entidades JPA (mapeamento relacional) com soft delete (`ativo`).
- **DTOs**: `RequestDto` e `ResponseDto` como contratos de entrada/saída.
- **Mappers**: MapStruct para conversão entre DTOs e entidades.
- **Security**: autenticação stateless com JWT, filtro de autenticação e configuração de rotas públicas/privadas.
- **Exceptions**: tratativas globais via `GlobalExceptionHandler` com resposta padronizada.

---

## 🧱 Modelo de Dados (Entidades e Campos)

### Usuario
- `id` (Long)
- `nome` (String)
- `email` (String)
- `foto` (String)
- `senha` (String - hash BCrypt)
- `dataCadastro` (LocalDateTime)
- `ativo` (boolean)
- `role` (UsuarioRole - String Enum)

### Endereco
- `id` (Long)
- `logradouro` (String)
- `bairro` (String)
- `cidade` (String)
- `estado` (String)

### Estabelecimento
- `id` (Long)
- `nome` (String)
- `tipo` (EstabelecimentoTipo - Enum)
- `foto` (String)
- `telefone` (String)
- `ativo` (boolean)
- `endereco` (Endereco - OneToOne)
- `usuario` (Usuario - ManyToOne)

### Produto
- `id` (Long)
- `nome` (String)
- `descricao` (String)
- `marca` (String)
- `codigoDeBarras` (String)
- `qrCode` (String)
- `imagem` (String)
- `categoria` (ProdutoEnum - Enum)
- `ativo` (boolean)
- `usuario` (Usuario - ManyToOne)

### Promocao
- `id` (Long)
- `precoOriginal` (BigDecimal)
- `precoPromocao` (BigDecimal)
- `dataCriacao` (LocalDateTime)
- `dataAtualizacao` (LocalDateTime)
- `dataInicio` (LocalDateTime)
- `dataFim` (LocalDateTime)
- `ativo` (boolean)
- `produto` (Produto - ManyToOne)
- `estabelecimento` (Estabelecimento - ManyToOne)
- `usuario` (Usuario - ManyToOne)

### Voto
- `id` (Long)
- `voto` (VotoEnum - Enum)
- `dataVoto` (LocalDateTime)
- `ativo` (boolean)
- `usuario` (Usuario - ManyToOne)
- `promocao` (Promocao - ManyToOne)

### Carrinho
- `id` (Long)
- `dataCadastro` (LocalDateTime)
- `precoTotal` (BigDecimal)
- `ativo` (boolean)
- `usuario` (Usuario - OneToOne)
- `itens` (ItemCarrinho - OneToMany)

### ItemCarrinho
- `id` (Long)
- `quantidadeItem` (Integer)
- `precoItem` (BigDecimal)
- `precoTotal` (BigDecimal)
- `promocao` (Promocao - ManyToOne)
- `carrinho` (Carrinho - ManyToOne)

---

## ✅ Regras de Negócio
- **Promoção**:
  - `dataFim` não pode ser anterior a `dataInicio`.
  - `precoPromocao` deve ser maior que zero e menor ou igual ao `precoOriginal`.
  - Promoção sempre referencia `Estabelecimento`, `Produto` e `Usuario` válidos.

- **Estabelecimento**:
  - Endereço obrigatório na criação. No payload, aceita `idEndereco` ou objeto `endereco` embutido.
  - Associação obrigatória ao usuário criador.

- **Carrinho**:
  - Carrinho é único por usuário (OneToOne).
  - `precoTotal` é a soma dos `precoTotal` dos itens.
  - Cada `ItemCarrinho` calcula `precoTotal = precoItem * quantidadeItem`.

- **Soft Delete**:
  - Exclusões são lógicas (`ativo = false`), mantendo histórico.

- **Autenticação**:
  - JWT com claims (issuer, subject, role) e expiração configurada.

---

## 🔌 Endpoints (Resumo)

### Auth
- `POST /v1/auth` → cadastra usuário (retorna token JWT)
- `POST /v1/auth/login` → autentica usuário (retorna token JWT)

### Usuarios
- `POST /v1/usuarios` → cria usuário
- `GET /v1/usuarios/{id}` → detalha usuário por id
- `GET /v1/usuarios` → lista usuários (filtros: `nome`, `email`; paginação padrão)
- `PUT /v1/usuarios/{id}` → atualiza usuário
- `DELETE /v1/usuarios/{id}` → desativa usuário (soft delete)

### Estabelecimentos
- `POST /v1/estabelecimentos` → cria estabelecimento (endereço embutido ou `idEndereco`)
- `GET /v1/estabelecimentos/{id}` → detalha estabelecimento por id
- `GET /v1/estabelecimentos` → lista estabelecimentos (filtros: `nome`, `tipo`, `idUsuario`; paginação padrão)
- `PUT /v1/estabelecimentos/{id}` → atualiza estabelecimento
- `DELETE /v1/estabelecimentos/{id}` → desativa estabelecimento (soft delete)

### Produtos
- `POST /v1/produtos` → cria produto
- `GET /v1/produtos/{id}` → detalha produto por id
- `GET /v1/produtos` → lista produtos (filtros: `nome`, `marca`, `categoria`; paginação padrão)
- `PUT /v1/produtos/{id}` → atualiza produto
- `DELETE /v1/produtos/{id}` → desativa produto (soft delete)

### Promocoes
- `POST /v1/promocoes` → cria promoção
- `GET /v1/promocoes/{id}` → detalha promoção por id
- `GET /v1/promocoes` → lista promoções (filtros: `idProduto`, `idEstabelecimento`, `idUsuario`; paginação padrão)
- `PUT /v1/promocoes/{id}` → atualiza promoção
- `DELETE /v1/promocoes/{id}` → desativa promoção (soft delete)

### Carrinhos
- `POST /v1/carrinhos` → adiciona item ao carrinho do usuário
- `GET /v1/carrinhos/{id}` → detalha carrinho por id
- `GET /v1/carrinhos/usuario/{idUsuario}` → detalha carrinho por usuário
- `PUT /v1/carrinhos/{id}` → substitui itens do carrinho pelo item enviado
- `DELETE /v1/carrinhos/{id}` → desativa carrinho (soft delete)

### Votos
- `POST /votos` → cria/atualiza voto do usuário em uma promoção
- `GET /votos/{id}` → detalha voto por id
- `GET /votos` → lista votos (filtros: `idPromocao`, `idUsuario`, `dataInicio`, `dataFim`, `voto`; ranking com `agruparPor=promocao` e `ordenacao`)
- `DELETE /votos/{id}` → desativa voto (soft delete)

---

## 📄 Diagramas e Coleções
- **Diagrama de Classes disponível em**: `resources/docs/Diagrama-de-Classes.pdf`
- **Diagrama de Entidades e Relacionamentos disponível em**: `resources/docs/Diagrama-Entidade-Relacionamento.pdf`
- **Postman Collection, para facilitar o teste dos endpoints, disponível em:**`resources/postman/nuPrecin.postman_collection.json`

---

## 🔄 Fluxo Principal do Backend (Caminho Feliz)
1. Usuário faz login (Auth) → recebe JWT.
2. Usuário cria um estabelecimento com endereço embutido (ou `idEndereco`).
3. Usuário cria/seleciona produtos.
4. Usuário publica promoção vinculando estabelecimento, produto e seu usuário.
5. Outros usuários consultam promoções e adicionam itens ao carrinho.
6. Carrinho soma automaticamente os totais por item.

---

## 🧰 Stack Completa (Backend)
- Java 17
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Validation (Jakarta Validation)
- Spring Security (JWT)
- BCrypt Password Encoder
- MapStruct
- Lombok
- PostgreSQL
- Flyway
- Maven

---

## 🚀 Próximos Passos (Evolução MVP)
- Integração com API externa de leitura de código de barras e QR Code (fallback de criação de produto).
- Cache com Redis para listagens quentes e promoções ativas.
- Testes unitários e de integração (JUnit + Mockito + Testcontainers).
- Autenticação social com Google (OAuth2).
- Documentação com Swagger OpenApi.
- Pipeline CI/CD para validação automática de migrations e build.
