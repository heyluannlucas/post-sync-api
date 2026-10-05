# Post Sync API

API REST em Java que sincroniza posts do [JSONPlaceholder](https://jsonplaceholder.typicode.com) para um PostgreSQL e os expõe via endpoints paginados.

## Stack

- Java 21, Spring Boot 3.5, Maven
- PostgreSQL 17 com migrations Flyway
- Spring Data JPA, Bean Validation, Actuator
- Swagger UI / OpenAPI (springdoc)
- Docker e Docker Compose
- JUnit 5, Mockito e Testcontainers

## Como rodar

### Tudo com Docker (recomendado)

```bash
docker compose up --build
```

A API sobe em `http://localhost:8080` depois que o PostgreSQL estiver saudável.

### Aplicação local com PostgreSQL em Docker

```bash
docker compose up -d db
mvn spring-boot:run
```

As variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD` e `EXTERNAL_API_URL` sobrescrevem os padrões de `application.yml`.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/sync` | Importa os posts da API pública (insere novos e atualiza existentes) |
| GET | `/posts?page=0&size=20&sort=id,asc` | Lista paginada de posts |
| GET | `/posts/{id}` | Consulta um post |
| GET | `/health` | Saúde da aplicação e do banco |

Documentação interativa: `http://localhost:8080/swagger-ui.html` (JSON em `/v3/api-docs`).

Exemplo:

```bash
curl -X POST http://localhost:8080/sync
curl "http://localhost:8080/posts?size=5"
curl http://localhost:8080/posts/1
```

## Tratamento de erros

Erros seguem o padrão RFC 7807 (`application/problem+json`):

| Situação | Status |
|----------|--------|
| Post inexistente | 404 |
| Parâmetro inválido (ex.: `/posts/abc`) | 400 |
| API externa indisponível ou com erro | 502 |
| Falha inesperada | 500 (sem expor detalhes internos) |

## Testes

```bash
mvn test      # unitários (service e controller)
mvn verify    # unitários + integração com PostgreSQL real via Testcontainers
```

Os testes de integração exigem Docker em execução.

## Arquitetura

```
dev.luann.postsync
├── post        entity, repository, service, controller e DTOs
├── external    cliente HTTP da API pública
└── shared      configuração e tratamento global de erros
```

Decisões principais:

- **Organização por feature**: o código de posts fica junto, o que facilita leitura e evolução.
- **Sync em lote**: uma consulta busca os posts já existentes e uma gravação em lote insere ou atualiza, evitando N+1. A coluna `version` permite ao Hibernate distinguir inserção de atualização com ids vindos da origem.
- **Timeouts explícitos** no cliente HTTP, para que uma API externa lenta não prenha a aplicação.
- **Schema versionado** com Flyway e `ddl-auto: validate`, mantendo o banco sob controle das migrations.
- **Imagem Docker multi-stage**, com usuário não root e healthcheck.
- **Virtual threads** habilitadas para boa concorrência em I/O.
