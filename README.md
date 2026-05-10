<div align="center">

# 🛒 nuPrecin
### APP colaborativo para cadastro e comparação de preços e promoções

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23C1272D?style=for-the-badge&logo=swagger&logoColor=white)

</div>

<br>

## 👥 Sobre o projeto

Projeto backend para o projeto NuPrecin.

A ideia é que os próprios usuários alimentem o sistema com:
- 🏪 estabelecimentos
- 📦 produtos
- 💰 promoções
- 👍 votos
- 🛒 carrinhos de compra

Tudo isso para permitir **comparação de preços entre mercados**.

---

## 🚨 IMPORTANTE (antes de rodar)

> [!IMPORTANT]
> Você PRECISA:

### ☕ Java 17
Certifique que você possui Java 17 instalado e esta configurado para o projeto.

### 🐘 PostgreSQL rodando
E criar o banco:

```sql
CREATE DATABASE "nuprecin";
```

> [!WARNING]
> 👉 Se não fizer isso, a aplicação **NÃO** sobe.

---

## ⚙️ Configuração do projeto

### 📁 Arquivos
```text
resources/
├── application.yaml
├── application-dev.yaml ❌ (não subir)
└── application-example.yaml ✅ (modelo)
```

### 🧪 Como configurar
1. Copie o arquivo:
```bash
cp src/main/resources/application-example.yaml src/main/resources/application-dev.yaml
```
2. Preencha com seus dados do banco.

> 📌 Existe um README explicando melhor:
> 👉 *README - application.properties*

---

## 🗃️ Banco de dados (Flyway)

📍 **Local:** `src/main/resources/db/migration`

> [!CAUTION]
> **📌 Regras IMPORTANTES:**
> - ❌ **NÃO** criar tabela manual no banco
> - ✅ **SEMPRE** usar migration
> - ❌ **NÃO** editar migration antiga
> - 🔄 **SEMPRE** atualizar sua branch antes

### 📌 Nome padrão
```text
V1__create_usuario.sql
V2__create_produto.sql
```

> 📌 Leia para mais informações sobre o Flyway Migrations:
> 👉 *README - flyway*

---

## 🧱 Estrutura do projeto

```text
|nuPrecin
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── mapper
├── repository
├── security
├── service
└── specification
```

---

## 📌 MUITO IMPORTANTE

👉 **TODAS** as pastas já têm uma classe de exemplo.

**Exemplo:**
- `Exemplo`
- `ExemploService`
- `ExemploMapper`
- `etc`

---

## 🧠 Como desenvolver

🔥 **Regra principal:**
👉 Em caso de dúvidas, uses as classes exemplos.

**Exemplo:**
```text
Exemplo → Produto
ExemploService → ProdutoService
ExemploMapper → ProdutoMapper
```

---

## 🧾 Padrão de nomes

| Tipo | Exemplos |
| :--- | :--- |
| 🧱 **Entidades** | `Produto`, `Usuario`, `Estabelecimento`, etc. |
| 📦 **DTOs** | `ProdutoRequestDto`, `ProdutoResponseDto`, `UsuarioRequestDto`, etc. |
| 🔄 **Mapper** | `ProdutoMapper`, `UsuarioMapper`, `EstabelecimentoMapper`, etc. |
| 🧠 **Service** | `ProdutoService`, `UsuarioService`, `EstabelecimentoService`, etc. |
| 🗄️ **Repository** | `ProdutoRepository`, `UsuarioRepository`, `EstabelecimentoRepository`, etc. |

---

## 🔄 Fluxo padrão de uma feature

Toda feature deve ter:
- `entity`
- `dto` (formato Record Request/Response)
- `mapper`
- `repository`
- `service`
- `controller`
- `migration`
- `specification` (se necessário)

---

## 🔁 MapStruct

👉 Já configurado no projeto. Use sempre:
- `toEntity`
- `toResponse`
- `updateEntityFromDto`

> 📌 **Template já existe:**
> 👉 `EntidadeMapperTemplate`

---

## ⚠️ Tratamento de erro

Já configurado:
- `GlobalExceptionHandler`
- `ErrorResponse`

> 👉 Em casos de necessidade, criar exceptions especiais.

---

## 🛠️ Regras do time e Código

### 🔀 Git
- Cada um trabalha na sua branch
- ❌ **Não subir** direto na main
- 🔄 **Sempre atualizar** antes

### 🧩 Código
- Seguir padrão do exemplo
- Manter nomes consistentes
- Não inventar estrutura nova
- O projeto deve ser homogêneo

---

## 🎯 Resumo rápido

Se você está começando:

1. 🐘 sobe o **PostgreSQL**
2. 🗄️ cria o banco `"nuprecin"`
3. ⚙️ configura `application-dev.yaml`
4. 📖 lê os **READMEs** internos
5. 📋 segue conforme o **exemplo**
6. 🔧 adapta pra sua entidade
7. 🗃️ cria **migration**
8. 🚀 sobe sua **branch**

---

> [!NOTE]
> 💡 Em caso de sugestões ou melhorias, sinta livre para compartilhar, o intuito é que todos possam participar e contribuir.
