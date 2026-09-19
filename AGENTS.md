# AGENTS.md — Guia de trabalho do NuPrecin

Este arquivo orienta agentes e colaboradores que alterarem este repositório. O projeto é o backend do NuPrecin, uma API colaborativa para cadastro, consulta e comparação de preços e promoções em estabelecimentos comerciais.

## 1. Como interpretar a documentação

Use as fontes abaixo nesta ordem quando houver conflito:

1. Código Java e migrations Flyway: representam o contrato implementado no momento.
2. `README.md`: resume arquitetura, entidades, endpoints e fluxo feliz do backend.
3. `src/main/resources/postman/NuPrecin.postman_collection.json`: exemplos de consumo da API; valide as rotas no código antes de reproduzi-los.
4. Diagramas em `src/main/resources/docs/`: ajudam a entender as relações de domínio, mas podem estar defasados em relação ao código e ao banco.
5. Documento de requisitos do produto fornecido para o início do projeto: representa a visão do produto e o backlog do MVP, não necessariamente funcionalidades já implementadas.

Não transforme automaticamente um requisito evolutivo em código. Quando uma mudança ampliar o modelo ou o contrato da API, preserve o comportamento atual, registre a decisão e inclua testes e migration compatíveis.

## 2. Visão do produto

O NuPrecin deve ajudar o usuário a descobrir mercados, consultar produtos, colaborar com preços atualizados, montar uma compra e comparar o custo total entre estabelecimentos. A jornada-alvo é:

```text
entrada/autenticação → home → exploração, lista ou contribuição
                         ↓
                  preços contextualizados
                         ↓
              comparação, economia e decisão de compra
```

O valor principal está na comparação de uma compra completa, e não somente na exibição de um preço isolado. Dados colaborativos devem sempre carregar contexto suficiente para gerar confiança: produto, estabelecimento, valor, data/hora e origem do registro.

### Escopo-alvo do MVP

- autenticação e conta;
- descoberta de estabelecimentos e catálogo de produtos;
- contribuição de preço por código de barras/QR Code ou preenchimento manual;
- lista de compras com itens e quantidades;
- comparação por estabelecimento, total estimado, economia e itens sem preço;
- feedback claro para sucesso, erro, ausência de dados e permissão negada.

### Estado implementado hoje

O backend já possui autenticação JWT e CRUDs para usuários, endereços, estabelecimentos, produtos, promoções, carrinhos, itens de carrinho e votos. A representação atual de preço é feita principalmente por `Promocao`, e o carrinho é composto por itens vinculados a promoções.

Ainda não há, como módulos independentes, histórico de preços/contribuições, preferências de distância, favoritos, geolocalização, leitura de código, entidade de lista de compras comparável, motor de comparação por mercado ou recompensas. Essas capacidades pertencem à evolução do produto; implemente-as somente quando houver uma tarefa explícita e uma decisão de contrato/modelo.

## 3. Stack e comandos

- Java 17.
- Spring Boot `3.5.13`.
- Spring Web, Spring Data JPA e Jakarta Validation.
- PostgreSQL e Flyway.
- Spring Security com JWT (`java-jwt`) e BCrypt.
- MapStruct e Lombok.
- Springdoc OpenAPI/Swagger.
- Maven Wrapper (`./mvnw`; no Windows, `mvnw.cmd`).

Comandos principais:

```bash
./mvnw test
./mvnw -DskipTests compile
./mvnw clean verify
./mvnw spring-boot:run
```

O perfil padrão em `src/main/resources/application.yaml` é `dev`. O arquivo `src/main/resources/application-dev.yaml` é local/ignorado pelo Git e precisa apontar para um PostgreSQL disponível; use `application-example.yaml` como modelo sem copiar credenciais reais para o repositório. A aplicação executa validação das migrations e do schema ao iniciar.

O Swagger UI fica em `/swagger-ui.html` e o contrato OpenAPI em `/v3/api-docs`, quando a aplicação está em execução. A coleção para testes manuais está em `src/main/resources/postman/NuPrecin.postman_collection.json`.

## 4. Estrutura do código

O código é organizado por domínio dentro de `src/main/java/br/com/anima/nuPrecin`:

| Pacote | Responsabilidade |
| --- | --- |
| `auth` | login e emissão do JWT |
| `security` | filtro, validação e configuração do JWT/Spring Security |
| `usuario` | conta, credenciais, roles e soft delete |
| `endereco` | endereço de estabelecimento |
| `estabelecimento` | mercados/estabelecimentos e filtros |
| `produto` | catálogo, código de barras e categoria |
| `promocao` | preços promocionais associados a produto e estabelecimento |
| `carrinho` | carrinho único por usuário e itens baseados em promoção |
| `voto` | avaliação de promoções e ranking |
| `exception` | exceções e resposta HTTP padronizada |

Cada domínio normalmente contém entidade, DTOs, mapper, repository, service e controller. As migrations ficam em `src/main/resources/db/migration` e são aplicadas em ordem pelo Flyway.

## 5. Convenções de implementação

