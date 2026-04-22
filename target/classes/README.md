# ⚙️ Configuração do Projeto - application-dev.yaml

## 📌 Objetivo

Este projeto utiliza arquivos de configuração separados por ambiente.

👉 O arquivo `application-dev.yaml` contém configurações locais (ex: banco de dados).
👉 Ele **não é versionado no Git** por conter dados sensíveis.

---

## 📁 Estrutura de configuração

```text
src/main/resources/
├── application.yaml
├── application-dev.yaml (NÃO versionado)
├── application-example.yaml (versionado)
```

---

## 🧠 Como funciona

* `application.yaml` → define qual perfil usar
* `application-dev.yaml` → configuração local do desenvolvedor
* `application-example.yaml` → modelo para referência

---

## 🚨 Pré-requisitos (OBRIGATÓRIO)

Antes de rodar o projeto, você precisa:

### ✔ Ter o PostgreSQL instalado e em execução

👉 O banco precisa estar rodando localmente

---

### ✔ Criar o banco de dados com o nome EXATO:

```text
nuprecin
```

Se o banco não existir, a aplicação **não irá subir**.

---

### Exemplo de criação do banco:

```sql
CREATE DATABASE nuprecin;
```

---

## 🚀 Como configurar o ambiente local

### 1. Criar o arquivo local

Copie o arquivo de exemplo:

```bash
cp src/main/resources/application-example.yaml src/main/resources/application-dev.yaml
```

---

### 2. Preencher com seus dados

Exemplo:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nuprecin
    username: seu_usuario
    password: sua_senha
```

---

### 3. Perfil ativo

O projeto já está configurado para usar o perfil `dev`:

```yaml
spring:
  profiles:
    active: dev
```

---

## ⚠️ Regras IMPORTANTES

### 1. Nunca versionar o application-dev.yaml ❌

Este arquivo está no `.gitignore`.

👉 Não deve ser commitado.

---

### 2. Nunca colocar dados reais no application-example.yaml ❌

👉 Ele deve conter apenas dados genéricos:

```yaml
username: "seu usuário"
password: "sua senha"
```

---

### 3. Cada dev usa sua própria configuração ✔️

* banco local próprio
* credenciais próprias

---

## 🧪 Testando a configuração

Ao rodar o projeto:

* o Spring carregará automaticamente o `application-dev.yaml`
* o Flyway irá rodar as migrations
* se tudo estiver correto, a aplicação sobe normalmente

---

## ❗ Problemas comuns

### ❌ Erro de conexão com banco

Verifique:

* PostgreSQL está rodando?
* O DataBase `nuprecin` foi criado?
* Usuário e senha estão corretos?

---

### ❌ Aplicação não sobe

Verifique:

* `application-dev.yaml` foi criado?
* O perfil `dev` está ativo?

---

## 🎯 Resumo

✔ PostgreSQL deve estar rodando
✔ DataBase `nuprecin` deve existir
✔ Configurar `application-dev.yaml` corretamente
✔ Não commitar dados sensíveis

---
