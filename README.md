# AGTECH — Plataforma de Gestão Rural

Plataforma web para centralizar a gestão de propriedades rurais. O projeto é
construído de forma incremental. A V0.1 estabeleceu a fundação com usuários,
autenticação, propriedades, talhões, culturas, safras e plantios.

> Gigantesco no destino, pequeno em cada entrega.

## Sobre o projeto

O AGTECH pretende evoluir para uma plataforma capaz de reunir informações
operacionais, administrativas, financeiras e analíticas do meio rural. O
backend Java é a fonte de verdade das regras de negócio; o frontend React é
responsável pela experiência do usuário.

O projeto começa como um monólito modular. Novos serviços, integrações e
machine learning serão adicionados somente quando houver requisitos concretos.

## Estado atual — V0.1 concluída

A primeira versão está funcionalmente concluída. Todas as entidades da
fundação usam UUID como identificador e as operações vinculadas a propriedades
validam o acesso ao recurso.

### Backend

- usuários: cadastro, consulta, atualização e controle de status;
- autenticação stateless com JWT;
- propriedades: cadastro, consulta, atualização e controle de status;
- vínculo entre usuários e propriedades com os papéis `PROPRIETARIO`,
  `GESTOR` e `OPERADOR`;
- concessão, listagem e remoção de acessos;
- talhões: cadastro, consulta, atualização e controle de status;
- validação de acesso e pertencimento dos talhões à propriedade;
- cadastro e listagem de culturas;
- safras: cadastro, consulta e transições entre planejada, em andamento,
  finalizada e cancelada;
- plantios: cadastro, consulta e atualização;
- autorização em cascata entre propriedade, talhão, safra e plantio;
- Bean Validation, tratamento global de erros e migrations Flyway.

### Frontend

- cadastro de conta, login e logout;
- proteção de rotas e tratamento de sessão expirada;
- dashboard responsivo de propriedades;
- cadastro, detalhes e edição de propriedades;
- cadastro, listagem e edição de talhões;
- ativação e desativação de talhões com confirmação;
- cadastro e listagem de culturas;
- cadastro e listagem de safras por talhão;
- início, finalização e cancelamento de safras;
- cadastro, listagem e edição de plantios por safra;
- estados de carregamento, erro, sucesso e conteúdo vazio.

### Próxima versão

A V0.2 será dedicada a insumos e estoque, começando pela modelagem de produtos
e depósitos. Entradas, saídas e movimentações serão adicionadas de forma
incremental, sem antecipar módulos futuros.

## Arquitetura

```text
Navegador
   |
   | HTTP/JSON + JWT
   v
Frontend React
   |
   | API REST
   v
Backend Spring Boot
   |
   +---- JPA / Hibernate ----> H2
   +---- Flyway ------------> migrations
```

O backend controla autenticação, autorização, persistência e regras de
negócio. A proteção de rotas do frontend não substitui as verificações de
segurança do servidor. PostgreSQL é o banco planejado para a evolução do
projeto; a configuração atual utiliza H2 embarcado.

## Tecnologias

### Backend

- Java 26 e Spring Boot 4.1;
- Spring Web MVC, Data JPA e Security;
- Bean Validation;
- JWT com JJWT;
- Flyway e H2;
- Maven Wrapper, JUnit e Spring Test.

### Frontend

- React 19 e TypeScript 6;
- React Router 7;
- Vite 8;
- CSS responsivo;
- Oxlint.

### Dados — planejado

Python, FastAPI, Pandas ou Polars, NumPy e scikit-learn. O serviço Python
ainda não existe e será reservado para análises e modelos com objetivos e
métricas definidos.

## Estrutura

```text
AGTECH/
├── backend/                 # API, domínio e regras de negócio
│   └── src/
│       ├── main/java/       # controllers, services, models e segurança
│       ├── main/resources/  # configuração e migrations
│       └── test/            # testes automatizados
├── frontend/                # aplicação React e TypeScript
│   └── src/
│       ├── api/             # cliente HTTP e contratos
│       ├── components/      # componentes e layout
│       ├── pages/           # páginas da aplicação
│       └── styles/          # estilos globais e por fluxo
├── docs/architecture/       # decisões arquiteturais
├── scripts/                 # utilitários de desenvolvimento
├── AGENTS.md                # regras permanentes do projeto
└── README.md
```

## Domínio atual

- **Usuário:** pessoa que acessa a plataforma. O e-mail é único e a senha é
  armazenada como hash BCrypt.
