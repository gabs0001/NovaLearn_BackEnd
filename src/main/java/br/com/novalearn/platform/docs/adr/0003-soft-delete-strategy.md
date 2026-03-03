# ADR 0003 - Soft Delete como Estratégia Padrão

## Status
Accepted

## Contexto

A aplicação lida com dados acadêmicos e históricos.
Excluir permanentemente poderia causar:

- Perda de rastreabilidade
- Problemas de auditoria
- Inconsistências históricas

## Decisão

Implementar soft delete como padrão em entidades principais.

Cada entidade possui:

- Campo active
- Campo deleted
- Métodos de ativar, desativar, deletar e restaurar

As regras impedem operação em entidades deletadas.

## Alternativas consideradas

- Hard delete tradicional
- Arquivamento externo

## Consequências

Positivas:
- Preservação de histórico
- Maior controle de integridade
- Auditoria consistente

Negativas:
- Consultas precisam filtrar registros ativos
- Maior complexidade de regras