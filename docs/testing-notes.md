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

### Assignment interpretation recorded
- The current scope is unit tests only for `ProfileServiceImpl` and the debt branch of `DecisionServiceImpl`.
- Validation, controller behavior, amount-period calculations, alternative-period logic, and integration coverage are intentionally deferred until those behaviors become stable.
