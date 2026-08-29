# AGENTS.md --- Diretrizes do Projeto de Gestão Rural

## 1. Objetivo deste arquivo

Este arquivo define o contexto permanente do projeto e as regras que
qualquer agente de IA deve seguir ao trabalhar neste repositório.

A IA deve ler e respeitar estas instruções antes de propor, gerar ou
alterar código.

O projeto é intencionalmente grande e de longo prazo. A prioridade não é
produzir o máximo de código possível rapidamente, e sim construir uma
base sólida, compreensível, testável e evolutiva.

------------------------------------------------------------------------

# 2. Visão do projeto

Este projeto será uma plataforma completa de gestão rural, pensada para
crescer até se tornar um sistema de grande porte.

A ideia é administrar uma ou várias propriedades rurais e concentrar
informações operacionais, administrativas, financeiras e analíticas em
um único sistema.

O projeto deverá ser maior e mais abrangente que um CRUD convencional.
Ele será desenvolvido de maneira incremental, com módulos independentes
e responsabilidades bem definidas.

A plataforma poderá futuramente atender áreas como:

-   propriedades rurais;
-   fazendas e unidades produtivas;
-   talhões;
-   culturas;
-   plantios;
-   safras;
-   colheitas;
-   produtividade;
-   rebanhos;
-   animais individuais;
-   estoque;
-   insumos;
-   sementes;
-   fertilizantes;
-   defensivos;
-   fornecedores;
-   compras;
-   vendas;
-   clientes;
-   contratos;
-   funcionários;
-   atividades de campo;
-   máquinas e implementos;
-   combustível;
-   manutenção preventiva e corretiva;
-   custos;
-   receitas e despesas;
-   fluxo financeiro;
-   documentos;
-   relatórios;
-   auditoria;
-   alertas;
-   indicadores;
-   análise histórica;
-   análise de dados;
-   previsões;
-   detecção de anomalias;
-   machine learning;
-   integrações externas;
-   meteorologia;
-   sensores e IoT, caso façam sentido futuramente.

Essa lista representa a direção do projeto, não uma obrigação de
implementar tudo imediatamente.

Uma direção futura do produto é permitir que propriedades rurais
diferentes (de usuários/contas diferentes) se comuniquem e comercializem
entre si dentro da própria plataforma — por exemplo, uma propriedade
anunciando excedente de insumo ou produção para outra comprar, ou troca
de mensagens entre gestores de propriedades distintas. Isso é distinto do
módulo de vendas/clientes já listado, que trata de venda para fora do
sistema. Essa capacidade de comunicação/comercialização entre
propriedades é visão de produto, não um requisito da V0.1 — deve ser
modelada e implementada apenas quando entrar formalmente no escopo de
uma versão futura, com atenção especial a autorização (uma propriedade
não deve enxergar dados de outra além do que for explicitamente
compartilhado).

------------------------------------------------------------------------

# 3. Filosofia de desenvolvimento

A regra central é:

> Gigantesco no destino, pequeno em cada entrega.

O projeto deve crescer por versões e módulos.

Não criar dezenas de entidades, serviços e endpoints antecipadamente
apenas porque eles poderão ser necessários no futuro.

Cada funcionalidade deve surgir a partir de um requisito concreto.

Preferir:

1.  entender o requisito;
2.  modelar;
3.  implementar uma pequena parte;
4.  testar;
5.  revisar;
6.  documentar;
7.  integrar;
8.  somente então avançar.

Evitar arquitetura especulativa.

------------------------------------------------------------------------

# 4. Tecnologias principais

## Backend operacional

A aplicação principal será desenvolvida prioritariamente com:

-   Java;
-   Spring Boot;
-   PostgreSQL;
-   APIs REST;
-   migrations de banco de dados;
-   testes automatizados.

Java/Spring Boot será a autoridade sobre as regras de negócio do
sistema.

Exemplos de responsabilidades:

-   autenticação;
-   autorização;
-   usuários;
-   propriedades;
-   talhões;
-   safras;
-   animais;
-   estoque;
-   máquinas;
-   operações;
-   financeiro;
-   persistência;
-   validações;
-   auditoria;
-   integrações;
-   controle de acesso.

## Dados e inteligência

