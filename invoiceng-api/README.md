# InvoiceNG API

WhatsApp-based Invoice & Payment Collection SaaS for Nigerian SMEs.

## Tech Stack

- **Framework**: Spring Boot 3.2
- **Language**: Java 21
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA + Hibernate
- **Migrations**: Flyway
- **Security**: Spring Security + JWT
- **API Docs**: SpringDoc OpenAPI (Swagger)

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16 (or Docker)
- Paystack account (for payments)
- Termii account (for SMS OTP)

## Quick Start

### Using Docker Compose

```bash
# Start PostgreSQL and API
docker-compose up -d

# View logs
docker-compose logs -f api
```

### Manual Setup

1. **Create PostgreSQL database**:
```sql
CREATE DATABASE invoiceng;
```

2. **Set environment variables**:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/invoiceng
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
export JWT_SECRET=your-256-bit-secret-key
export PAYSTACK_SECRET_KEY=sk_test_xxxxx
export TERMII_API_KEY=your-termii-api-key
```

3. **Run the application**:
```bash
./mvnw spring-boot:run
```

## API Documentation

Once running, access the Swagger UI at:
- http://localhost:8080/swagger-ui.html

OpenAPI JSON spec:
- http://localhost:8080/api-docs

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/invoiceng` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | JWT signing secret (min 32 chars) | - |
| `JWT_EXPIRATION` | Token expiry in ms | `86400000` (24h) |
| `PAYSTACK_SECRET_KEY` | Paystack secret key | - |
| `PAYSTACK_PUBLIC_KEY` | Paystack public key | - |
| `PAYSTACK_CALLBACK_URL` | Payment callback URL | - |
| `TERMII_API_KEY` | Termii SMS API key | - |
| `TERMII_SENDER_ID` | SMS sender ID | `InvoiceNG` |

### Profiles

- `dev` - Development mode with debug logging
- `prod` - Production mode with optimized settings

## API Endpoints

### Authentication
- `POST /api/v1/auth/request-otp` - Request OTP
- `POST /api/v1/auth/verify-otp` - Verify OTP and login
- `POST /api/v1/auth/refresh` - Refresh token

### Users
- `GET /api/v1/users/me` - Get current user
- `PUT /api/v1/users/me` - Update profile

### Customers
- `GET /api/v1/customers` - List customers
- `GET /api/v1/customers/{id}` - Get customer
- `POST /api/v1/customers` - Create customer
- `PUT /api/v1/customers/{id}` - Update customer
- `DELETE /api/v1/customers/{id}` - Delete customer

### Invoices
- `GET /api/v1/invoices` - List invoices
- `GET /api/v1/invoices/{id}` - Get invoice
- `POST /api/v1/invoices` - Create invoice
- `PUT /api/v1/invoices/{id}` - Update invoice
- `DELETE /api/v1/invoices/{id}` - Delete invoice
- `POST /api/v1/invoices/{id}/send` - Send invoice
- `POST /api/v1/invoices/{id}/cancel` - Cancel invoice
- `POST /api/v1/invoices/{id}/duplicate` - Duplicate invoice

### Payments
- `POST /api/v1/payments/initialize` - Initialize payment
- `GET /api/v1/payments/verify/{reference}` - Verify payment

### Dashboard
- `GET /api/v1/dashboard/stats` - Get dashboard statistics
- `GET /api/v1/dashboard/top-customers` - Get top customers

### Webhooks
- `POST /api/v1/webhooks/paystack` - Paystack payment webhook

## Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## Building

```bash
# Build JAR
./mvnw package -DskipTests

# Build Docker image
docker build -t invoiceng-api .
```

## Deployment

### Railway

1. Connect your GitHub repository
2. Set environment variables in Railway dashboard
3. Deploy

### Manual

```bash
java -jar target/invoiceng-api-1.0.0-SNAPSHOT.jar
```

## Project Structure

```
src/main/java/com/invoiceng/
├── InvoiceNgApplication.java     # Main application
├── config/                       # Configuration classes
├── controller/                   # REST controllers
├── service/                      # Business logic
├── repository/                   # Data access
├── entity/                       # JPA entities
├── dto/                          # Data transfer objects
│   ├── request/
│   └── response/
├── exception/                    # Custom exceptions
├── security/                     # JWT security
└── util/                         # Utilities
```

## License

Proprietary - All rights reserved.