- **Propriedade rural:** fazenda ou unidade produtiva com localização, área
  total em hectares e status.
- **Acesso à propriedade:** vínculo entre usuário e propriedade. Ao criar uma
  propriedade, o usuário recebe o papel `PROPRIETARIO`.
- **Talhão:** divisão produtiva pertencente a uma propriedade, com nome, área
  em hectares e status.
- **Cultura:** cultura agrícola identificada por nome único, disponível na API
  e no catálogo do frontend.
- **Safra:** ciclo produtivo vinculado a um talhão e uma cultura. O ciclo
  começa planejado, pode entrar em andamento e termina como finalizado ou
  cancelado.
- **Plantio:** registro operacional de uma safra, contendo data, área plantada
  em hectares e observações opcionais.

Consulte o [ADR de modelagem da V0.1](docs/architecture/0001-modelagem-v0.1-fundacao.md)
para conhecer as decisões de domínio e autorização.

## Pré-requisitos

- Java 26;
- Node.js compatível com Vite 8;
- npm.

Não é necessário instalar Maven globalmente: o projeto inclui o wrapper.

## Configuração

O backend exige `JWT_SECRET`. Use um segredo longo e aleatório e nunca o
adicione ao repositório.

Linux/macOS:

```bash
export JWT_SECRET="substitua-por-um-segredo-longo-e-aleatorio"
```

PowerShell:

```powershell
$env:JWT_SECRET="substitua-por-um-segredo-longo-e-aleatorio"
```

O token expira em uma hora por padrão. Para alterar o período em
milissegundos:

```bash
export JWT_EXPIRATION_MS=3600000
```

## Como executar

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

No Windows, use `./mvnw.cmd spring-boot:run`. A API ficará disponível em
`http://localhost:8080`. O Flyway aplicará as migrations automaticamente.

### Frontend

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

A interface ficará disponível em `http://localhost:5173`. O cliente HTTP
aponta atualmente para `http://localhost:8080`.

## Rotas do frontend

| Rota | Acesso | Descrição |
| --- | --- | --- |
| `/` | Público | Login |
| `/cadastro` | Público | Cadastro de conta |
| `/propriedades` | Autenticado | Dashboard de propriedades |
| `/propriedades/nova` | Autenticado | Cadastro de propriedade |
| `/propriedades/:id` | Autenticado | Detalhes e talhões |
| `/propriedades/:id/editar` | Autenticado | Edição da propriedade |
| `/propriedades/:propriedadeId/talhoes/novo` | Autenticado | Cadastro de talhão |
| `/propriedades/:propriedadeId/talhoes/:talhaoId/editar` | Autenticado | Edição de talhão |
| `/culturas` | Autenticado | Catálogo de culturas |
| `/culturas/nova` | Autenticado | Cadastro de cultura |
| `/propriedades/:propriedadeId/talhoes/:talhaoId/safras` | Autenticado | Safras do talhão |
| `/propriedades/:propriedadeId/talhoes/:talhaoId/safras/nova` | Autenticado | Cadastro de safra |
| `/propriedades/:propriedadeId/talhoes/:talhaoId/safras/:safraId/plantios` | Autenticado | Plantios da safra |
| `/propriedades/:propriedadeId/talhoes/:talhaoId/safras/:safraId/plantios/novo` | Autenticado | Cadastro de plantio |
| `/propriedades/:propriedadeId/talhoes/:talhaoId/safras/:safraId/plantios/:plantioId/editar` | Autenticado | Edição de plantio |

Rotas desconhecidas são redirecionadas para o login.

## Endpoints da API

### Autenticação e usuários

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/auth/login` | Autentica e retorna um JWT |
| `POST` | `/api/usuarios` | Cadastra uma conta |
| `GET` | `/api/usuarios/{id}` | Consulta um usuário |
| `PUT` | `/api/usuarios/{id}` | Atualiza um usuário |
| `PATCH` | `/api/usuarios/{id}/ativar` | Ativa um usuário |
| `PATCH` | `/api/usuarios/{id}/desativar` | Desativa um usuário |

### Propriedades e acessos

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/propriedades` | Cadastra uma propriedade |
| `GET` | `/api/propriedades/minhas` | Lista propriedades acessíveis |
| `GET` | `/api/propriedades/{id}` | Consulta uma propriedade |
| `PUT` | `/api/propriedades/{id}` | Atualiza uma propriedade |
| `PATCH` | `/api/propriedades/{id}/ativar` | Ativa uma propriedade |
| `PATCH` | `/api/propriedades/{id}/desativar` | Desativa uma propriedade |
| `POST` | `/api/propriedades/{id}/acessos` | Concede acesso |
| `GET` | `/api/propriedades/{id}/acessos` | Lista acessos |
| `DELETE` | `/api/propriedades/{id}/acessos/{acessoId}` | Remove um acesso |

