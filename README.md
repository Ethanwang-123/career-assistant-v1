# Application Tracker

Application Tracker is Phase 1 of a career assistant project. It provides a simple backend API for tracking job applications before adding AI features later.

## Tech Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- H2 in-memory database
- PostgreSQL for Docker deployment
- Docker and Docker Compose
- React
- TypeScript
- Vite
- Maven

## Features

- Add a job application
- View all job applications
- View one job application by ID
- Update a job application
- Delete a job application
- Filter applications by status
- Search applications by keyword across company, role, location, and notes
- Load 5 sample applications at startup
- Register and log in users
- Protect application endpoints with JWT authentication
- Keep each user's applications private
- Automatically store `createdAt` and `updatedAt`
- Return dashboard statistics
- Analyse job descriptions with a mock AI-style keyword extraction service
- Run locally with Docker Compose and PostgreSQL
- Browser frontend for authentication, applications, dashboard, and JD analysis
- Basic validation for required and length-limited fields
- Simple unit and web-layer tests

## Application Status Values

Use one of these exact values for `status`:

```text
NOT_APPLIED
APPLIED
ONLINE_TEST
INTERVIEW
OFFER
REJECTED
```

## API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/auth/register` | Register a user and return a JWT |
| POST | `/auth/login` | Log in and return a JWT |
| POST | `/applications` | Create a new application |
| GET | `/applications` | Get all applications |
| GET | `/applications?status=APPLIED` | Filter applications by status |
| GET | `/applications/search?keyword=cloud` | Search by keyword |
| GET | `/applications/{id}` | Get one application |
| PUT | `/applications/{id}` | Update one application |
| DELETE | `/applications/{id}` | Delete one application |
| GET | `/dashboard/stats` | Get application statistics |
| POST | `/ai/analyse-job-description` | Analyse a job description |

All `/applications/**`, `/dashboard/**`, and `/ai/**` endpoints require a JWT:

```text
Authorization: Bearer <token>
```

## Authentication Examples

Register:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "me@example.com",
    "password": "password123"
  }'
```

Login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "me@example.com",
    "password": "password123"
  }'
```

Use the returned token on protected requests:

```bash
curl http://localhost:8080/applications \
  -H "Authorization: Bearer <token>"
```

## Example Request

```bash
curl -X POST http://localhost:8080/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "companyName": "OpenAI",
    "roleTitle": "Backend Developer",
    "location": "Remote",
    "status": "APPLIED",
    "applicationDate": "2026-06-18",
    "deadline": "2026-07-01",
    "notes": "Submitted through company careers page."
  }'
```

## Search Examples

Filter by status:

```bash
curl "http://localhost:8080/applications?status=APPLIED"
```

Search by keyword:

```bash
curl "http://localhost:8080/applications/search?keyword=cloud"
```

Get dashboard stats:

```bash
curl http://localhost:8080/dashboard/stats \
  -H "Authorization: Bearer <token>"
```

Example response:

```json
{
  "totalApplications": 1,
  "totalInterviews": 1,
  "totalOffers": 0,
  "totalRejected": 0
}
```

Analyse a job description:

```bash
curl -X POST http://localhost:8080/ai/analyse-job-description \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "jobDescription": "We need a Java Spring Boot engineer to build REST API services for cloud-native distributed systems on AWS. Docker, Kubernetes, Terraform, SQL, LangChain and AI agent experience are preferred."
  }'
```

Example response:

```json
{
  "roleType": "AI / Cloud Software Engineer",
  "requiredSkills": ["Java", "Spring Boot", "REST API", "SQL"],
  "preferredSkills": ["AWS", "Docker", "Kubernetes", "Terraform", "LangChain", "AI agent"],
  "responsibilities": ["Build and maintain backend services"],
  "cloudRelated": true,
  "aiRelated": true,
  "summary": "This looks like a AI / Cloud Software Engineer role requiring Java, Spring Boot, REST API, SQL. The job description has a clear cloud or deployment focus. It also includes AI-related work.",
  "suggestedProjects": ["Build a secure Spring Boot REST API with JWT authentication and validation"]
}
```

Look up a missing application:

```bash
curl http://localhost:8080/applications/999
```

This returns a `404` response with a JSON error body.

## How to Run Locally

Run the tests:

```bash
./mvnw test
```

Start the API:

```bash
./mvnw spring-boot:run
```

The API will run at:

```text
http://localhost:8080
```

The H2 database console is available at:

```text
http://localhost:8080/h2-console
```

Use these H2 settings:

- JDBC URL: `jdbc:h2:mem:application_tracker`
- User Name: `sa`
- Password: leave blank

## How to Run the Frontend

The React frontend lives in:

```text
frontend/
```

Create a frontend env file:

```bash
cd frontend
cp .env.example .env
```

For the normal backend port, keep:

```text
VITE_API_BASE_URL=http://localhost:8080
```

Install dependencies and start Vite:

```bash
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

Example full-stack workflow:

1. Start the backend with `./mvnw spring-boot:run` or `docker compose up --build`.
2. Start the frontend with `npm run dev` inside `frontend/`.
3. Register a user in the browser.
4. Add a job application.
5. View dashboard statistics.
6. Paste a job description into the JD Analysis page.

## How to Run with Docker

Build the Docker image:

```bash
docker build -t application-tracker .
```

Run the API and PostgreSQL together:

```bash
docker compose up --build
```

The API will run at:

```text
http://localhost:8080
```

Docker Compose starts two services:

- `postgres`: PostgreSQL database
- `app`: Spring Boot API

The app uses the `docker` Spring profile and reads these environment variables from `docker-compose.yml`:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
```

Test the API after startup:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "docker@example.com",
    "password": "password123"
  }'
```

Login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "docker@example.com",
    "password": "password123"
  }'
```

Use the returned token:

```bash
curl http://localhost:8080/applications \
  -H "Authorization: Bearer <token>"
```

Analyse a job description:

```bash
curl -X POST http://localhost:8080/ai/analyse-job-description \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "jobDescription": "Java Spring Boot REST API role using AWS, Docker, Kubernetes, Terraform, SQL and cloud-native distributed systems."
  }'
```

Stop the containers:

```bash
docker compose down
```

Remove the PostgreSQL volume if you want a clean database next time:

```bash
docker compose down -v
```

## Project Structure

```text
src/main/java/com/example/applicationtracker
├── ApplicationTrackerApplication.java
├── config
│   ├── JpaAuditingConfig.java
│   └── SecurityConfig.java
├── controller
│   ├── AuthController.java
│   ├── AiController.java
│   ├── DashboardController.java
│   └── JobApplicationController.java
├── dto
│   ├── AiJobDescriptionRequest.java
│   ├── AiJobDescriptionResponse.java
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   └── DashboardStatsResponse.java
├── entity
│   └── AppUser.java
│   └── JobApplication.java
│   └── JobApplicationStatus.java
├── exception
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository
│   └── AppUserRepository.java
│   └── JobApplicationRepository.java
├── security
│   ├── AppUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
└── service
    └── AiService.java
    └── AuthService.java
    └── DashboardService.java
    └── JobApplicationService.java
```

Docker files:

```text
Dockerfile
docker-compose.yml
.dockerignore
src/main/resources/application-docker.properties
```

Frontend files:

```text
frontend/
├── src/api/api.ts
├── src/components
├── src/context/AuthContext.tsx
├── src/pages
├── src/types
└── src/styles.css
```

## Frontend Auth Flow

The frontend stores the JWT in `localStorage` after register or login. Protected API requests go through `src/api/api.ts`, which adds:

```text
Authorization: Bearer <token>
```

If no token exists, protected frontend routes redirect to `/login`.

## Security Flow

1. A user registers or logs in with email and password.
2. The password is stored as a BCrypt hash, not plain text.
3. The backend returns a JWT.
4. The client sends the JWT on later requests using the `Authorization` header.
5. `JwtAuthenticationFilter` checks the token signature and expiration.
6. If the token is valid, Spring Security treats the request as authenticated.
7. Application queries use the logged-in user's email, so users can only access their own applications.

## Auditing

Spring Data JPA auditing fills these fields automatically on `JobApplication`:

- `createdAt`
- `updatedAt`

`createdAt` is set when the row is first saved. `updatedAt` changes whenever the row is updated.

## Mock AI Job Description Analysis

`AiService` currently uses keyword extraction rules instead of calling a real AI provider. It detects terms such as:

- Java
- Python
- Spring Boot
- REST API
- AWS
- Azure
- Google Cloud
- Docker
- Kubernetes
- SQL
- TensorFlow
- LangChain
- GitLab CI
- Terraform
- AI agent
- cloud-native
- distributed systems

This gives the project an AI-style feature without depending on external APIs during early development.

Later, `AiService` can be replaced with a provider-backed implementation:

- OpenAI: send the job description to a model and ask for structured JSON matching `AiJobDescriptionResponse`.
- Gemini: use the same DTO response shape, but call Gemini from inside `AiService`.
- Apple Foundation Models: keep the controller and DTOs, but route analysis to an on-device model where available.

The API contract can stay the same while the internal implementation changes.

## Future Improvements

- Add MySQL configuration for persistent local development
- Add production Docker secrets instead of plain Compose environment variables
- Add cloud deployment using AWS, Azure, Google Cloud, or Render/Fly.io
- Add user accounts and authentication
- Add filters by company, status, date, and deadline
- Replace mock AI job description analysis with a real AI provider
- Add AI resume and cover letter suggestions
- Deploy the backend to a cloud platform
- Add a frontend dashboard
