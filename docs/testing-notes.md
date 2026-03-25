# Testing Notes

## 2026-03-25

### Added tests for profile code mapping
- Why: `ProfileServiceImpl` is currently a hardcoded lookup table, so a typo in a personal code or returned profile would silently change downstream decision behavior.
- Protects: the exact mapping for modifier `100`, `300`, `1000`, the debt code, and the unknown-code fallback currently implemented as `creditModifier = 0` and `hasDebt = false`.
- Edge cases discovered: the fallback behavior is already concretely implemented in code, so the test suite should lock down that current contract instead of leaving the unknown-code case ambiguous.

### Added test for the stable debt branch in decision calculation
- Why: `DecisionServiceImpl` only contains one non-placeholder behavior today, the debt rejection path.
- Protects: when a resolved profile has debt, the service returns `Decision.NEGATIVE` and leaves both approved values `null`.
- Assumption: the assignment explicitly says not to test the non-debt branch if it still returns `null`, so that path is intentionally left uncovered for now.

### Expanded decision-service tests around selected-period logic
- Why: the service now contains stable selected-period approval logic, so the test suite should protect both the exact-fit case and the branch that returns the maximum amount available for the requested period.
- Protects: a profile with modifier `100` approves `5000 / 50` when requesting `5000 / 50`, and also approves `5000 / 50` when requesting only `2000 / 50`.
- Edge cases discovered: for a modifier of `0`, the current business expectation is a negative decision rather than a `null` response, even though the implementation still returns `null` for unsupported cases today.
- Assumption: the failing selected-period case is intentionally asserted as `NEGATIVE` for now, with the understanding that this test will be updated once alternative-period search is implemented.

### Assignment interpretation recorded
- The current scope is unit tests only for `ProfileServiceImpl` and the currently agreed `DecisionServiceImpl` algorithm stages.
- Validation, controller behavior, amount-period calculations, alternative-period logic, and integration coverage are intentionally deferred until those behaviors become stable.
