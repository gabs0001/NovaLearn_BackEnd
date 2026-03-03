# ADR 0005 - Estratégia de Banco de Dados (Oracle em Produção e H2 em Testes)

## Status
Accepted

## Contexto

A aplicação foi projetada para simular um ambiente corporativo real, onde Oracle é amplamente utilizado.

Ao mesmo tempo, os testes automatizados exigem:

- Execução rápida
- Independência de infraestrutura externa
- Isolamento de ambiente

Era necessário equilibrar realismo de produção com eficiência em testes.

## Decisão

Adotar:

- Oracle como banco principal
- H2 em memória para execução de testes automatizados
- Flyway para versionamento e controle de migrations
- Sequences explícitas para geração de identificadores

Não foi adotado UUID, priorizando alinhamento com práticas comuns em ambientes corporativos Oracle.

## Alternativas consideradas

- Utilizar apenas Oracle em todos os ambientes
- Utilizar Testcontainers
- Utilizar apenas banco em memória

## Consequências

Positivas:
- Ambiente realista de produção
- Testes rápidos e isolados
- Versionamento consistente do banco
- Controle explícito de geração de IDs

Negativas:
- Possíveis pequenas diferenças de comportamento entre Oracle e H2
- Necessidade de atenção na compatibilidade SQL