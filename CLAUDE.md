# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot backend for a credit registration/lookup technical test (Fya Social Capital). Exposes a REST API to register credits and search them with pagination, and sends an async email notification on every registration.

## Commands

- Run locally: `./mvnw spring-boot:run` (requires env vars below to be exported first)
- Build jar: `./mvnw clean package`
- Run tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=ClassName`
- Docker build: `docker build -t fya-creditos-backend:local .`
- Docker run: `docker run -d -p 8080:8080 -e SPRING_DATASOURCE_URL=... [...other env vars] fya-creditos-backend:local`

### Required environment variables

Copy `.env.example` for the full list. Key ones:
- `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` — Postgres (Neon) connection.
- `MAIL_HOST` / `MAIL_PORT` — default to `smtp.gmail.com:587`; overridden in production because Render blocks outbound ports 25/465/587, so prod uses Brevo's relay on port 2525 instead.
- `MAIL_USERNAME` / `MAIL_PASSWORD` — SMTP auth credentials (login, not necessarily the visible sender).
- `MAIL_SENDER` — the visible "From" address, decoupled from `MAIL_USERNAME` because SMTP relay providers (e.g. Brevo) often use a different login than the verified sender address.
- `MAIL_NOTIFICATION_RECIPIENT` — where registration notifications are sent.
- `CORS_ALLOWED_ORIGINS` — optional, defaults to `http://localhost:8100,capacitor://localhost,http://localhost` (the Ionic/Capacitor frontend origins).

No Spring profiles exist (no `application-dev`/`application-prod`); the same jar/image runs everywhere, and only env var values differ between local, Docker, and Render.

## Architecture

Standard layered structure under `com.fya.creditos`: `controller` → `service` → `repository`/`mapper`, with `dto` at the API edge and `entity` for JPA. Two things are less obvious from a single file and worth knowing up front:

**Async email notification via domain events, not a job/worker.** `CreditService.create()` is `@Transactional` and publishes a `CreditRegisteredEvent` through `ApplicationEventPublisher`. `CreditRegisteredListener` consumes it with `@Async` + `@TransactionalEventListener(phase = AFTER_COMMIT)`. The `@Transactional` on the service method is load-bearing: without it, the event listener's `AFTER_COMMIT` phase never fires because there's no transaction to commit into (the save's own internal transaction would already be closed). The executor is configured in `AsyncConfig`. Listener failures are caught and logged, never propagated — email delivery failure never fails the HTTP request.

**Pagination/search decoupled from Spring Data's `Page<T>`.** `CreditController#search` returns the custom `PagedResponse<T>` (built via `PagedResponse.from(Page<T>)`), not `Page<T>` directly, to avoid leaking `PageImpl`'s serialization quirks to API clients. `CreditRepository.search` takes the query term as a plain `@Param` (never null — `CreditService.search` normalizes `null` to `""` before calling the repository) because a null bind parameter inside `LOWER()`/`CONCAT()` in the JPQL query fails against Postgres with `function lower(bytea) does not exist` (type inference failure on a null param). The `Pageable` controller parameter is annotated with `@ParameterObject` (springdoc) so Swagger renders `page`/`size`/`sort` as separate query params instead of one opaque object.

**CORS is wired through Spring Security, not `@CrossOrigin`.** `CorsConfig` builds the `CorsConfigurationSource` bean from `app.cors.allowed-origins`; `SecurityConfig` injects that bean and registers it via `http.cors(...)`. `SecurityConfig` currently permits all requests (`permitAll()`) — there is no auth yet; this is intentionally temporary.

**Timestamps use `Instant`/`TIMESTAMPTZ`, not naive `LocalDateTime`/`TIMESTAMP`** (see `V3` migration), to avoid ambiguity between local dev (UTC-5) and cloud deploy (UTC).

Flyway migrations in `src/main/resources/db/migration` run automatically on boot (`spring.jpa.hibernate.ddl-auto=validate` — schema changes only ever go through a new migration, never through Hibernate auto-DDL).

## Deployment

Deployed to Render from this repo's `Dockerfile` (multi-stage: JDK Alpine build stage, JRE Alpine runtime stage), with auto-deploy on push to `main`. Env vars are set independently in Render's dashboard (not via git). See `README.md` for Render-specific quirks (free-tier cold start, SMTP port blocking).
