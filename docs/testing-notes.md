# Testing Notes

## 2026-03-25

### Added tests for profile code mapping
- Why: `ProfileServiceImpl` is currently a hardcoded lookup table, so a typo in a personal code or returned profile would silently change downstream decision behavior.
- Protects: the exact mapping for modifier `100`, `300`, `1000`, the debt code, and the unknown-code fallback currently implemented as `creditModifier = 0` and `hasDebt = false`.
- Edge cases discovered: the fallback behavior is already concretely implemented in code, so the test suite should lock down that current contract instead of leaving the unknown-code case ambiguous.

### Added tests for decision calculation branches
- Why: `DecisionServiceImpl` now contains stable logic for debt rejection, selected-period approval, smallest-fitting alternative-period search, capped approval amounts, and the best-possible-offer fallback at `60` months.
- Protects: debt still returns `NEGATIVE`; selected period can approve the request; selected period can return more than requested; the service chooses the smallest later period that satisfies the amount; when no period can satisfy the request, it returns the largest possible offer at `60` months; and modifier `0` still produces a negative decision.
- Edge cases discovered: the current BLL no longer uses requested amount as the deciding factor for eligibility. The approved offer is now driven by credit modifier and period, so different requested amounts can legitimately produce the same approved amount and period.
- Important refactor caused by tests: once loan constraints moved into `application.yml`, the service tests were rewritten to construct `LoanConstraintsProperties` explicitly so unit coverage stays independent from Spring configuration binding.
- Assumption: tests continue to target service-level business logic only, not validation or controller HTTP behavior.

### Added focused controller validation tests
- Why: the global error handler makes validation behavior part of the controller contract, so a small `@WebMvcTest` suite is now worth locking down.
- Protects: valid requests return `200`, invalid request fields return `400`, and validation errors expose the failing field name in the JSON body.
- Assumption: the controller tests intentionally mock `DecisionService` so they verify request binding, bean validation, and exception handling without turning into full application integration tests.

### Added config endpoint controller test
- Why: the frontend now depends on `/api/config`, so the controller contract should be locked down separately from YAML binding.
- Protects: `GET /api/config` returns `200` and exposes `minAmount`, `maxAmount`, `minPeriod`, and `maxPeriod` in JSON.
- Assumption: the controller test mocks `LoanConstraintsProperties`; the real YAML values are still verified by the dedicated config-binding test.

### Added config-binding coverage for loan constraints
- Why: unit tests now build `LoanConstraintsProperties` manually, so a separate Spring-backed test is needed to catch accidental changes in `application.yml` binding.
- Protects: `loan.constraints.min-amount`, `max-amount`, `min-period`, and `max-period` bind to the expected values in the real application context.
- Assumption: this test is intentionally narrow and checks binding only, not end-to-end service behavior through Spring.

### Assignment interpretation recorded
- The current scope is unit tests for `ProfileServiceImpl` and `DecisionServiceImpl`, focused controller-level validation coverage, one targeted config-binding test, and one focused config endpoint controller test.
- Full Spring Boot integration coverage beyond config binding, frontend tests, and config externalization tests remain intentionally deferred.