Python será utilizado principalmente para tarefas em que sua ergonomia e
ecossistema de dados sejam vantajosos.

Tecnologias possíveis:

-   Python;
-   FastAPI;
-   Pandas;
-   Polars;
-   NumPy;
-   scikit-learn.

Responsabilidades possíveis:

-   manipulação de grandes conjuntos de dados;
-   análises estatísticas;
-   agregações;
-   geração de indicadores;
-   processamento de dados;
-   modelos preditivos;
-   detecção de anomalias;
-   previsão de produtividade;
-   análise de custos;
-   manutenção preditiva;
-   experimentos de machine learning.

Python NÃO deve substituir regras centrais de negócio que pertencem ao
backend Java apenas para aumentar sua participação no projeto.

Da mesma forma, Java não deve reproduzir desnecessariamente tarefas
analíticas que sejam claramente mais adequadas ao ecossistema Python.

Cada linguagem deve existir onde fizer sentido.

------------------------------------------------------------------------

# 5. Arquitetura inicial

Evitar microserviços prematuros.

A arquitetura inicial preferida é:

``` text
project/
├── backend-java/
│   └── Spring Boot
├── analytics-python/
│   └── FastAPI / processamento de dados
├── frontend/
├── docs/
├── docker/
└── AGENTS.md
```

O backend Java deve começar como um monólito modular bem organizado.

O serviço Python deve permanecer separado quando houver uma necessidade
real de analytics/processamento.

A comunicação entre Java e Python poderá inicialmente ocorrer por
HTTP/REST e JSON.

Exemplo:

``` text
Frontend
   |
   v
Spring Boot
   |
   +---- PostgreSQL
   |
   +---- REST/JSON ----> Python/FastAPI
                           |
                           +-- Pandas / Polars
                           +-- scikit-learn
                           +-- analytics
```

Novos serviços só devem ser separados quando houver justificativa
arquitetural concreta.

------------------------------------------------------------------------

# 6. Machine Learning

Machine learning é parte planejada do projeto, mas NÃO deve ser
adicionado apenas para que o sistema possa dizer que possui IA.

Antes de criar um modelo, devem existir:

-   uma pergunta concreta;
-   dados adequados;
-   uma variável-alvo ou objetivo definido quando aplicável;
-   uma métrica de avaliação;
-   uma estratégia de validação;
-   uma justificativa para usar ML em vez de uma regra simples.

Possíveis aplicações futuras:

-   previsão de produtividade por hectare;
-   previsão de produção de uma safra;
-   estimativa de custos;
-   identificação de talhões anormais;
-   detecção de consumo incomum de combustível;
-   previsão de necessidade de manutenção;
-   classificação de riscos;
-   análise de padrões históricos.

Uma fonte de dados futura prevista é uma API externa de previsão do
tempo, integrada pelo serviço Python (`analytics-python`). Dados
meteorológicos (histórico e previsão) poderão alimentar features de
modelos scikit-learn, por exemplo para prever produtividade ou sugerir
janelas ideais para atividades de campo. Essa integração só deve ser
implementada quando houver um modelo ou análise concreta que a
justifique, seguindo as mesmas regras desta seção (pergunta concreta,
dados adequados, métrica de avaliação definida).

Começar com regras, estatística e análise exploratória quando forem
suficientes.

Ao utilizar scikit-learn, registrar pelo menos:

-   features utilizadas;
-   origem dos dados;
-   tratamento dos dados;
-   algoritmo;
-   hiperparâmetros relevantes;
-   divisão treino/validação/teste;
-   métricas;
-   versão do modelo;
-   data do treinamento.

Nunca apresentar a saída de um modelo como certeza absoluta.

------------------------------------------------------------------------

# 7. Fonte de verdade e limites entre Java e Python

O backend Java é a fonte de verdade para dados operacionais e regras de
negócio.

O serviço Python deve, por padrão:

1.  receber os dados necessários;
2.  processá-los;
3.  retornar resultados analíticos.

Exemplo:

``` text
Spring Boot
    |
POST /analytics/productivity/predict
    |
    v
FastAPI
    |
    v
modelo
    |
    v
{
  "predictedKgPerHectare": 3940.2,
  "modelVersion": "1.0.0"
}
```

