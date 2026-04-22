# 📦 Flyway - Padrão de Migrations do Projeto

## 📌 Objetivo

Este projeto utiliza o **Flyway** para controle de versão do banco de dados.

👉 Toda alteração no banco deve ser feita via migration.
👉 Não é permitido criar ou alterar tabelas manualmente no banco.

---

## 📁 Local das migrations

```text
src/main/resources/db/migration
```

---

## 🧠 Como funciona

* Cada arquivo `.sql` representa uma alteração no banco
* O Flyway executa os arquivos em ordem de versão
* Cada migration roda apenas **uma vez**

---

## 📝 Padrão de nome dos arquivos

```text
V<numero>__<descricao>.sql
```

### Exemplos:

```text
V1__create_usuarios.sql
V2__create_produtos.sql
V3__create_estabelecimentos.sql
```

---

## ⚠️ Regras IMPORTANTES

### 1. Nunca editar uma migration já executada ❌

Se precisar alterar algo:

* crie uma nova migration

---

### 2. Sempre sincronizar com a main antes de criar uma migration

```bash
git pull origin main
```

Depois disso:

* verifique o último número de migration
* crie a próxima versão

---

### 3. Uma migration = uma responsabilidade

✔ Correto:

```text
V1__create_usuarios.sql
V2__create_produtos.sql
```

❌ Errado:

```text
V1__create_all_tables.sql
```

---

### 4. Ordem importa

Se uma tabela depende de outra (FK), ela deve ser criada depois.

Exemplo:

```text
usuarios → produtos → promocoes
```

---

### 5. Não criar migrations vazias ❌

Arquivos vazios:

* são ignorados ou causam inconsistência
* não devem ser usados

---

## 🔄 Fluxo de trabalho

1. Criar entidade
2. Criar migration correspondente
3. Testar localmente
4. Commitar e subir

---

## ⚠️ Conflito de versões

Se duas pessoas criarem a mesma versão (ex: V2):

👉 quem for subir depois deve:

* atualizar a branch
* renomear sua migration para a próxima versão disponível

---

## 🧪 Executando as migrations

As migrations são executadas automaticamente ao iniciar a aplicação.

---

## 🎯 Configuração do projeto

```properties
spring.jpa.hibernate.ddl-auto=validate
```

👉 O JPA apenas valida
👉 O Flyway controla o banco

---

## 🚀 Boas práticas

* Nomear arquivos de forma clara
* Evitar mudanças diretas no banco
* Manter migrations pequenas e organizadas

---

## 📌 Resumo

✔ Use Flyway para qualquer alteração no banco
✔ Nunca edite migrations antigas
✔ Sempre siga a ordem de versões
✔ Sincronize antes de criar novas migrations

---
