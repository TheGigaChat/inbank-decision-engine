# Testing Notes

## 2026-03-25

### Added tests for profile code mapping
- Why: `ProfileServiceImpl` is currently a hardcoded lookup table, so a typo in a personal code or returned profile would silently change downstream decision behavior.
- Protects: the exact mapping for modifier `100`, `300`, `1000`, the debt code, and the unknown-code fallback currently implemented as `creditModifier = 0` and `hasDebt = false`.
- Edge cases discovered: the fallback behavior is already concretely implemented in code, so the test suite should lock down that current contract instead of leaving the unknown-code case ambiguous.

### Added tests for decision calculation branches
- Why: `DecisionServiceImpl` now contains stable logic for debt rejection, selected-period approval, smallest-fitting alternative-period search, capped approval amounts, and the best-possible-offer fallback at `60` months.
- Protects: debt still returns `NEGATIVE`; selected period can approve the request; selected period can return more than requested; the service chooses the smallest later period that satisfies the amount; when no period can satisfy the request, it returns the largest possible offer at `60` months; and modifier `0` still produces a negative decision.
- Edge cases discovered: the alternative-period branch returns the maximum amount available at the first suitable period, which can equal the requested amount exactly for `2000 / 20` with modifier `100`.
- Important refactor caused by tests: once loan constraints moved into `application.yml`, the service tests were rewritten to construct `LoanConstraintsProperties` explicitly so unit coverage stays independent from Spring configuration binding.
- Assumption: tests continue to target service-level business logic only, not validation or controller HTTP behavior.

### Added focused controller validation tests
- Why: the global error handler makes validation behavior part of the controller contract, so a small `@WebMvcTest` suite is now worth locking down.
- Protects: valid requests return `200`, invalid request fields return `400`, and validation errors expose the failing field name in the JSON body.
- Assumption: the controller tests intentionally mock `DecisionService` so they verify request binding, bean validation, and exception handling without turning into full application integration tests.

### Assignment interpretation recorded
- The current scope is unit tests for `ProfileServiceImpl` and `DecisionServiceImpl`, plus focused controller-level validation coverage.
- Full Spring Boot integration coverage, frontend tests, and config externalization tests remain intentionally deferred.