Uma previsão não deve alterar silenciosamente dados operacionais.

Resultados de ML devem ser tratados como previsões, recomendações ou
indicadores, não como fatos.

------------------------------------------------------------------------

# 8. Primeiras etapas sugeridas

A primeira versão deve ser deliberadamente pequena.

Exemplo:

## V0.1 --- Fundação

-   usuário; ✅ concluído (entidade, migration, repository, service,
    DTOs, controller)
-   autenticação; ✅ concluído (JwtService, UsuarioDetailsService,
    AuthController com login, JwtAuthFilter, testado ponta a ponta)
-   propriedade rural; 🔶 em andamento (entidade e migration prontas;
    faltam repository, service e controller)
-   talhão;
-   cultura;
-   safra;
-   plantio.

Também já modelada, junto de propriedade rural, a entidade
`UsuarioPropriedadeAcesso` (vínculo N:N imutável entre usuário e
propriedade, com papel `PROPRIETARIO`/`GESTOR`/`OPERADOR`), que é a base
da autorização por recurso — ver ADR em
`docs/architecture/0001-modelagem-v0.1-fundacao.md`.

## V0.2 --- Insumos e estoque

-   produtos;
-   depósitos;
-   entradas;
-   saídas;
-   movimentações;
-   insumos utilizados por atividade/safra.

## V0.3 --- Operações agrícolas

-   atividades de campo;
-   aplicação de insumos;
-   plantio;
-   colheita;
-   custos associados.

## V0.4 --- Máquinas

-   máquinas;
-   implementos;
-   horímetro;
-   abastecimentos;
-   manutenção;
-   custos.

## V0.5 --- Pecuária

Somente se estiver no escopo escolhido naquele momento:

-   rebanhos;
-   animais;
-   movimentações;
-   produção;
-   alimentação;
-   histórico.

## V0.6 --- Financeiro

-   receitas;
-   despesas;
-   categorias;
-   contas;
-   compras;
-   vendas;
-   custos por propriedade/talhão/safra.

## V0.7 --- Analytics Python

-   consultas analíticas;
-   agregações;
-   indicadores;
-   análise histórica;
-   endpoints FastAPI.

## V0.8+ --- Inteligência

Somente após dados e necessidades concretas:

-   modelos scikit-learn;
-   previsões;
-   detecção de anomalias;
-   avaliação e versionamento de modelos.

As versões acima são uma orientação, não um contrato imutável.

------------------------------------------------------------------------

# 9. Regras obrigatórias para agentes de IA

## A IA DEVE

-   Ler este arquivo antes de trabalhar no projeto.
-   Respeitar a arquitetura e as decisões já existentes.
-   Analisar o código atual antes de propor mudanças estruturais.
-   Explicar resumidamente o que pretende alterar antes de mudanças
    grandes.
-   Fazer mudanças pequenas, coesas e revisáveis.
-   Manter responsabilidades bem separadas.
-   Preferir código legível a código excessivamente engenhoso.
-   Respeitar convenções já existentes no projeto.
-   Usar nomes claros.
-   Validar entradas.
-   Tratar erros adequadamente.
-   Preservar compatibilidade sempre que razoável.
-   Criar ou atualizar testes quando alterar comportamento.
-   Compilar/executar testes relevantes depois das mudanças.
-   Informar claramente testes que não puder executar.
-   Atualizar documentação quando alterar arquitetura, contratos ou
    comportamento importante.
-   Explicar decisões arquiteturais relevantes.
-   Perguntar ou sinalizar ambiguidade quando uma decisão puder alterar
    significativamente o produto.
-   Considerar segurança e privacidade desde o início.
-   Usar migrations para mudanças persistentes no schema do banco.
-   Manter segredos fora do repositório.
-   Respeitar contratos entre Java e Python.
-   Manter precisão monetária apropriada em módulos financeiros.
-   Considerar idempotência quando operações puderem ser repetidas.
-   Registrar eventos relevantes sem expor informações sensíveis.
-   Manter o projeto executável durante sua evolução sempre que
    possível.

## A IA NÃO DEVE

-   Reescrever grandes partes do projeto sem necessidade.
-   Criar funcionalidades que não foram solicitadas só porque parecem
    interessantes.
