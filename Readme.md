# SGPE — Sistema de Gestão de Projetos e Equipes (Java + OO — Console)

> **Resumo:** Aplicação para cadastrar **usuários, equipes, projetos e tarefas**, controlar **alocações**, acompanhar **status e prazos** e gerar **relatórios** (progresso, produtividade, atrasos e capacidade). Projeto didático, seguindo POO e arquitetura em camadas (estilo MVC).

---

## ✨ Funcionalidades

* **Autenticação** por login/senha (hash simulado) e **perfis**: Administrador, Gerente, Colaborador.
* **Usuários**: CRUD (Admin); validações de CPF e e-mail; perfis e cargos.
* **Projetos**: CRUD; gerente responsável; status (Planejado/Em andamento/Concluído/Cancelado).
* **Equipes**: CRUD; membros (usuários); uma equipe atua em vários projetos.
* **Alocação Equipe↔Projeto**: período e capacidade (horas/semana).
* **Tarefas**: CRUD; responsável; prioridade; fluxo de status; esforço estimado/real.
* **Relatórios**: progresso do projeto, produtividade por usuário, tarefas em atraso, capacidade vs. carga.
* **Bônus (opcional)**: exportar CSV, persistência em arquivo, logs simples.

---

## 🧱 Arquitetura (camadas / MVC simples)

* **Domínio (Model)**: entidades de negócio, enums, VOs e regras/invariantes.
* **Repositórios**: interfaces + implementação em memória (`List/Map`); ponto de troca para arquivo/DB.
* **Serviços (Controller)**: orquestra casos de uso, validações cruzadas, autorização por perfil.
* **UI de Console (View)**: menus, entrada/saída; sem regra de negócio.

```
br.com.sgpe
  ├── dominio/              (Usuario, Projeto, Equipe, Tarefa, etc.)
  ├── repositorio/          (RepositorioUsuario, ... + InMemory)
  ├── servico/              (ServicoUsuario, ServicoProjeto, ...)
  ├── relatorio/            (GeradorRelatorios, formatters)
  ├── ui/                   (ConsoleMenu)
  └── App.java              (bootstrap/injeção simples)
```

---

## 🧠 Modelo de Domínio

**Entidades:** `Usuario`, `Projeto`, `Equipe`, `AlocacaoEquipeProjeto`, `Tarefa`
**Enums:** `Perfil`, `StatusProjeto`, `StatusTarefa`, `PrioridadeTarefa`
**Value Objects (opcional):** `CPF`, `Email` (imutáveis, validam no construtor)

**Regras-chave (exemplos)**

* `dataTerminoPrevista ≥ dataInicio` (Projeto e Tarefa).
* Tarefa só **CONCLUIDA** com `dataConclusao` e `esforcoRealHoras`.
* Gerente responsável deve ter **perfil GERENTE ou ADMIN**.
* Não exceder **capacidade semanal** da equipe (relatório aponta risco).

---

## ✅ Requisitos

* **JDK 17+**
* **IDE** (IntelliJ) ou editor + terminal
* **SO**: Windows
* **Git**: GitHub 

---

## ▶️ Como compilar e executar

### Opção A — Terminal (sem build tool)

```bash
# Na raiz do projeto:
javac -d out $(find src -name "*.java")
java -cp out br.com.sgpe.App
```

### Opção B — Gradle (opcional)

```bash
# build.gradle (trecho)
plugins { id 'application' }
java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }
application { mainClass = 'br.com.sgpe.App' }

# comandos
gradle run
```

---

## 🔐 Perfis e Permissões (resumo)

