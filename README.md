# AGTECH — Plataforma de Gestão Rural

Plataforma web para centralizar a gestão operacional e administrativa de
propriedades rurais. O projeto está sendo desenvolvido de forma incremental,
com um backend Java responsável pelas regras de negócio e um frontend React
responsável pela experiência do usuário.

> Gigantesco no destino, pequeno em cada entrega.

## Estado atual

O projeto está na fase de fundação da V0.1. Já estão implementados:

- cadastro, consulta, atualização, ativação e desativação de usuários;
- autenticação stateless com JWT;
- cadastro, consulta, atualização, ativação e desativação de propriedades;
- vínculo entre usuários e propriedades com papéis de acesso;
- listagem das propriedades vinculadas ao usuário autenticado;
- concessão, consulta e remoção de acessos a propriedades;
- migrations do banco com Flyway;
- validação de entrada e respostas de erro padronizadas;
- frontend com cadastro de conta, login e logout;
- tratamento de sessão expirada no frontend;
- dashboard responsivo de propriedades;
- cadastro de propriedade pelo frontend.

Ainda não fazem parte da implementação atual:

- talhões;
- culturas;
- safras;
- plantios;
- serviço de analytics em Python;
- modelos de machine learning.

Esses módulos serão adicionados apenas quando entrarem formalmente no escopo
de uma entrega.

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
   +---- JPA / Hibernate ----> Banco de dados
   |
   +---- Flyway ------------> Versionamento do schema
```

O backend é a fonte de verdade para dados operacionais, autenticação,
autorização e regras de negócio. O frontend não substitui verificações de
segurança do servidor.

O projeto começa como um monólito modular. Microserviços, mensageria, cache e
outros componentes de infraestrutura só serão considerados diante de uma
necessidade concreta e mensurável.

## Tecnologias

### Backend

- Java 26;
- Spring Boot 4;
- Spring Web MVC;
- Spring Data JPA;
- Spring Security;
- Bean Validation;
- JWT com JJWT;
- Flyway;
- H2 no ambiente atual;
- Maven Wrapper;
- JUnit e Spring Test.

PostgreSQL é o banco definido para a evolução do projeto, mas a configuração
atual ainda utiliza o H2 fornecido pelo Spring Boot. A migração para
PostgreSQL deve preservar as migrations e ser feita em uma entrega própria.

### Frontend

- React 19;
- TypeScript;
- React Router;
- Vite;
- CSS responsivo;
- Oxlint.

### Dados e analytics planejados

- Python;
- FastAPI;
- Pandas ou Polars;
- NumPy;
- scikit-learn.

O serviço Python ainda não foi criado. Ele será usado somente para análises,
processamento de dados e modelos que possuam objetivo e métricas definidos.

## Estrutura do repositório

```text
AGTECH/
├── backend/             # API Spring Boot e regras de negócio
├── frontend/            # aplicação React
├── docs/
│   └── architecture/    # decisões e documentação arquitetural
├── AGENTS.md            # contexto e regras permanentes do projeto
└── README.md
```

No futuro, o serviço de dados poderá ser criado em `analytics-python/` quando
houver um requisito analítico concreto.

## Domínio implementado

### Usuário

Representa uma pessoa que acessa a plataforma. O e-mail é único e utilizado
na autenticação. Senhas são armazenadas pelo backend como hash BCrypt.

### Propriedade rural

Representa uma fazenda ou unidade produtiva. Nesta fase possui nome,
município, estado, área total em hectares e situação ativa/inativa.

### Acesso à propriedade

`UsuarioPropriedadeAcesso` representa o vínculo entre usuário e propriedade.
Os papéis atuais são:

- `PROPRIETARIO`;
- `GESTOR`;
- `OPERADOR`.

Ao cadastrar uma propriedade, o usuário autenticado recebe o papel de
`PROPRIETARIO`. A autorização deve sempre considerar o acesso ao recurso, e
não somente a autenticação ou um papel global.

Mais detalhes estão no
[ADR de modelagem da V0.1](docs/architecture/0001-modelagem-v0.1-fundacao.md).

## Pré-requisitos

- Java 26;
- Node.js compatível com a versão do Vite do projeto;
- npm.

Não é necessário instalar o Maven globalmente, pois o projeto inclui Maven
Wrapper.

## Configuração do backend

O backend exige a variável de ambiente `JWT_SECRET`. Utilize um segredo longo
e aleatório e nunca o versione no repositório.

Linux/macOS:

```bash
export JWT_SECRET="substitua-por-um-segredo-longo-e-aleatorio"
```

PowerShell:

```powershell
$env:JWT_SECRET="substitua-por-um-segredo-longo-e-aleatorio"
```

A validade do token é de uma hora por padrão. Ela pode ser alterada em
milissegundos com:

```bash
export JWT_EXPIRATION_MS=3600000
```

## Executando o backend

Linux/macOS:

```bash
cd backend
./mvnw spring-boot:run
```

Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

A API será disponibilizada, por padrão, em:

```text
http://localhost:8080
```

Ao iniciar a aplicação, o Flyway aplica as migrations existentes em
`backend/src/main/resources/db/migration`.

## Executando o frontend

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

O frontend será disponibilizado, por padrão, em:

```text
http://localhost:5173
```

Atualmente, a URL da API está configurada no cliente HTTP do frontend como
`http://localhost:8080`.

