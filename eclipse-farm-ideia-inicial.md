# Eclipse Farm — ideia inicial

Sistema de gestão agrícola, mesma dupla de tecnologias do Eclipse Bank: Spring Boot (Java) pro backend principal e Python/FastAPI pra análise de dados.

## Ideia geral

- Spring Boot: cadastro e regras de negócio — fazendas, talhões, safras, atividades (plantio, colheita, aplicação de defensivo), usuários/autenticação.
- FastAPI: análises em cima dos dados registrados — produtividade por talhão, custo por safra, previsão simples, cruzamento com clima.
- Comunicação entre os dois: a definir (endpoint FastAPI chamado pelo Java, ou script/API separada como fizemos no Eclipse Bank).

## Roadmap sugerido (fases, como no Eclipse Bank)

1. **Núcleo**: usuário, fazenda, talhão (CRUD básico + autenticação)
2. **Safras e atividades**: cadastro de safra por talhão, registro de atividades (plantio, colheita, insumos aplicados)
3. **Custos**: registro de despesas por safra/talhão, cálculo de custo por hectare
4. **Analytics em Python**: produtividade esperada x realizada, custo-benefício por safra, gráficos
5. **Clima**: integração com API pública de clima, sugestão de janela ideal pra atividades
6. **Rastreabilidade**: histórico completo do talhão até a venda do produto

## Nomes considerados

Eclipse Farm, AgroVerde, CampoVivo, FazendaLog, Raiz, Talhão.

## Lições técnicas reaproveitáveis do Eclipse Bank

- BigDecimal pra qualquer valor monetário (custos, receitas de venda)
- Nunca confiar em ID vindo do cliente pra autorização — sempre validar no servidor
- H2 (ou outro banco) em modo `AUTO_SERVER=TRUE` se for integrar Python direto ao banco sem parar o backend
- Auditoria imutável pra ações críticas, se fizer sentido no domínio (ex: histórico de aplicação de defensivo)