### Camadas

- Controllers expõem REST, validam payloads com `@Valid` e delegam regras ao service.
- Services resolvem dependências, aplicam regras de negócio e controlam soft delete.
- Repositories usam Spring Data JPA; para filtros combináveis, use `JpaSpecificationExecutor` e Specifications do domínio.
- DTOs de entrada e saída são preferencialmente `record`; não exponha entidades JPA diretamente.
- Mappers MapStruct (`@Mapper(componentModel = "spring")`) convertem DTOs e entidades.
- Entidades usam `@Enumerated(EnumType.STRING)`, associações explícitas e `@PrePersist`/`@PreUpdate` somente para invariantes simples.

Preserve a separação de camadas. Não coloque regra de negócio no controller, não faça acesso direto ao repository a partir do controller e não use o mapper para resolver relações que exigem consulta ao banco; resolva essas relações no service.

O código existente usa injeção por campo com `@Autowired`. Não faça uma refatoração ampla de estilo junto com uma funcionalidade; em mudanças novas, mantenha consistência local e prefira uma migração de injeção documentada se ela for realmente necessária.

### Validação e erros

- Use Jakarta Validation nos DTOs para campos obrigatórios, valores positivos, e-mail e enums.
- `EntityNotFoundException` resulta em `404`.
- Erros de validação e `IllegalArgumentException` resultam em `400`.
- Violações de integridade resultam em `409`.
- Erros são formatados por `GlobalExceptionHandler` no contrato `ErrorResponse`.
- Não retorne senha, segredo JWT ou credencial em resposta, log ou mensagem de exceção.

Ao adicionar uma exceção de negócio recorrente, escolha explicitamente o status HTTP e atualize o handler/testes. Não esconda erro de autorização como erro interno.

### API

Mantenha o prefixo `/v1` nos novos endpoints. O padrão de sucesso existente é:

- `POST`: `201 Created`, corpo de resposta e header `Location`;
- `GET`: `200 OK`;
- `PUT`: `200 OK`;
- soft delete via `DELETE`: `204 No Content`.

Os endpoints implementados são:

| Recurso | Rotas principais |
| --- | --- |
| Auth | `POST /v1/auth/login` |
| Usuários | `/v1/usuarios` |
| Estabelecimentos | `/v1/estabelecimentos` |
| Produtos | `/v1/produtos` |
| Promoções | `/v1/promocoes` |
| Carrinhos | `/v1/carrinhos` e `/v1/carrinhos/usuario/{idUsuario}` |
| Votos | atualmente mapeado no código como `/votos`; a coleção Postman/README usam `/v1/votos` |

Confirme o mapping real antes de alterar uma rota. A divergência de votos deve ser resolvida em uma tarefa própria ou acompanhada de compatibilidade/documentação, para não quebrar consumidores existentes.

Listagens paginadas devem continuar usando `Pageable`; filtros opcionais devem ser compostos por Specifications e devem ignorar registros inativos.

### Persistência e migrations

- Nunca edite uma migration já aplicada para corrigir dados ou schema; crie uma nova migration versionada.
- A última migration existente no repositório é `V9__insert_dados_teste.sql`; a próxima deve ser `V10__...`, salvo se o histórico mudar explicitamente.
- Antes de adicionar constraint, índice ou cardinalidade, verifique dados legados, especialmente o seed `V9`.
- Use `BigDecimal` para valores monetários e `LocalDateTime` para datas do domínio.
- Mantenha nomes de tabela/coluna alinhados às migrations (`id_usuario`, `id_produto`, `id_estabelecimento`, etc.).
- Soft delete significa marcar `ativo = false`, preservar o registro e filtrar o inativo nas consultas de negócio.
- Depois de alterações de associação JPA, valide tanto o schema limpo quanto um banco com as migrations e dados de teste aplicados.

## 6. Regras de domínio que devem ser preservadas

### Usuário e segurança

- Usuários possuem role `USER` ou `ADMIN`; novos usuários começam como `USER` e ativos.
- Senhas são armazenadas com BCrypt.
- O JWT usa e-mail como subject, inclui role e expira conforme `JwtService`.
- Rotas de auth são públicas; as demais exigem autenticação pelo filtro JWT.
- O fato de um DTO receber `idUsuario` não prova que a operação pertence ao usuário autenticado. Em novos fluxos, derive o usuário do `SecurityContext` sempre que a operação for pessoal; se houver exceção administrativa, aplique autorização explícita.

### Estabelecimento e endereço

- Estabelecimento precisa de usuário criador ativo.
- O endereço pode ser referenciado por `idEndereco` ou criado a partir do objeto embutido; não aceite os dois fluxos de forma ambígua.
- Os tipos atuais são `MERCADO`, `SUPERMERCADO` e `ATACADAO`.

### Produto

- Produto pertence a um usuário ativo e possui nome, descrição, marca, código de barras e categoria.
- As categorias atuais são `BEBIDA`, `ALIMENTO`, `LIMPEZA`, `ELETRONICO`, `LAZER`, `FERRAMENTA`, `VESTIMENTA` e `OUTRO`.
- Código de barras é usado para localizar produto ativo. Não presuma unicidade física enquanto ela não estiver garantida por constraint/migration.

