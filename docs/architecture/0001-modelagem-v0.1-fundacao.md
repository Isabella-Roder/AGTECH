# ADR 0001 — Modelagem de domínio da V0.1 (Fundação)

## Status
Proposto.

## Contexto
A V0.1 definida no AGENTS.md cobre: usuário, autenticação, propriedade rural,
talhão, cultura, safra e plantio. Este documento modela essas entidades e
seus relacionamentos antes da implementação, para que cada entidade seja
depois implementada em fatias pequenas e revisáveis.

## Entidades

### Usuario
Representa quem acessa o sistema.

- `id`
- `nome`
- `email` (único, usado para login)
- `senhaHash`
- `ativo`
- `criadoEm`

Papel de acesso **não** fica direto no usuário. Ver `UsuarioPropriedadeAcesso`
abaixo — autorização deve verificar acesso ao recurso (propriedade), não
apenas um papel global, conforme a regra do AGENTS.md.

### PropriedadeRural
Uma fazenda/unidade produtiva administrada no sistema.

- `id`
- `nome`
- `municipio`, `estado`
- `areaTotalHectares`
- `criadoEm`

### UsuarioPropriedadeAcesso
Tabela de associação que define quem pode acessar/administrar cada
propriedade e com que papel. Existe desde já para não depender de papel
global e para já nascer correto do ponto de vista de autorização.

- `id`
- `usuarioId` (FK)
- `propriedadeId` (FK)
- `papel` (`PROPRIETARIO`, `GESTOR`, `OPERADOR`)

### Talhao
Subdivisão de uma propriedade onde ocorrem plantios.

- `id`
- `propriedadeId` (FK → PropriedadeRural)
- `nome` (identificador do talhão, ex: "Talhão 3")
- `areaHectares`

### Cultura
Catálogo simples de culturas agrícolas (soja, milho, café, etc). Entidade
compartilhada, não pertence a uma propriedade específica.

- `id`
- `nome` (único)

### Safra
Um ciclo produtivo de uma cultura em um talhão.

- `id`
- `talhaoId` (FK → Talhao)
- `culturaId` (FK → Cultura)
- `nome` (ex: "2025/2026")
- `dataInicio`
- `dataFimPrevista`
- `dataFimReal` (nulo até encerrar)
- `status` (`PLANEJADA`, `EM_ANDAMENTO`, `FINALIZADA`, `CANCELADA`)

### Plantio
Evento de plantio dentro de uma safra. Modelado como 1:N em relação à
safra para permitir replantio, em vez de assumir sempre um único evento.

- `id`
- `safraId` (FK → Safra)
- `dataPlantio`
- `areaPlantadaHectares`
- `observacoes` (opcional)

## Relacionamentos

```text
Usuario N---N PropriedadeRural   (via UsuarioPropriedadeAcesso, com papel)
PropriedadeRural 1---N Talhao
Talhao 1---N Safra
Cultura 1---N Safra
Safra 1---N Plantio
```

## Decisões e justificativas

- **Autorização por recurso desde o início**: em vez de um papel global no
  `Usuario`, o acesso é resolvido por `UsuarioPropriedadeAcesso`. Isso evita
  ter que retrofitar autorização por propriedade mais tarde, e é uma regra
  explícita do AGENTS.md.
- **Cultura como catálogo global**: evita duplicar "Soja" por propriedade;
  se no futuro surgir necessidade de parametrizar cultura por propriedade
  (ex: ciclo esperado diferente por região), isso vira um atributo da
  `Safra`, não da `Cultura`.
- **Plantio 1:N em relação à Safra**: cobre replantio sem forçar
  complexidade extra agora — o campo `observacoes` é suficiente para casos
  raros na V0.1.
- **Sem geolocalização/geometria de talhão nesta fase**: não é requisito
  concreto ainda; adicionar apenas quando houver necessidade real (ex:
  integração com mapas/sensores).
- **Áreas em hectares explícitas no nome do campo** (`areaHectares`,
  `areaTotalHectares`, `areaPlantadaHectares`), conforme a regra de
  unidades explícitas do AGENTS.md.

## Ordem de implementação sugerida (fatias pequenas)

1. `Usuario` + autenticação (login, hash de senha) — sem isso nada mais
   pode ser protegido corretamente.
2. `PropriedadeRural` + `UsuarioPropriedadeAcesso` — CRUD de propriedade já
   nascendo com controle de acesso por recurso.
3. `Talhao` (depende de `PropriedadeRural`).
4. `Cultura` (catálogo simples, independente).
5. `Safra` (depende de `Talhao` e `Cultura`).
6. `Plantio` (depende de `Safra`).

Cada item acima deve ser entregue, testado e revisado antes do próximo,
conforme a filosofia de desenvolvimento do AGENTS.md.

## Consequências
- Toda consulta e regra de autorização em `PropriedadeRural` e entidades
  filhas (`Talhao`, `Safra`, `Plantio`) deve considerar
  `UsuarioPropriedadeAcesso`, não apenas autenticação.
- Migrations (Flyway/Liquibase — a definir) precisarão ser criadas na
  mesma ordem listada acima.
