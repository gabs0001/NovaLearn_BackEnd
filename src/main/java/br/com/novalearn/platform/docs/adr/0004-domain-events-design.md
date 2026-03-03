# ADR 0004 - Uso de Eventos de Domínio Internos

## Status
Accepted

## Contexto

O domínio da aplicação contém ações que geram efeitos colaterais importantes, como:

- Conclusão de curso
- Emissão de certificado
- Cancelamento de matrícula
- Registro de usuário

Era necessário desacoplar a execução dessas ações das regras principais de negócio, evitando lógica procedural concentrada em services.

Ao mesmo tempo, o projeto ainda não exige mensageria distribuída.

## Decisão

Implementar eventos de domínio internos (in-memory), disparados diretamente pelas entidades.

As entidades:

- Registram eventos internamente
- Permitem coleta (pull) dos eventos
- Limpam eventos após processamento

Handlers específicos tratam os eventos na camada apropriada.

A arquitetura foi preparada para futura evolução para mensageria externa (Kafka ou RabbitMQ), caso necessário.

## Alternativas consideradas

- Implementar lógica diretamente nos services
- Introduzir mensageria desde o início
- Não utilizar eventos

## Consequências

Positivas:
- Melhor separação de responsabilidades
- Modelo de domínio mais expressivo
- Preparação para evolução futura
- Redução de acoplamento

Negativas:
- Complexidade adicional na modelagem
- Necessidade de disciplina para manutenção do padrão