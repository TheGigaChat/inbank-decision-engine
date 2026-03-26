# Inbank Decision Engine

This project implements a simple loan decision engine as part of the Inbank internship assignment.
The application consists of a **Spring Boot backend** and a **Next.js frontend**, providing a clean API and a user-friendly interface for calculating loan decisions.

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- H2
- REST API
- JUnit & Mockito

### Frontend

- Next.js (App Router)
- React + TypeScript
- Custom CSS (Inbank-inspired design)

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

Note:
Validation annotations in DTOs still use hardcoded values, so configuration and validation are not fully centralized.

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

### Additional Endpoint

`GET /api/config`

Returns loan constraints for frontend usage.

This endpoint is not strictly required by the assignment but was added to keep frontend and backend configuration consistent.

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

- If a user has debt, the loan is not approved.
- Otherwise, the decision is calculated with:

```text
creditModifier * loanPeriod
```

### 3. Decision Flow

1. Reject if the client has debt.
2. Calculate maximum approvable amount for the selected period.
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

- Clean separation between API, business logic, and persistence
- Repository-based profile lookup (instead of in-memory map)
- Externalized configuration via application.yml
- Returning the maximum possible offer, not just the requested amount
- Period optimization: returning the shortest valid period for an approved amount
- Simple and readable architecture prioritizing clarity over over-engineering

## Possible Improvements

- Split frontend logic into structured layers:
  - components
  - hooks
  - services/api
  - utils
  - Currently, most logic is located in a single page.tsx file for simplicity.
- Add integration tests (Spring Boot + database)
- Replace duplicated validation constants with custom annotation-based validation
- Improve error handling with structured error responses
- Add loading states / skeleton UI
- Introduce API versioning
- Add authentication layer if extended further

## Assignment Feedback
One thing I would improve about the take-home assignment is the clarity of the decision logic, especially regarding the relationship between loan amount and loan period.

In the current description, it is not fully clear whether the decision engine should prioritize:
- matching the requested loan amount by adjusting the period, or
- returning the maximum possible loan amount within the selected period, even if it differs from the request.

This ambiguity led to multiple valid interpretations during implementation.

### Suggested Improvement

I would improve the assignment by:
- providing a few additional concrete input/output examples
- explicitly defining the decision priority:
  - whether the system should prioritize the selected period or the requested amount
- clarifying edge cases such as:
  - when the requested amount cannot be satisfied within the selected period
  - when multiple valid solutions exist

This would make the expected behavior more deterministic while still allowing candidates to focus on implementation and design decisions.

## Author

Artur Dzekunov  
TalTech IT Systems Development student