### Talhões

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/propriedades/{propriedadeId}/talhoes` | Cadastra |
| `GET` | `/api/propriedades/{propriedadeId}/talhoes` | Lista |
| `GET` | `/api/propriedades/{propriedadeId}/talhoes/{talhaoId}` | Consulta |
| `PUT` | `/api/propriedades/{propriedadeId}/talhoes/{talhaoId}` | Atualiza |
| `PATCH` | `/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/ativar` | Ativa |
| `PATCH` | `/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/desativar` | Desativa |

### Culturas

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/culturas` | Cadastra uma cultura |
| `GET` | `/api/culturas` | Lista culturas |

### Safras

Base: `/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras`

| Método | Endpoint relativo | Descrição |
| --- | --- | --- |
| `POST` | `/` | Cadastra uma safra planejada |
| `GET` | `/` | Lista as safras do talhão |
| `GET` | `/{safraId}` | Consulta uma safra |
| `PATCH` | `/{safraId}/iniciar` | Inicia uma safra planejada |
| `PATCH` | `/{safraId}/finalizar` | Finaliza uma safra em andamento |
| `PATCH` | `/{safraId}/cancelar` | Cancela uma safra não encerrada |

Transições permitidas:

```text
PLANEJADA ----> EM_ANDAMENTO ----> FINALIZADA
    |                |
    +----------------+-----------> CANCELADA
```

### Plantios

Base:
`/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras/{safraId}/plantios`

| Método | Endpoint relativo | Descrição |
| --- | --- | --- |
| `POST` | `/` | Registra um plantio |
| `GET` | `/` | Lista os plantios da safra |
| `GET` | `/{plantioId}` | Consulta um plantio |
| `PUT` | `/{plantioId}` | Atualiza data, área e observações |

Somente o login e o cadastro de conta são públicos. As outras chamadas devem
enviar:

```http
Authorization: Bearer <token>
```

## Tratamento de erros

O backend retorna erros em um formato padronizado:

```json
{
  "timestamp": "2026-09-04T12:00:00Z",
  "status": 422,
  "erro": "Unprocessable Content",
  "mensagem": "Descrição segura do erro.",
  "caminho": "/api/recurso"
}
```

O frontend apresenta `mensagem` ao usuário. Ao receber `401`, remove o token
local e redireciona para o login.

## Migrations

| Versão | Descrição |
| --- | --- |
| `V1` | Usuários |
| `V2` | Propriedades rurais |
| `V3` | Vínculo entre usuários e propriedades |
| `V4` | Talhões |
| `V5` | Culturas |
| `V6` | Safras |
| `V7` | Plantios |

Toda mudança de schema deve usar uma nova migration. Não altere migrations já
aplicadas.

## Testes e verificações

```bash
# backend
cd backend
./mvnw test

# frontend
cd frontend
npm run lint
npm run build
```

O frontend ainda não possui uma suíte automatizada de testes.

## Segurança

- senhas são processadas com BCrypt;
- a API usa sessão stateless e JWT;
- o CORS permite `http://localhost:5173`;
- o frontend remove a sessão ao receber `401`;
- o token é armazenado atualmente no `localStorage`;
- segredos e credenciais não devem ser versionados;
- toda autorização de negócio deve ser validada pelo backend.

## Roadmap resumido

### V0.1 — Fundação ✅

Usuários, autenticação, propriedades, acessos, talhões, culturas, safras e
plantios.

### V0.2 — Insumos e estoque

1. produtos e categorias necessárias;
2. depósitos;
3. entradas e saídas;
4. movimentações e rastreabilidade;
5. integração do consumo de insumos com atividades ou safras quando houver um
   requisito definido.

Máquinas, pecuária, financeiro, analytics e machine learning permanecem em
versões futuras.

## Contribuição

Leia o [AGENTS.md](AGENTS.md) antes de alterar o projeto. Faça mudanças
pequenas e testáveis, mantenha as responsabilidades separadas, preserve os
contratos entre frontend e backend, use migrations para o banco, não versione
segredos e execute as verificações relevantes antes de concluir.
