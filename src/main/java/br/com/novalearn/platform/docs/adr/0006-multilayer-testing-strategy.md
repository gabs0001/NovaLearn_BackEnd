# ADR 0006 - Estratégia de Testes Multicamadas

## Status
Accepted

## Contexto

O projeto NovaLearn possui regras de negócio complexas, múltiplas transações e fluxos críticos como:

- Jornada completa do aluno
- Controle de segurança
- Emissão de certificados
- Ciclo de avaliações
- Consistência de eventos

Era necessário definir uma estratégia de testes que garantisse confiabilidade sem depender exclusivamente de testes E2E.

## Decisão

Adotar uma estratégia de testes dividida em múltiplos níveis:

- Testes de entidades (regras puras de domínio)
- Testes de services (regras + transações)
- Testes de repositórios
- Testes de controllers (contrato HTTP)
- Testes de mappers (MapStruct)
- Testes E2E cobrindo fluxos críticos

Os testes priorizam:

- Cenários de erro
- Validação de regras de negócio
- Integridade transacional
- Comportamento esperado em fluxos completos

## Alternativas consideradas

- Apenas testes de controller
- Apenas testes E2E
- Baixa cobertura focando apenas em cenários positivos

## Consequências

Positivas:
- Alta confiabilidade do sistema
- Detecção precoce de regressões
- Documentação viva do comportamento esperado
- Maior segurança para evolução futura

Negativas:
- Maior volume de código de teste
- Tempo maior de manutenção