## Rotas do frontend

| Rota | Acesso | Descrição |
| --- | --- | --- |
| `/` | Público | Login |
| `/cadastro` | Público | Cadastro de conta |
| `/propriedades` | Autenticado | Dashboard e listagem de propriedades |
| `/propriedades/nova` | Autenticado | Cadastro de propriedade |

## API disponível

### Autenticação

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/auth/login` | Autentica um usuário e retorna um JWT |

### Usuários

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/usuarios` | Cadastra uma conta |
| `GET` | `/api/usuarios/{id}` | Consulta um usuário |
| `PUT` | `/api/usuarios/{id}` | Atualiza um usuário |
| `PATCH` | `/api/usuarios/{id}/ativar` | Ativa um usuário |
| `PATCH` | `/api/usuarios/{id}/desativar` | Desativa um usuário |

### Propriedades

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/propriedades` | Cadastra uma propriedade |
| `GET` | `/api/propriedades/minhas` | Lista propriedades acessíveis ao usuário |
| `GET` | `/api/propriedades/{id}` | Consulta uma propriedade |
| `PUT` | `/api/propriedades/{id}` | Atualiza uma propriedade |
| `PATCH` | `/api/propriedades/{id}/ativar` | Ativa uma propriedade |
| `PATCH` | `/api/propriedades/{id}/desativar` | Desativa uma propriedade |

### Acessos a propriedades

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/propriedades/{id}/acessos` | Concede acesso a um usuário |
| `GET` | `/api/propriedades/{id}/acessos` | Lista os acessos da propriedade |
| `DELETE` | `/api/propriedades/{id}/acessos/{acessoId}` | Remove um acesso |

Com exceção do login e do cadastro de conta, as rotas da API exigem o token:

```http
Authorization: Bearer <token>
```

## Respostas de erro

O backend utiliza um formato padronizado:

```json
{
  "timestamp": "2026-08-29T12:00:00Z",
  "status": 422,
  "erro": "Unprocessable Content",
  "mensagem": "Descrição segura do erro.",
  "caminho": "/api/recurso"
}
```

Erros inesperados são registrados no servidor, mas detalhes internos não são
expostos ao cliente.

## Testes e verificações

Backend:

```bash
cd backend
./mvnw test
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

O frontend ainda não possui suíte automatizada de testes. Ela deverá ser
adicionada conforme os fluxos crescerem.

## Migrations

As migrations existentes são:

| Versão | Descrição |
| --- | --- |
| `V1` | Criação da tabela de usuários |
| `V2` | Criação da tabela de propriedades rurais |
| `V3` | Criação do vínculo entre usuários e propriedades |

Mudanças persistentes no schema devem sempre ser feitas por uma nova
migration. Migrations já aplicadas não devem ser alteradas retroativamente.

## Segurança

- senhas são processadas com BCrypt no backend;
- a API utiliza autenticação stateless com JWT;
- CORS permite o frontend local em `http://localhost:5173`;
- segredos devem permanecer em variáveis de ambiente;
- o frontend remove a sessão local quando recebe `401`;
- o token está atualmente armazenado no `localStorage`;
- autorização de negócio deve ser validada pelo backend para cada recurso.

## Roadmap resumido

Próximas etapas planejadas da V0.1:

1. concluir e reforçar a autorização por propriedade;
2. implementar talhões;
3. implementar culturas;
4. implementar safras;
5. implementar plantios;
6. ampliar testes de API e segurança.

Módulos de estoque, operações agrícolas, máquinas, pecuária, financeiro,
analytics e machine learning pertencem a versões futuras e não devem ser
implementados antecipadamente.

## Diretrizes de contribuição

Antes de propor ou alterar código, leia o [AGENTS.md](AGENTS.md). As regras
centrais são:

- fazer mudanças pequenas, coesas e testáveis;
- evitar arquitetura especulativa;
- manter controllers finos e regras no domínio/serviços;
- validar entradas e tratar erros;
- preservar os contratos entre frontend e backend;
- não incluir credenciais ou segredos;
- criar migrations para alterações no banco;
- executar os testes e verificações relevantes;
- não fazer `git push` sem solicitação explícita.
