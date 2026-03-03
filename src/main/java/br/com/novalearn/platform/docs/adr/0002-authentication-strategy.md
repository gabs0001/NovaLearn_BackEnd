# ADR 0002 - Estratégia de Autenticação com JWT e Refresh Token Rotacionado

## Status
Accepted

## Contexto

A aplicação exige autenticação segura, controle de múltiplas sessões e possibilidade de revogação de tokens.

Era necessário evitar:

- Sessões stateful
- Tokens long-lived inseguros
- Falta de controle sobre sessões ativas

## Decisão

Implementar autenticação baseada em:

- JWT como Access Token (expiração curta)
- Refresh Token persistido no banco
- Armazenamento criptografado do refresh token
- Rotação de refresh token a cada renovação
- Invalidação no logout

## Alternativas consideradas

- JWT sem refresh token
- Sessão tradicional baseada em servidor
- Refresh token sem rotação

## Consequências

Positivas:
- Maior segurança
- Controle de sessões
- Possibilidade de revogação
- Escalabilidade stateless

Negativas:
- Complexidade maior de implementação
- Persistência adicional no banco