-   Implementar todo o roadmap antecipadamente.
-   Transformar o projeto em microserviços sem justificativa.
-   Criar abstrações para problemas que ainda não existem.
-   Duplicar regras de negócio entre Java e Python.
-   Colocar lógica de negócio importante em controllers.
-   Fazer acesso indiscriminado ao banco entre serviços.
-   Alterar schema manualmente quando o projeto utilizar migrations.
-   Desabilitar validações ou mecanismos de segurança para fazer algo
    funcionar.
-   Colocar senhas, tokens, chaves ou credenciais no código.
-   Logar senhas, tokens ou outros segredos.
-   Inventar requisitos.
-   Inventar dados ou métricas de ML e apresentá-los como reais.
-   Adicionar machine learning sem uma finalidade mensurável.
-   Usar previsões de ML como decisões automáticas irreversíveis sem
    regra explícita.
-   Misturar refatorações gigantes com implementação de uma pequena
    feature.
-   Alterar APIs públicas silenciosamente.
-   Remover testes para fazer o build passar.
-   Ignorar warnings ou erros importantes sem explicar.
-   Fazer `git push` sem solicitação explícita.
-   Fazer operações Git destrutivas sem solicitação explícita.
-   Apagar arquivos ou dados importantes sem autorização explícita.
-   Modificar arquivos fora do escopo da tarefa sem necessidade.

------------------------------------------------------------------------

# 10. Comportamento esperado da IA ao receber uma tarefa

Antes de implementar uma funcionalidade, a IA deve identificar:

1.  qual é o requisito;
2.  qual módulo é responsável;
3.  se pertence ao Java, Python ou frontend;
4.  quais entidades/contratos são afetados;
5.  quais riscos existem;
6.  quais testes devem validar a mudança.

Para mudanças pequenas, não é necessário produzir um documento extenso
de planejamento.

Para mudanças grandes, apresentar primeiro um plano curto e objetivo.

Depois da implementação, informar:

-   o que foi alterado;
-   arquivos principais modificados;
-   decisões importantes;
-   testes executados;
-   pendências ou riscos conhecidos.

------------------------------------------------------------------------

# 11. Regras para Java / Spring Boot

Manter camadas e responsabilidades claras.

Estrutura possível:

``` text
controller
service
repository
domain/model
dto
mapper
config
security
exception
```

A estrutura real do projeto tem prioridade sobre este exemplo.

Regras:

-   Controllers devem ser finos.
-   Regras de negócio devem ficar em serviços/domínio apropriado.
-   Entidades de persistência não devem ser expostas diretamente pela
    API sem motivo.
-   Usar DTOs quando necessário.
-   Usar Bean Validation.
-   Usar transações explicitamente onde a consistência exigir.
-   Padronizar tratamento de erros.
-   Não usar `double`/`float` para valores monetários.
-   Preferir `BigDecimal` para dinheiro.
-   Evitar N+1 queries.
-   Paginar consultas potencialmente grandes.
-   Criar índices de banco quando houver justificativa.
-   Autorização deve verificar acesso ao recurso, não apenas o papel
    global do usuário.

------------------------------------------------------------------------

# 12. Regras para Python

Python deve permanecer organizado mesmo quando usado para scripts e
análise.

Evitar transformar `analytics-python` em uma coleção de arquivos soltos.

Separar quando apropriado:

``` text
api/
services/
analytics/
models/
schemas/
data/
tests/
```

Regras:

-   Usar type hints em código de aplicação.
-   Validar payloads de API.
-   Separar preparação de dados, treinamento e inferência.
-   Evitar notebooks como única implementação de lógica importante.
-   Notebooks podem ser usados para exploração.
-   Código que vai para produção deve ser reproduzível.
-   Fixar/gerenciar dependências adequadamente.
-   Não carregar modelos arbitrários de fontes não confiáveis.
-   Tratar dados ausentes e inválidos explicitamente.

------------------------------------------------------------------------

# 13. Dados

O sistema poderá eventualmente manipular grande volume de informações.

Desde cedo:

