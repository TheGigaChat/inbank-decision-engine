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

## How to Run

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

1. Check the selected period.
2. If it can satisfy the requested amount, approve the loan.
3. Return the maximum possible amount for that period.
4. If the selected period fails, find the smallest period that satisfies the request.
5. If no period satisfies the request, return the largest possible amount within 60 months.
6. If even the minimum amount of `2000 EUR` is not possible, return `NEGATIVE`.

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

- Externalize configuration such as limits and modifiers into `application.yml`
- Add integration tests
- Improve error handling with structured responses
- Add loading skeletons or animations in the UI
- Deploy backend and frontend with Docker or a cloud platform

## Author

Artur Dzekunov  
TalTech IT Systems Development student