| Ação                           | Admin | Gerente | Colaborador |
| ------------------------------ | :---: | :-----: | :---------: |
| CRUD Usuários                  |   ✔️  |    ❌    |      ❌      |
| CRUD Projetos                  |   ✔️  |    ✔️   |      ❌      |
| Definir Gerente de Projeto     |   ✔️  |    ❌    |      ❌      |
| CRUD Equipes                   |   ✔️  |    ✔️   |      ❌      |
| Alocar Equipe↔Projeto          |   ✔️  |    ✔️   |      ❌      |
| CRUD Tarefas                   |   ✔️  |    ✔️   |      ❌      |
| Mudar Status da própria tarefa |   ✔️  |    ✔️   |      ✔️     |
| Relatórios                     |   ✔️  |    ✔️   |  ✔️ (limit) |

---

## 🧪 Execução de exemplo (roteiro de demo)

1. **Login (Admin)** → cadastrar usuários (gerente, colaboradores) e equipes.
2. **Projetos** → criar e definir **Gerente responsável**.
3. **Equipes** → alocar ao projeto (datas + capacidade).
4. **Tarefas** → criar, atribuir a responsáveis, definir estimativas e prazos.
5. **Relatórios** → progresso, atrasos, produtividade, capacidade vs. carga.
6. **Login (Colaborador)** → atualizar status e esforço real; verificar reflexo nos relatórios.

---

## 📝 Dados de exemplo (seeds)

* **Usuários**:

    * admin / `admin123` (ADMIN)
    * maria.gerente / `gerente123` (GERENTE)
    * joao.dev / `dev123` (COLABORADOR)
* **Projeto**: “Portal Interno 2.0” (Planejado → Em andamento)
* **Equipe**: “Time A” (membros: gerente + dev)
* **Tarefas**: “Setup inicial”, “Modelagem domínio”, “Console UI”

*(As senhas são meramente ilustrativas; no código, usar `senhaHash` simulado.)*

---

## 📊 Relatórios (saída no console)

* **Progresso do Projeto**: `%concluído = concluídas / total`.
* **Produtividade por Usuário**: tarefas concluídas, esforço real somado (período).
* **Atrasos**: tarefas com prazo vencido e não concluídas.
* **Capacidade vs. Carga**: horas disponíveis x horas estimadas do período.

*(Opcional)* **Exportar CSV** em `./out/reports/`.

---

## 🧩 Boas práticas adotadas

* **POO**: encapsulamento, métodos de intenção, invariantes no domínio.
* **SOLID** (nível introdutório): SRP nos serviços, Strategy para notificações/formatters.
* **Baixo acoplamento**: dependências por **interfaces**.
* **Tratamento de erros**: mensagens amigáveis no console.

---

## 🧪 Testes (opcional/bônus)

* **JUnit** para VOs (`CPF`, `Email`) e regras críticas (datas/fluxos).
* Casos de borda: login duplicado, status inválido, prazos incoerentes.

---

## 🗂️ Estrutura do repositório

```
/src/main/java/br/com/sgpe/...
/docs/diagrama-classes.puml (ou .png/.svg)
/out/ (compilação e relatórios, ignorar no Git)
/README.md
/LICENSE (opcional)
/.gitignore
```

---

## 📅 Relatórios semanais & Sprint Backlog (exigência acadêmica)

* Entregar **relato semanal**: o que foi planejado, feito, pendente e riscos.
* Manter **Sprint Backlog** da semana atual (tarefas, responsáveis, status, impedimentos).

---

## 🚧 Limitações & Próximos passos

* Persistência padrão em memória (trocar para CSV/JSON/DB é evolução natural).
* Notificação apenas em console (pode virar e-mail/arquivo).
* Sem concorrência/multiusuário (escopo didático).

---

## 🤝 Contribuição

* Abrir PRs com descrição clara e checklist de testes.
* Seguir padrão de pacotes e nomes; evitar métodos > 25–30 linhas.

---

## 📜 Licença

Defina a licença do projeto (ex.: MIT).

---

## 👤 Autor

**André Bonnano** — Projeto acadêmico (Gestão da TI).
Contato: *[seu-email@exemplo.com](mailto:seu-email@exemplo.com)* (opcional)

---
