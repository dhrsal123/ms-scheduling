# Theater Seat Scheduling API

A microservice for managing movie scheduling within theater rooms, including showtime creation, updates, retrieval, and batch movie information lookup.

## Key features

- **Schedule Management**: Create, update, and delete movie showtimes for specific theater rooms
- **Paginated Retrieval**: Get scheduled movies with pagination support
- **Batch Movie Lookup**: Efficiently fetch movie details by multiple IDs for inter-service communication
- **Role-Based Security**: Secured with JWT-based authentication, restricting management operations to EMPLOYEE role
- **Standardized Error Handling**: Unified error response format for consistent frontend integration
- **Caching**: Spring Cache annotations for improved read performance

## Prerequisites

- Java 21
- Docker & Docker Compose
- Keycloak
- PostgreSQL

## Environment configuration

Create a `.env` file in the root directory based on the example below:

```env
# --- Redis Config ---
REDIS_HOST=localhost
REDIS_PASSWORD=YOUR_REDIS_PASSWORD

# --- DB Config ---
POSTGRES_DB=cinema_db
POSTGRES_HOST=localhost
POSTGRES_PASSWORD=YOUR_POSTGRES_PASSWORD
POSTGRES_USER=postgres

# --- Keycloak ---
KEYCLOAK_HOST=localhost

# --- LOKI ---
LOKI_HOST=localhost
```

## API Documentation

Once the service is running, you can explore the full API specification:

- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/v3/api-docs
- **Static OpenAPI**: http://localhost:8082/openapi.yaml (served from `src/main/resources/static/openapi.yaml`)

## Usage

### Role-Based Access Control

This service distinguishes between public information and management operations. Management endpoints require a valid JWT with the `EMPLOYEE` authority.

### Endpoint Examples

| Category        | Method | Path                                              | Required Role |
|-----------------|--------|---------------------------------------------------|---------------|
| Schedule Movies | GET    | `/api/v1/theaters/{theaterId}/schedule-movies/{roomId}` | None          |
| Schedule Movies | POST   | `/api/v1/theaters/{theaterId}/schedule-movies/{roomId}` | EMPLOYEE      |
| Schedule Movies | PUT    | `/api/v1/theaters/{theaterId}/schedule-movies/{roomId}/{scheduleId}` | EMPLOYEE      |
| Schedule Movies | DELETE | `/api/v1/theaters/{theaterId}/schedule-movies/{roomId}/{scheduleId}` | EMPLOYEE      |
| Movies          | POST   | `/api/v1/movies/batch`                            | None          |

### Request/Response Examples

**Create Schedule Movie Request:**
```json
{
  "movieId": "uuid-of-movie",
  "start": "2026-07-25T14:00:00",
  "end": "2026-07-25T16:30:00"
}
```

**Scheduled Movie Response:**
```json
{
  "movieId": "uuid-of-movie",
  "title": "Inception",
  "posterUrl": "https://example.com/poster.jpg",
  "start": "2026-07-25T14:00:00",
  "end": "2026-07-25T16:30:00"
}
```

**Batch Movies Request:**
```json
{
  "movieIds": ["uuid-1", "uuid-2", "uuid-3"]
}
```

### Validation Rules

- `start` time must be present or future
- `end` time must be in the future
- `end` time must be after `start` time (enforced by DB constraint)
- `movieId` is required and must be a valid UUID

## Error Handling

The service returns a standardized `BaseErrorDto` for all business and technical exceptions:

```json
{
  "message": "The requested schedule could not be found.",
  "code": "CINEMA-001",
  "status": "NOT_FOUND"
}
```

### Common Error Codes

| Code        | HTTP Status | Description                          |
|-------------|-------------|--------------------------------------|
| CINEMA-001  | 400/404     | Resource Not Found / Validation Fail |
| CINEMA-002  | 500         | Technical / Database Error           |
| CINEMA-003  | 409         | Data Conflict                        |
| CINEMA-004  | 403         | Forbidden (Missing EMPLOYEE Role)    |
| CINEMA-005  | 401         | Unauthorized (Missing/Expired Token) |

## Database Migrations

Liquibase migrations are located in `src/main/resources/db/changelog/` and run automatically on application startup.

### Schema

- **scheduled_movie**: Stores movie showtimes per theater room
  - Indexes on `(theater_id, room_id)`, `movie_id`, and `start`
  - Check constraint ensuring `end > start`

## Running Locally

```bash
# Start dependencies (PostgreSQL, Redis, Keycloak)
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

The service will be available at `http://localhost:8082`