### Promoção/preço atual

- Uma promoção relaciona produto, estabelecimento e usuário.
- Preços devem ser positivos e `precoPromocao` não pode superar `precoOriginal`.
- `dataFim` não pode ser anterior a `dataInicio`.
- O DTO atual exige datas futuras; o seed SQL pode conter datas passadas porque não passa pela validação HTTP. Não altere essa regra sem decidir o comportamento de promoções já iniciadas.
- Toda consulta de negócio deve considerar apenas promoções ativas, salvo uma necessidade explícita de histórico.

### Carrinho

- Existe um único carrinho por usuário no modelo atual, reforçado por `@OneToOne` e índice único em `carrinhos.id_usuario`.
- O carrinho possui itens; cada item referencia uma promoção e calcula `precoTotal = precoItem × quantidadeItem`.
- O total do carrinho é a soma dos totais dos itens.
- A remoção é lógica (`ativo = false`). Criação, reativação e alteração do proprietário devem respeitar a unicidade 1:1.
- O carrinho atual não é ainda a lista de compras comparável do documento de requisitos: ele aceita itens de promoção e não possui seleção de vários mercados nem cálculo de economia entre estabelecimentos.

### Voto

- Um usuário não pode votar na própria promoção.
- Deve haver no máximo um voto ativo por usuário e promoção; novo voto ativo atualiza o anterior.
- Rankings filtram por período e tipo de voto e aceitam ordenação ascendente/descendente.

## 7. Fluxos de produto para orientar evoluções

Ao implementar a visão-alvo, preserve a conexão entre estes fluxos:

1. **Exploração:** usuário escolhe estabelecimento, consulta catálogo/detalhe e pode adicionar item à compra.
2. **Lista e comparação:** usuário define itens e quantidades, escolhe estabelecimentos, recebe totais, economia, ranking e indicação de itens sem preço.
3. **Contribuição:** usuário escolhe estabelecimento, tenta leitura automática, usa fallback manual quando necessário, confirma/cadastra produto, registra preço e recebe feedback.
4. **Macro:** autenticação e home distribuem as três trilhas; contribuição deve alimentar a base usada pela comparação.

Falha de câmera, localização ou internet não deve quebrar a jornada. Sempre que uma integração ainda não existir, mantenha um fallback manual e deixe o estado dos dados explícito.

## 8. Testes e definição de pronto

O repositório atualmente possui apenas um teste de carregamento de contexto. Toda funcionalidade nova deve aumentar a cobertura da regra alterada, preferencialmente com:

- teste unitário de service para regras e cenários de erro;
- teste de controller/MockMvc para status, validação, autenticação e contrato JSON;
- teste de repository/integração quando a mudança depender de query, constraint ou migration;
- cenário de soft delete e tentativa de acesso ao registro inativo;
- cenário feliz e pelo menos um caso de dado ausente/conflitante.

Antes de concluir uma alteração:

1. confira o diff e se não há arquivo local/segredo incluído;
2. execute os testes ou, se o banco local impedir a execução, rode ao menos `./mvnw -DskipTests compile` e registre a limitação;
3. valide migrations em banco limpo quando o schema tiver mudado;
4. atualize README, Postman ou documentação de API quando o contrato público mudar.

## 9. Pontos de atenção conhecidos

Estes pontos foram identificados na leitura inicial e devem ser tratados como decisões, não como licença para alterações silenciosas:

- A visão de produto fala em preço colaborativo, histórico, lista comparável e economia; o modelo atual usa `Promocao`, `Carrinho` e `ItemCarrinho`. Antes de criar endpoints de contribuição/comparação, decidir se haverá entidades de preço/lista separadas ou uma evolução desses domínios.
- O diagrama ER mostra a relação de endereço de forma diferente do código/migration. Hoje a fonte operacional é `estabelecimentos.id_endereco` com `@OneToOne` em `Estabelecimento`.
- O diagrama e alguns documentos são conceituais; não use nomes ou cardinalidades do PDF para substituir o schema efetivo sem migration correspondente.
- A rota de votos diverge entre código (`/votos`) e documentação/coleção (`/v1/votos`).
- A segurança atual autentica por JWT, mas várias operações recebem `idUsuario` no corpo e não demonstram autorização por proprietário. Novos endpoints pessoais devem corrigir esse risco sem confiar no identificador enviado pelo cliente.
- A cobertura de testes é mínima e não há ainda integração de código de barras/QR Code, geolocalização, Redis, OAuth2, refresh token ou observabilidade avançada.
- `application-dev.yaml` contém configuração local e é ignorado pelo Git. Nunca copie seus valores para commits, exemplos, logs ou documentação.

Quando uma tarefa depender de qualquer um desses pontos, pare para registrar a escolha no código/documentação e cubra-a com teste. Se a decisão alterar o escopo do MVP, peça confirmação ao responsável pelo produto.

