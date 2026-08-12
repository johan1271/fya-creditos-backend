🇬🇧 English | 🇪🇸 [Español](#español)

# fya-creditos-backend

Backend for Fya Social Capital's credit registration/lookup technical test: a REST API to register credits and search them with pagination, protected with JWT auth and per-IP rate limiting, with an async email notification on every registration.

- **Live API:** https://fya-creditos-backend.onrender.com
- **Swagger UI:** https://fya-creditos-backend.onrender.com/swagger-ui.html
- **Frontend repo:** https://github.com/johan1271/fya-creditos-frontend
- **Live web app:** https://fya-creditos-frontend.netlify.app
- **Signed Android APK:** https://github.com/johan1271/fya-creditos-frontend/releases/tag/v1.2.0

## Stack

- Java 21, Spring Boot 4.1.0 (Web MVC, Data JPA, Validation, Security, Flyway, Mail)
- PostgreSQL, hosted on [Neon](https://neon.tech)
- JWT auth (`io.jsonwebtoken`), stateless
- [Bucket4j](https://github.com/bucket4j/bucket4j) for per-IP rate limiting
- springdoc-openapi (Swagger UI)
- Docker multi-stage build, deployed on [Render](https://render.com)

## Prerequisites

- JDK 21
- A PostgreSQL database (Neon's free tier works)
- An SMTP account for the async notification email (a Gmail app password, or a relay like Brevo)

## Running locally

1. Copy `.env.example` to `.env` (or export the variables directly in your shell) and fill in real values.
2. Export the variables, then:
   ```bash
   ./mvnw spring-boot:run
   ```
3. API at `http://localhost:8080`, Swagger UI at `http://localhost:8080/swagger-ui.html`.

Flyway runs all migrations automatically on startup — no manual schema setup needed beyond having an empty database.

### Environment variables

| Variable | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Postgres connection (Neon) |
| `MAIL_HOST` / `MAIL_PORT` | SMTP host/port. Default `smtp.gmail.com:587`; Render needs an alternate port (see deployment notes) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP auth credentials |
| `MAIL_SENDER` | Visible "From" address (may differ from `MAIL_USERNAME` with relay providers) |
| `MAIL_NOTIFICATION_RECIPIENT` | Where the registration notification email is sent |
| `JWT_SECRET` | Secret used to sign JWTs (falls back to a dev-only default if unset — don't rely on that in production) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins; defaults cover `ionic serve`, the Capacitor Android WebView, and the Netlify web deploy |
| `PORT` | Optional, defaults to `8080` (Render sets this itself) |

## Database

Flyway migrations live in `src/main/resources/db/migration`. Applied migrations are immutable — any schema change goes through a new `V{n}__description.sql` file, never an edit to an existing one.

| Migration | What it does |
|---|---|
| `V1` | Creates the `credits` table |
| `V2` | Seeds the 10 credits from the test's annex |
| `V3` | Switches `registered_at` to `TIMESTAMPTZ` |
| `V4` | Creates the `users` table |
| `V5` | Seeds 3 test users (credentials below) |

## API

`/api/credits/**` requires an `Authorization: Bearer <token>` header, obtained via login. `/api/auth/**` and the Swagger/OpenAPI paths are public.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/login` | `{ username, password }` → `{ token, username, fullName }` |
| `POST` | `/api/credits` | Registers a credit. Validates `customerName`/`idNumber` (non-blank), `creditAmount` (> 0), `interestRate` (0-100), `termMonths` (1-360), `salesAgent` (non-blank) |
| `GET` | `/api/credits?q=&page=&size=&sort=` | Paginated search across customer name, ID number, and sales agent |

Full request/response shapes and a try-it-out console: `/swagger-ui.html`.

### Test credentials

Same password (`password123`, bcrypt-hashed) for all three seeded users:

| Username | Full name |
|---|---|
| `carlos.ramirez` | Carlos Ramirez |
| `laura.jimenez` | Laura Jimenez |
| `andres.torres` | Andres Torres |

## Rate limiting

20 requests/minute per client IP (see `RateLimitFilter`), enforced *before* JWT auth so login attempts are covered too. Exceeding it returns `429` with the same `{timestamp, status, message, errors}` shape as other API errors.

## Async email notification

Every successful `POST /api/credits` publishes a domain event; a listener sends the notification email **after** the database transaction commits, on a separate thread — a slow or unreachable SMTP server never delays or fails the HTTP response (see `CreditRegisteredListener` / `MailService`).

## Deployment notes (Render)

- **Free tier cold start**: this service is deployed on Render's free plan, which spins the instance down after ~15 minutes of inactivity. The first request after idle time can take 30-60 seconds while it wakes up; subsequent requests are fast.
- **Outbound SMTP ports are blocked**: Render blocks outbound connections on the standard SMTP ports (25, 465, 587) on free/starter plans to prevent abuse. The async email notification uses Brevo's SMTP relay on the alternate port **2525**, which is not blocked. Locally/Docker this restriction doesn't apply, so any SMTP provider/port works.

---

## Español

# fya-creditos-backend

Backend para la prueba técnica de registro/consulta de créditos de Fya Social Capital: una API REST para registrar créditos y buscarlos con paginación, protegida con JWT y rate limiting por IP, con notificación de correo asíncrona en cada registro.

- **API en vivo:** https://fya-creditos-backend.onrender.com
- **Swagger UI:** https://fya-creditos-backend.onrender.com/swagger-ui.html
- **Repo del frontend:** https://github.com/johan1271/fya-creditos-frontend
- **App web en vivo:** https://fya-creditos-frontend.netlify.app
- **APK firmado de Android:** https://github.com/johan1271/fya-creditos-frontend/releases/tag/v1.2.0

## Stack

- Java 21, Spring Boot 4.1.0 (Web MVC, Data JPA, Validation, Security, Flyway, Mail)
- PostgreSQL, alojado en [Neon](https://neon.tech)
- Autenticación JWT (`io.jsonwebtoken`), sin estado (stateless)
- [Bucket4j](https://github.com/bucket4j/bucket4j) para rate limiting por IP
- springdoc-openapi (Swagger UI)
- Build Docker multi-stage, desplegado en [Render](https://render.com)

## Prerrequisitos

- JDK 21
- Una base de datos PostgreSQL (el plan free de Neon funciona bien)
- Una cuenta SMTP para el correo de notificación asíncrono (un App Password de Gmail, o un relay como Brevo)

## Correr en local

1. Copia `.env.example` a `.env` (o exporta las variables directo en tu shell) y completa con valores reales.
2. Exporta las variables y luego:
   ```bash
   ./mvnw spring-boot:run
   ```
3. API en `http://localhost:8080`, Swagger UI en `http://localhost:8080/swagger-ui.html`.

Flyway corre todas las migraciones automáticamente al iniciar — no hace falta preparar el esquema a mano, solo tener una base de datos vacía.

### Variables de entorno

| Variable | Propósito |
|---|---|
| `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Conexión a Postgres (Neon) |
| `MAIL_HOST` / `MAIL_PORT` | Host/puerto SMTP. Por defecto `smtp.gmail.com:587`; Render necesita un puerto alterno (ver notas de despliegue) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Credenciales de autenticación SMTP |
| `MAIL_SENDER` | Dirección "De" visible (puede diferir de `MAIL_USERNAME` con proveedores de relay) |
| `MAIL_NOTIFICATION_RECIPIENT` | A dónde se envía el correo de notificación de registro |
| `JWT_SECRET` | Secreto usado para firmar los JWT (usa un valor de desarrollo por defecto si no se define — no confiar en eso en producción) |
| `CORS_ALLOWED_ORIGINS` | Lista de orígenes permitidos separados por coma; el valor por defecto cubre `ionic serve`, el WebView de Capacitor Android, y el deploy web en Netlify |
| `PORT` | Opcional, por defecto `8080` (Render lo define solo) |

## Base de datos

Las migraciones de Flyway viven en `src/main/resources/db/migration`. Las migraciones ya aplicadas son inmutables — cualquier cambio de esquema va en un archivo `V{n}__descripcion.sql` nuevo, nunca editando uno existente.

| Migración | Qué hace |
|---|---|
| `V1` | Crea la tabla `credits` |
| `V2` | Siembra los 10 créditos del anexo de la prueba |
| `V3` | Cambia `registered_at` a `TIMESTAMPTZ` |
| `V4` | Crea la tabla `users` |
| `V5` | Siembra 3 usuarios de prueba (credenciales abajo) |

## API

`/api/credits/**` requiere el header `Authorization: Bearer <token>`, obtenido al loguearse. `/api/auth/**` y las rutas de Swagger/OpenAPI son públicas.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/login` | `{ username, password }` → `{ token, username, fullName }` |
| `POST` | `/api/credits` | Registra un crédito. Valida `customerName`/`idNumber` (no vacíos), `creditAmount` (> 0), `interestRate` (0-100), `termMonths` (1-360), `salesAgent` (no vacío) |
| `GET` | `/api/credits?q=&page=&size=&sort=` | Búsqueda paginada por nombre del cliente, número de identificación o asesor de ventas |

Formas completas de request/response y consola interactiva: `/swagger-ui.html`.

### Credenciales de prueba

Misma contraseña (`password123`, hasheada con bcrypt) para los tres usuarios sembrados:

| Usuario | Nombre completo |
|---|---|
| `carlos.ramirez` | Carlos Ramirez |
| `laura.jimenez` | Laura Jimenez |
| `andres.torres` | Andres Torres |

## Rate limiting

20 peticiones/minuto por IP del cliente (ver `RateLimitFilter`), aplicado *antes* de la autenticación JWT, así que también cubre los intentos de login. Al excederlo devuelve `429` con la misma forma `{timestamp, status, message, errors}` que los demás errores de la API.

## Notificación de correo asíncrona

Cada `POST /api/credits` exitoso publica un evento de dominio; un listener envía el correo de notificación **después** de que la transacción de base de datos hace commit, en un hilo separado — un servidor SMTP lento o inalcanzable nunca retrasa ni hace fallar la respuesta HTTP (ver `CreditRegisteredListener` / `MailService`).

## Notas de despliegue (Render)

- **Cold start del plan free**: este servicio está desplegado en el plan gratuito de Render, que apaga la instancia luego de ~15 minutos de inactividad. La primera petición después de estar inactivo puede tardar 30-60 segundos en despertar; las siguientes son rápidas.
- **Los puertos SMTP salientes están bloqueados**: Render bloquea las conexiones salientes por los puertos estándar de SMTP (25, 465, 587) en los planes free/starter para prevenir abuso. La notificación de correo asíncrona usa el relay SMTP de Brevo por el puerto alternativo **2525**, que no está bloqueado. En local/Docker esta restricción no aplica, así que cualquier proveedor/puerto SMTP funciona.