-   evitar carregar tabelas inteiras em memória sem necessidade;
-   utilizar paginação;
-   projetar consultas conscientemente;
-   registrar datas/horários de forma consistente;
-   preservar histórico quando ele tiver valor operacional;
-   considerar rastreabilidade;
-   evitar deleções irreversíveis de dados históricos importantes;
-   documentar unidades de medida.

Unidades precisam ser explícitas.

Evitar campos ambíguos como:

``` text
area = 50
```

Preferir conceitos claros como:

``` text
areaHectares = 50
```

ou uma estratégia consistente de unidades no domínio.

------------------------------------------------------------------------

# 14. Segurança

Mesmo sendo inicialmente um projeto de desenvolvimento/portfólio, tratar
segurança como requisito real.

Considerar:

-   hashing seguro de senhas;
-   autenticação;
-   autorização;
-   menor privilégio;
-   validação;
-   proteção contra acesso indevido a recursos;
-   rate limiting onde necessário;
-   proteção de endpoints administrativos;
-   gerenciamento seguro de sessões/tokens;
-   CORS configurado conscientemente;
-   CSRF quando aplicável;
-   auditoria;
-   logs seguros;
-   backups;
-   recuperação de dados;
-   dependências atualizadas.

Nunca confiar apenas no frontend para autorização.

------------------------------------------------------------------------

# 15. Testes

A estratégia deve crescer junto com o sistema.

Priorizar:

-   testes unitários para regras de negócio;
-   testes de integração para persistência;
-   testes de API para fluxos importantes;
-   testes de segurança para autorização;
-   testes dos contratos Java ↔ Python;
-   testes de transformação de dados;
-   testes de modelos e pipelines analíticos quando existirem.

Bugs importantes corrigidos devem receber teste de regressão quando
possível.

------------------------------------------------------------------------

# 16. Documentação

Manter documentação útil, não documentação decorativa.

Documentar principalmente:

-   como executar;
-   configuração;
-   arquitetura;
-   módulos;
-   decisões arquiteturais importantes;
-   contratos entre serviços;
-   variáveis de ambiente;
-   migrations;
-   processos de treinamento de modelos;
-   requisitos importantes.

Decisões grandes podem ser registradas como ADRs em
`docs/architecture/`.

------------------------------------------------------------------------

# 17. Git

A IA pode preparar alterações localmente, mas:

-   não fazer `git push` sem autorização explícita;
-   não usar `git reset --hard`, force push ou equivalentes destrutivos
    sem autorização explícita;
-   não apagar branches sem autorização;
-   não incluir segredos em commits;
-   manter commits pequenos e semanticamente coerentes quando a IA for
    solicitada a criá-los.

Antes de mudanças amplas, verificar o estado atual do repositório.

------------------------------------------------------------------------

# 18. Princípio de evolução

Não sacrificar a qualidade atual em nome de uma escala futura
hipotética.

Quando surgir necessidade real de escala, medir primeiro.

Exemplos:

-   não criar Kafka porque "um dia pode precisar";
-   não criar Kubernetes porque "é projeto grande";
-   não separar cada módulo em um microserviço;
-   não adicionar Redis sem um problema concreto de cache/coordenação;
-   não adicionar ML sem dados e métrica.

Tecnologia deve resolver problemas, não decorar o README.

------------------------------------------------------------------------

# 19. Identidade técnica do projeto

Este projeto deve demonstrar principalmente:

-   engenharia backend com Java/Spring Boot;
-   arquitetura de software;
-   modelagem de domínio;
-   bancos relacionais;
-   segurança;
-   APIs;
-   testes;
-   integração entre linguagens;
-   engenharia e manipulação de dados com Python;
-   analytics;
-   machine learning aplicado quando houver justificativa.

Java e Python são tecnologias de primeira classe neste projeto, mas
possuem responsabilidades diferentes.

------------------------------------------------------------------------

# 20. Regra final

Ao encontrar duas soluções possíveis, a IA deve preferir aquela que:

1.  atende ao requisito atual;
2.  é mais fácil de entender;
3.  é mais fácil de testar;
4.  preserva a possibilidade de evolução;
5.  adiciona menos complexidade desnecessária.

O tamanho ambicioso do projeto NÃO é autorização para overengineering.

Construir uma plataforma enorme significa manter uma base saudável
durante centenas de pequenas evoluções.
