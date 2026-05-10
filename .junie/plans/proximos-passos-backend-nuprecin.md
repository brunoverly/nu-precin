---
sessionId: session-260510-124820-1r7t
isActive: true
---

# Requirements

### Overview & Goals
Implementar o domínio **Carrinho** com `Service` e `Controller` seguindo o padrão do projeto, garantindo a regra já definida: **cada usuário pode ter somente um carrinho ativo** e o vínculo com usuário deve ser tratado como **1:1 no domínio**.

### Scope
#### In Scope
- Criar `CarrinhoService` com regras de criação, busca, atualização e deleção lógica.
- Criar `CarrinhoController` com endpoints REST para operar o carrinho.
- Ajustar o modelo/repositório para suportar a regra 1:1 entre `Usuario` e `Carrinho`.
- Definir comportamento de criação quando já existir carrinho para o usuário.

#### Out of Scope
- Implementação de `ItemCarrinhoService` e `ItemCarrinhoController` nesta entrega.
- Mudanças nos fluxos de `Promocao`, `Voto`, `Produto` e `Estabelecimento` além das validações de dependência necessárias.

### User Stories
- Como usuário, quero ter um único carrinho ativo para centralizar minha lista de compras.
- Como usuário, quero consultar meu carrinho ativo rapidamente para abrir a aba de carrinho.

### Functional Requirements
1. O sistema deve permitir criar carrinho apenas para usuário ativo.
2. O sistema deve impedir dois carrinhos ativos para o mesmo usuário.
3. A relação usuário-carrinho deve ser tratada como 1:1 no domínio e reforçada na persistência.
4. Deve existir endpoint para buscar carrinho por usuário.
5. A remoção deve continuar sendo soft delete (`ativo = false`).

# Technical Design

### Current Implementation
- `src/main/java/br/com/anima/nuPrecin/carrinho/Carrinho.java` usa `@ManyToOne` com `Usuario`.
- `src/main/java/br/com/anima/nuPrecin/usuario/Usuario.java` mantém `List<Carrinho> carrinhos` com `@OneToMany`.
- `src/main/java/br/com/anima/nuPrecin/carrinho/CarrinhoRepository.java` expõe `findByIdAndAtivoTrue` e `findByUsuarioIdAndAtivoTrue` retornando lista.
- DTOs e mapper já existem (`CarrinhoRequestDto`, `CarrinhoResponseDto`, `CarrinhoMapper`) e estão prontos para uso em service/controller.
- Ainda não existem `CarrinhoService` e `CarrinhoController`.
- `GlobalExceptionHandler` já trata `EntityNotFoundException` (404), `IllegalArgumentException` (400) e `DataIntegrityViolationException` (409).

### Key Decisions
1. **Aplicar regra 1:1 no domínio de carrinho**
   - Ajustar o mapeamento de `Carrinho`/`Usuario` para um único carrinho por usuário no modelo JPA.
2. **Enforçar unicidade em banco para `id_usuario`**
   - Criar migration para impedir múltiplas linhas de carrinho por usuário.
3. **Create com política de reativação**
   - Se existir carrinho inativo para o usuário, reativar e atualizar nome.
   - Se existir carrinho ativo, bloquear criação com erro de negócio.

### Proposed Changes
- **Entidade e persistência**
  - Alterar `Carrinho.java` para vínculo 1:1 com `Usuario`.
  - Ajustar `Usuario.java` para representar relação 1:1 de carrinho.
  - Adicionar migration após `V10` com restrição de unicidade em `carrinhos.id_usuario`.

- **Repositório**
  - Evoluir `CarrinhoRepository` para consultas 1:1 por usuário:
    - `Optional<Carrinho> findByUsuarioId(Long idUsuario)`
    - `Optional<Carrinho> findByUsuarioIdAndAtivoTrue(Long idUsuario)`

