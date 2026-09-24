# Payment Service (Group 2)

Payment microservice for the Webshop project. Handles checkout sessions and payments via the Stripe API, secured with JWT tokens from `auth-server`.

---

## Features

- **Stripe API Integration:** Creates payment intents / checkout sessions and listens to Stripe webhooks.
- **Security:** Verifies JWT Bearer tokens issued by `auth-server` via JWKS (`/auth/jwks`).
- **OpenAPI / Swagger:** Interactive API documentation.

---

## Prerequisites

- **Java 17**
- **Stripe Account** (Test API keys)
- Running instances of `auth-server` (and optionally `local-db` / PostgreSQL)

---

## Running Locally

### Using Maven Wrapper

```bash
./mvnw clean spring-boot:run
```

### Or using installed Maven

```bash
mvn clean spring-boot:run
```

---

## API Documentation

Once the service is running, Swagger UI is accessible at:
- **Swagger UI:** [http://localhost:5003/swagger-ui/index.html](http://localhost:5003/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:5003/v3/api-docs](http://localhost:5003/v3/api-docs)
