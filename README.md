# Client Onboarding API

A REST API simulating a client onboarding workflow for financial services, built with Spring Boot and PostgreSQL.

Inspired by 3 years of professional experience implementing client onboarding and lifecycle management workflows for a major wealth management bank.

## Tech Stack
- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Maven

## How to Run Locally

1. Clone the repository
2. Configure your PostgreSQL connection in `application.properties`
3. Run `mvn spring-boot:run`
4. API available at `http://localhost:8080`

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/clients` | Create a new client |
| GET | `/api/clients` | List all clients |
| GET | `/api/clients?status=DRAFT` | Filter clients by status |
| GET | `/api/clients/{id}` | Get a client by ID |
| PATCH | `/api/clients/{id}/advance` | Advance client to next status |
| PATCH | `/api/clients/{id}/reject` | Reject a client |

## Client Status Flow
DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED
