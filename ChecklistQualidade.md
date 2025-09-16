# Checklist de Qualidade — Sistema de Gestão de Projetos e Equipes (Java + OO)

> Versão: 1.0 • Data: 16/09/2025 • Aprovador: André Bonnano

## 1) POO aplicada
- [ ] Entidades coesas: `Usuario`, `Projeto`, `Equipe`, `AlocacaoEquipeProjeto`, `Tarefa`
- [ ] Enums: `Perfil`, `StatusProjeto`, `StatusTarefa`, `PrioridadeTarefa`
- [ ] Encapsulamento (atributos privados, métodos de intenção; evitar setters genéricos)
- [ ] Regras de negócio dentro das entidades (invariantes preservados)
- [ ] Validações: CPF, e-mail, datas (`dataTerminoPrevista ≥ dataInicio`)

## 2) Arquitetura (MVC/por camadas)
- [ ] Camadas separadas: **domínio**, **repositórios**, **serviços**, **UI (console)**
- [ ] Dependências por **interfaces** (DIP) e baixo acoplamento
- [ ] Controller não contém regras de negócio; View só exibe/recebe dados

## 3) Funcionalidades
- [ ] Autenticação/login e **perfis** (Administrador, Gerente, Colaborador)
- [ ] CRUD Usuários (Admin)
- [ ] CRUD Projetos + definição de **gerente responsável**
- [ ] CRUD Equipes + vínculo de **membros**
- [ ] **Alocação** Equipe↔Projeto (datas, capacidade/semana)
- [ ] CRUD Tarefas + atribuição de **responsável** + fluxo de **status**
- [ ] Listagens e **filtros** (projeto, equipe, responsável, status, atraso)
- [ ] **Relatórios**: progresso (% concluído), produtividade por usuário, atrasos, capacidade vs. carga

## 4) Código & Boas práticas
- [ ] Nomes claros e consistentes (classes, métodos, variáveis)
- [ ] Métodos curtos e coesos; mensagens de erro úteis
- [ ] Comentários sucintos (explique *por quê*, não o óbvio)
- [ ] Uso de `java.time` e coleções (`List`/`Map`)
- [ ] Tratamento de exceções e feedback no console

## 5) Testes & Qualidade
- [ ] Compila sem warnings
- [ ] Casos de teste críticos (validações, permissões, fluxo de status)
- [ ] JUnit para VOs e regras centrais (opcional/bônus)

## 6) Documentação & Entrega
- [ ] **README**: requisitos, como compilar/rodar, usuários de exemplo, casos de uso
- [ ] **Diagrama de classes** atualizado e coerente com o código
- [ ] Estrutura do repositório organizada (`src/`, `docs/`, etc.)
- [ ] Exemplos de execução e, se possível, **seed** de dados

## 7) Demo (apresentação)
- [ ] Roteiro de demonstração (login Admin → cadastros → tarefas → relatórios)
- [ ] Usuários de teste (logins/senhas) e projetos/tarefas pré-cadastrados
- [ ] Prints/GIFs ou gravação curta do fluxo (opcional)

## 8) Bônus (não obrigatório)
- [ ] Exportação CSV de relatórios
- [ ] Persistência em arquivo (CSV/JSON)
- [ ] Log simples de alterações
- [ ] Métricas extras (burndown textual, taxa de atraso)
