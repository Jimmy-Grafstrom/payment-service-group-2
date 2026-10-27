# Payment Service (`payment-service-group-2`)

Microservice responsible for managing checkout sessions, Stripe payment integration, and payment confirmation events within the e-commerce system.

---

## 1. Responsibilities & System Role
- **Checkout Session Management:** Initiates and tracks Stripe checkout sessions for customer orders.
- **Stripe Webhook Processing:** Listens for and securely verifies Stripe webhook events (`checkout.session.completed`, `checkout.session.expired`).
- **Order Verification & Coordination:** Validates order details with `order-service` before creating payment sessions.
- **Event Publishing:** Emits payment confirmation messages via RabbitMQ upon completed payments.
- **Security:** Validates incoming JWT tokens against `auth-service` for protected endpoints.

---

## 2. Tech Stack
- **Language & Framework:** Java 17, Spring Boot 4
- **Persistence:** Spring Data JPA, Hibernate
- **Payment Gateway:** Stripe Java SDK (`com.stripe:stripe-java`)
- **Messaging:** Spring AMQP / RabbitMQ
- **Security:** Spring Security (OAuth2 Resource Server, JWT)
- **API Documentation:** SpringDoc OpenAPI / Swagger UI
- **Build Tool:** Maven (via wrapper `./mvnw`)

---

## 3. Database
- **Local / Testing:** H2 In-Memory Database (`jdbc:h2:mem:testdb`). H2 web console accessible at `/h2-console`.
- **Production / Cloud (Render / CloudAMQP):** Configurable via environment variables to PostgreSQL.

---

## 4. Port & Environment Variables

Default port: **`5005`**

| Environment Variable | Description | Default Value (Local) |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port the microservice runs on | `5005` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`local`, `prod`) | `local` |
| `AUTH_SERVER` | URL to Auth Service for token validation | `http://localhost:8080` |
| `ORDER_SERVICE_URL` | URL to Order Service for order verification | `http://localhost:9000` |
| `STRIPE_API_KEY` | Stripe Secret API Key (`sk_test_...`) | *(empty)* |
| `STRIPE_WEBHOOK_SECRET` | Stripe Webhook signing secret (`whsec_...`) | *(empty)* |
| `STRIPE_SUCCESS_URL` | Redirect URL upon successful payment | `http://localhost:5173/payment/success` |
| `STRIPE_CANCEL_URL` | Redirect URL upon canceled payment | `http://localhost:5173/payment/cancel` |
| `RABBITMQ_QUEUE_PAYMENT_SUCCESS` | RabbitMQ queue name for payment notifications | `payment-queue` |
| `SPRING_RABBITMQ_HOST` | RabbitMQ Host | `localhost` |
| `SPRING_RABBITMQ_PORT` | RabbitMQ Port | `5672` |
| `SPRING_RABBITMQ_USERNAME` | RabbitMQ Username | `guest` |
| `SPRING_RABBITMQ_PASSWORD` | RabbitMQ Password | `guest` |
| `DB_URL` | JDBC database connection string (prod profile) | *(configured in cloud)* |
| `DB_USERNAME` | Database username (prod profile) | *(configured in cloud)* |
| `DB_PASSWORD` | Database password (prod profile) | *(configured in cloud)* |

---

## 5. Getting Started

Ensure you have Java 17 (or later) installed.

### Run on Linux / macOS:
```bash
./mvnw spring-boot:run
```

### Run on Windows:
```cmd
mvnw.cmd spring-boot:run
```

Once started, the service is accessible at: `http://localhost:5005`

---

## 6. Documentation & Javadoc

- **Swagger UI (Interactive API docs):** `http://localhost:5005/swagger-ui.html`
- **OpenAPI JSON specification:** `http://localhost:5005/v3/api-docs`
- **Generate Javadoc HTML report:**
  ```bash
  ./mvnw javadoc:javadoc
  ```
  Open the generated report in your browser:
  ```bash
  xdg-open target/reports/apidocs/index.html
  ```

---

## 7. Related Services in the System

This project is part of a distributed microservice architecture:

- [Auth Service](https://github.com/Jimmy-Grafstrom/auth-service-group-2) – User management and JWT issuance (Port: 8080)
- [Product Service](https://github.com/Jimmy-Grafstrom/product-service-group-2) – Product catalog & stock management (Port: 5002)
- [Order Service](https://github.com/Jimmy-Grafstrom/order-service-group-2) – Order processing and order history (Port: 9000)
- [API Gateway](https://github.com/Jimmy-Grafstrom/gateway-group-2) – Routing and centralized gateway entrypoint
- [Frontend](https://github.com/Jimmy-Grafstrom/frontend-group-2) – Web client (built with `npm install` and started with `npm run dev`)
