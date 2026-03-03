# ADR 0001 - Arquitetura Orientada ao Domínio

## Status
Accepted

## Contexto

O projeto NovaLearn foi criado com o objetivo de demonstrar maturidade arquitetural, organização clara de responsabilidades e proteção das regras de negócio.

Era necessário definir um estilo arquitetural que:

- Protegesse o domínio da aplicação
- Permitisse alta testabilidade
- Evitasse forte acoplamento com frameworks
- Mantivesse escalabilidade estrutural

## Decisão

Adotar uma abordagem híbrida combinando:

- Domain-Driven Design (DDD) pragmático
- Princípios de Clean Architecture

A estrutura foi organizada em:

- api
- core
- domain
- infra

O domínio concentra regras de negócio e eventos.
A infraestrutura implementa detalhes técnicos.
O core centraliza segurança e tratamento de exceções.
A API expõe contratos HTTP.

## Alternativas consideradas

- Arquitetura em camadas tradicional
- Arquitetura puramente baseada em Spring (controller → service → repository)
- Hexagonal Architecture completa

## Consequências

Positivas:
- Alta organização estrutural
- Testabilidade facilitada
- Separação clara de responsabilidades
- Evolução futura simplificada

Negativas:
- Maior complexidade inicial
- Mais arquivos e abstrações