- **Service (`CarrinhoService`)**
  - Implementar:
    - `CarrinhoResponseDto create(CarrinhoRequestDto dto)`
    - `CarrinhoResponseDto findById(Long id)`
    - `CarrinhoResponseDto findByUsuario(Long idUsuario)`
    - `CarrinhoResponseDto update(Long id, CarrinhoRequestDto dto)`
    - `void delete(Long id)`
  - Validar usuário ativo com `UsuarioRepository.findByIdAndAtivoTrue(...)`.
  - Aplicar regra de unicidade ativa no create com reativação quando cabível.
  - Manter soft delete no delete.

- **Controller (`CarrinhoController`)**
  - Expor endpoints:
    - `POST /carrinhos`
    - `GET /carrinhos/{id}`
    - `GET /carrinhos/usuario/{idUsuario}`
    - `PUT /carrinhos/{id}`
    - `DELETE /carrinhos/{id}`
  - Seguir padrão dos controllers existentes (`201 + Location` no create, `204` no delete).

### File Structure (impacto previsto)
- **Adicionar**
  - `src/main/java/br/com/anima/nuPrecin/carrinho/CarrinhoService.java`
  - `src/main/java/br/com/anima/nuPrecin/carrinho/CarrinhoController.java`
  - `src/main/resources/db/migration/V11__...sql`
- **Alterar**
  - `src/main/java/br/com/anima/nuPrecin/carrinho/Carrinho.java`
  - `src/main/java/br/com/anima/nuPrecin/usuario/Usuario.java`
  - `src/main/java/br/com/anima/nuPrecin/carrinho/CarrinhoRepository.java`

### Risks
- Pode haver impacto em dados legados se existir mais de um carrinho por usuário antes da migration.
- Mudança de cardinalidade em `Usuario` pode afetar código que assume coleção de carrinhos.

# Testing

### Validation Approach
Validar no nível de serviço e endpoints do carrinho cobrindo regra 1:1, reativação e soft delete.

### Key Scenarios
- Criar carrinho para usuário ativo sem carrinho prévio retorna `201`.
- Tentar criar novo carrinho para usuário com carrinho ativo retorna erro de negócio.
- Buscar carrinho por usuário retorna o carrinho ativo esperado.
- Deletar carrinho marca `ativo=false` e impede retorno em consultas de ativo.

### Edge Cases
- `idUsuario` inexistente/inativo retorna `404`.
- Recriação após delete reativa o carrinho inativo sem violar unicidade.
- Update tentando trocar proprietário do carrinho é rejeitado para preservar regra 1:1.

# Delivery Steps

###   Step 1: Ajustar modelo e persistência para vínculo 1:1 de carrinho por usuário
A estrutura de dados passa a garantir um único carrinho por usuário no domínio e no banco.

- Alterar `Carrinho` e `Usuario` para representar a relação 1:1 no JPA.
- Evoluir `CarrinhoRepository` para consultas por usuário retornando `Optional`.
- Criar migration com restrição de unicidade em `carrinhos.id_usuario`.
- Garantir compatibilidade com dados existentes antes de aplicar a constraint.

###   Step 2: Implementar regras de negócio no CarrinhoService
O `CarrinhoService` passa a controlar criação, leitura, atualização e soft delete respeitando unicidade ativa por usuário.

- Validar usuário ativo antes de processar operações de carrinho.
- Implementar `create` com política de reativação para carrinho inativo e bloqueio quando já houver ativo.
- Implementar `findById`, `findByUsuario`, `update` e `delete` com mensagens de negócio padronizadas.
- Reutilizar `CarrinhoMapper` para mapear request/response e persistência.

###   Step 3: Expor API REST no CarrinhoController conforme padrão do projeto
A aplicação passa a disponibilizar endpoints de carrinho consistentes com os demais domínios.

- Criar endpoints `POST`, `GET`, `PUT` e `DELETE` em `/carrinhos`.
- Adicionar `GET /carrinhos/usuario/{idUsuario}` para abrir a aba com o carrinho ativo do usuário.
- Retornar `201` com `Location` no create e `204` no delete, seguindo o padrão dos controllers existentes.
- Validar cenários críticos de unicidade ativa e soft delete no comportamento exposto pela API.