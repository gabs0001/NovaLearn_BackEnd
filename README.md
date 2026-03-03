# 🚀 NovaLearn

> Plataforma de cursos online construída com foco em arquitetura limpa, modelagem rica de domínio e práticas profissionais de backend.

**NovaLearn** é uma aplicação backend inspirada em plataformas como Udemy e Alura, desenvolvida como projeto técnico de alto nível para portfólio, aplicando conceitos modernos de desenvolvimento backend, arquitetura orientada ao domínio e boas práticas do ecossistema Java.

---

# 🧠 Visão Geral

NovaLearn é uma plataforma de cursos online que oferece:

* Autenticação segura com JWT + Refresh Token rotacionado
* Gestão acadêmica completa (categorias, cursos, módulos, aulas)
* Sistema de quizzes com cálculo de nota
* Controle de progresso do aluno
* Emissão de certificados
* Sistema de avaliações com moderação
* Auditoria e Soft Delete padronizados
* Eventos de domínio preparados para evolução futura

O projeto foi construído com foco em:

* Arquitetura organizada e escalável
* Separação clara de responsabilidades
* Testabilidade e confiabilidade
* Segurança robusta
* Evolução contínua

---

# 🏗 Arquitetura

O projeto adota uma abordagem **DDD pragmática combinada com princípios de Clean Architecture**, mantendo o domínio protegido e o código desacoplado.

## 📦 Estrutura de Pacotes

```
br.com.novalearn.platform

├── api
│   ├── controllers
│   ├── dtos
│   ├── mappers
│   └── events (handlers)
│
├── core
│   ├── config
│   ├── security
│   ├── exception
│   └── util
│
├── domain
│   ├── entities
│   ├── enums
│   ├── repositories
│   ├── services
│   ├── valueobjects
│   ├── events
│   ├── policies
│   └── converters
│
└── infra
    ├── gateway
    ├── security
    ├── persistence
    ├── jpa
    └── time
```

## 🎯 Responsabilidades

| Camada     | Responsabilidade                                          |
| ---------- | --------------------------------------------------------- |
| **api**    | Interface HTTP, DTOs, mapeamentos e handlers              |
| **core**   | Segurança, configurações e tratamento de exceções         |
| **domain** | Regras de negócio, entidades e eventos                    |
| **infra**  | Implementações técnicas (JPA, segurança, tempo, gateways) |

O domínio concentra as regras.
Spring e JPA reconhecem as entidades, mas **não controlam seu comportamento**.

---

# 🔐 Segurança

Autenticação baseada em:

* JWT (Access Token com expiração curta)
* Refresh Token persistido e criptografado
* Rotação automática de refresh token
* Controle de múltiplas sessões
* Invalidação no logout

Fluxo resumido:

1. Login → gera Access Token + Refresh Token
2. Access Token acompanha cada requisição
3. Refresh Token é armazenado criptografado
4. Cada refresh gera novo token (rotação)

---

# 🧭 Principais Endpoints

A API segue padrão REST com uso semântico correto de verbos HTTP.

## 🔑 Autenticação

```
POST   /api/auth/login
POST   /api/auth/register
POST   /api/auth/refresh
POST   /api/auth/logout
GET    /api/auth/me
PATCH  /api/auth/change-password
```

---

## 📚 Cursos

```
GET    /api/courses
GET    /api/courses/{id}
GET    /api/courses/slug/{slug}
GET    /api/courses/featured
POST   /api/courses
PATCH  /api/courses/{id}
DELETE /api/courses/{id}
```

---

## 🎓 Matrícula e Progresso

```
POST   /api/me/enrollments
PATCH  /api/me/enrollments/{courseId}/progress
POST   /api/me/enrollments/{courseId}/certificate
DELETE /api/me/enrollments/{courseId}
```

---

## 📝 Quizzes

```
GET    /api/quizzes/{quizId}
POST   /api/me/quiz-attempts
PATCH  /api/me/quiz-attempts/{id}/finish
POST   /api/me/quiz-answers
```

---

## ⭐ Avaliações

```
POST   /api/me/reviews
PATCH  /api/reviews/{id}/approve
PATCH  /api/reviews/{id}/reject
GET    /api/reviews/course/{courseId}
```

---

# 🎓 Regras Acadêmicas

* Curso deve estar **ativo e publicado**
* Conclusão validada apenas com **100% de progresso**
* Módulos obrigatórios
* Quiz influencia aprovação
* Certificado gerado após validação completa

---

# 📜 Eventos de Domínio

Eventos internos implementados:

* User Registered
* Course Completed
* Certificate Issued
* Enrollment Cancelled

Hoje são eventos internos (in-memory), mas a arquitetura já está preparada para futura mensageria (Kafka/RabbitMQ).

---

# 🗄 Banco de Dados

* **Oracle** como banco principal
* **H2** para testes
* Flyway para versionamento
* Sequences explícitas
* Sem uso de UUID

---

# 🧪 Estratégia de Testes

Testes organizados estrategicamente em:

* Entidades (regras puras de domínio)
* Services (regras + transações)
* Repositórios
* Controllers (contrato HTTP)
* Mappers (MapStruct)
* E2E cobrindo fluxos principais:

  * Estrutura acadêmica
  * Jornada do aluno
  * Ciclo de review
  * Segurança
  * Consistência transacional + eventos

O foco é validar:

* Integridade transacional
* Regras de negócio críticas
* Cenários de erro
* Fluxos completos

---

# 🧰 Stack Tecnológica

* Java 21
* Spring Boot 3.4.3
* Spring Security
* Spring Data JPA
* Maven
* MapStruct
* Lombok
* Oracle
* H2
* Flyway

---

# 🚀 Evoluções Planejadas

* Mensageria
* Dockerização
* Swagger/OpenAPI
* Observabilidade
* Expansão de paginação e filtros

---

# 🎯 Objetivo do Projeto

NovaLearn foi desenvolvido como um **projeto técnico de destaque para portfólio**, com foco em:

* Arquitetura organizada
* Código escalável
* Modelagem rica de domínio
* Segurança moderna
* Testes robustos
* Práticas profissionais de mercado

O objetivo não é apenas funcionar —
é demonstrar maturidade técnica e organização arquitetural.

---

flowchart TD

Client[Client / Frontend]
Client -->|HTTP Requests| API

subgraph Application
    API[API Layer\nControllers + DTOs + Mappers]
    CORE[Core\nSecurity + Config + Exception]
    DOMAIN[Domain\nEntities + Services + Events + Policies]
    INFRA[Infrastructure\nJPA + Gateways + Security Providers]
end

API --> DOMAIN
API --> CORE
DOMAIN --> INFRA
INFRA --> DB[(Oracle Database)]
INFRA --> H2[(H2 - Tests)]

DOMAIN -->|Domain Events| DOMAIN
CORE -->|JWT Filter| API

---

sequenceDiagram

participant User
participant API
participant DB

User->>API: POST /auth/login
API->>DB: Validate credentials
API->>User: Access Token + Refresh Token

User->>API: Request with Access Token
API->>User: Protected Resource

User->>API: POST /auth/refresh
API->>DB: Validate + Rotate Refresh Token
API->>User: New Access + New Refresh Token
