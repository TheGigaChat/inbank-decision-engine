# Inbank Decision Engine

This project implements a simple loan decision engine as part of the Inbank internship assignment.
The application consists of a **Spring Boot backend** and a **Next.js frontend**, providing a clean API and a user-friendly interface for calculating loan decisions.

## Tech Stack

### Backend

- Java 21
- Spring Boot
- REST API
- JUnit for testing

### Frontend

- Next.js with App Router
- React with TypeScript
- CSS with a custom design system based on Inbank style

## Project Structure

```text
backend/   Spring Boot application
frontend/  Next.js application
docs/      Tests documentation
```

## How to Run (Docker)

From the project root:

```bash
docker compose up --build
```

## How to Run (Manually)

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
cp .env.example .env.local
npm run dev
```

Frontend runs on `http://localhost:3000`.

## Configuration

Loan constraints are externalized in `application.yml`:

- min amount
- max amount
- min period
- max period

This allows changing business rules without modifying code.
At the same time, some request validation annotations still use hardcoded limits in the backend, so configuration and validation are not fully driven by the same source yet.

## API

### Endpoint

`POST /api/decision`

### Request

```json
{
  "personalCode": "49002010965",
  "loanAmount": 5000,
  "loanPeriod": 24
}
```

### Response: Positive

```json
{
  "decision": "POSITIVE",
  "approvedAmount": 5000,
  "approvedPeriod": 50
}
```

### Response: Negative

```json
{
  "decision": "NEGATIVE",
  "approvedAmount": null,
  "approvedPeriod": null
}
```

I also used a support endpoint GET /api/config which is a bit above the task requirements and I need it for the consistent consts from application.yml file. I can remove it and everything will work with only 1 endpoint how is declared in the task, however we will not be able to change the consts from one place without changing the code.

## Business Logic

### 1. User Segmentation

Each personal code is mapped to a profile:

| Personal Code | Segment   | Credit Modifier |
| ------------- | --------- | --------------- |
| 49002010965   | Segment 1 | 100             |
| 49002010976   | Segment 2 | 300             |
| 49002010987   | Segment 3 | 1000            |
| 49002010998   | Debt      | Not allowed     |

### 2. Rules

If a user has debt, the loan is not approved.

Otherwise, the decision is calculated with:

```text
creditModifier * loanPeriod >= loanAmount
```

### 3. Decision Flow

1. Reject if the client has debt.
2. Calculate the maximum approvable amount for the selected period.
3. If the selected period produces a valid loan amount, return it, even if it is lower than the requested amount.
4. Otherwise, try to find the smallest new period that produces a valid loan amount.
5. If no valid period exists, return `NEGATIVE`.

### 4. Constraints

| Field       | Min  | Max   |
| ----------- | ---- | ----- |
| Loan amount | 2000 | 10000 |
| Loan period | 12   | 60    |

Invalid input returns `400 Bad Request`.

## Testing

The backend includes:

- Unit tests for profile resolution
- Unit tests for decision logic
- Validation tests for the controller

The frontend is tested manually through UI scenarios.

## UI

The frontend follows a clean fintech-style design inspired by Inbank:

- Minimal layout
- Soft neutral colors
- Clear hierarchy
- Responsive design
- Interactive sliders for inputs

## Design Decisions

- Strict backend validation with no silent corrections
- Returning the maximum possible offer, not only the requested amount
- Record-based DTOs where appropriate for cleaner code
- Separation of concerns between controller, service, and profile logic

## Possible Improvements

- Add integration tests
- Writing custom annotation validation for the consts
- Improve error handling with structured responses
- Add loading skeletons or animations in the UI
- Add versioning for API requests

## Author

Artur Dzekunov  
TalTech IT Systems Development student
