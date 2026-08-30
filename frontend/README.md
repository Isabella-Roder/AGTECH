# AGTECH Frontend

Interface web da plataforma AGTECH para gestão de propriedades rurais. O
frontend consome a API REST do backend Spring Boot e, nesta etapa, cobre os
fluxos de autenticação, cadastro de conta e gestão inicial de propriedades.

## Funcionalidades atuais

- cadastro de conta;
- login com autenticação JWT;
- proteção de rotas autenticadas;
- encerramento de sessão;
- tratamento de sessão expirada;
- listagem das propriedades vinculadas ao usuário;
- resumo com quantidade de propriedades, propriedades ativas e área total;
- cadastro de propriedade rural;
- estados visuais de carregamento, erro, sucesso e lista vazia;
- layout responsivo para desktop e dispositivos móveis.

## Tecnologias

- React 19;
- TypeScript;
- React Router;
- Vite;
- CSS;
- Oxlint.

## Pré-requisitos

- Node.js em uma versão compatível com o Vite configurado no projeto;
- npm;
- backend AGTECH em execução.

Atualmente, o cliente HTTP utiliza a API em:

```text
http://localhost:8080
```

O backend deve permitir requisições originadas de:

```text
http://localhost:5173
```

## Instalação

Na pasta `frontend`, instale as dependências:

```bash
npm install
```

## Execução em desenvolvimento

Com o backend em execução na porta `8080`, inicie o frontend:

```bash
npm run dev
```

A aplicação estará disponível, por padrão, em:

```text
http://localhost:5173
```

## Scripts

```bash
# inicia o servidor de desenvolvimento
npm run dev

# verifica o código com o linter
npm run lint

# valida o TypeScript e gera o build de produção
npm run build

# serve localmente o build gerado
npm run preview
```

Antes de entregar uma alteração, execute:

```bash
npm run lint
npm run build
```

## Rotas

| Rota | Acesso | Descrição |
| --- | --- | --- |
| `/` | Público | Login |
| `/cadastro` | Público | Cadastro de conta |
| `/propriedades` | Autenticado | Dashboard e listagem de propriedades |
| `/propriedades/nova` | Autenticado | Cadastro de propriedade |

Rotas desconhecidas são redirecionadas para o login.

## Estrutura principal

```text
src/
├── api/                 # cliente HTTP e contratos de integração
├── assets/              # imagens e recursos estáticos
├── components/
│   └── layout/          # layout das áreas autenticadas
├── pages/               # páginas associadas às rotas
├── styles/              # estilos específicos das páginas
├── RotaProtegida.tsx    # controle de acesso às rotas autenticadas
├── index.css            # estilos globais
└── main.tsx             # inicialização e definição das rotas
```

## Integração com o backend

As chamadas HTTP são centralizadas em `src/api/cliente.ts`. O token JWT é
enviado no cabeçalho das chamadas autenticadas:

```http
Authorization: Bearer <token>
```

Quando uma chamada autenticada retorna `401`, o token local é removido e o
usuário é encaminhado novamente ao login.

Os erros retornados pela API devem seguir o contrato que contém o campo
`mensagem`, utilizado para apresentar uma descrição segura ao usuário.

## Segurança

O frontend ajuda a controlar a navegação, mas não é autoridade de
autorização. O backend deve validar o usuário autenticado e seu acesso a cada
propriedade em todas as operações protegidas.

O token está armazenado atualmente no `localStorage`. Não devem ser
armazenadas senhas, credenciais ou outras informações sensíveis no código ou
nos arquivos versionados.
