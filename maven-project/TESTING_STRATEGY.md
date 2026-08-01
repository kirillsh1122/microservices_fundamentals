# Testing Strategy for MicroservicesFundamentals

Checklist (what this document delivers)
- [x] Overall testing goals and principles
- [x] Recommended test pyramid and numeric targets
- [x] Detailed approach for: Unit, Integration, Component, Contract, and End-to-End tests
- [x] Tooling, CI hints and example commands
- [x] Mapping to the existing repository structure and next steps

---

## 1. Goals and Principles

- Ensure correctness and prevent regressions while keeping builds fast and reliable.
- Favor fast, deterministic unit tests; run heavier integration/contract/E2E tests selectively.
- Use realistic runtime dependencies (Testcontainers) in integration tests.
- Enforce service boundaries through consumer-driven contract tests.
- Keep E2E small and stable — test only critical user flows.

## 2. Test Pyramid & Targets

- Unit & component tests: 60–75% of test volume. Target ~70% line coverage for business code.
- Integration tests (DB/Kafka wiring, Spring context): 20–30% of test volume.
- Contract tests: cover 100% of public API surfaces used by consumers (not counted as high-volume tests).
- End-to-end tests: small set (2-3 tests) focused on critical journeys.

Coverage guidance: prefer sensible coverage targets (e.g., 70% line) and prioritize tests for critical paths rather than forcing 100%.

## 3. Test Types and Approaches

### Unit Tests
- Scope: Single class or method. No network, DB, or broker.
- Tools: JUnit 5, Mockito, AssertJ.
- Practices: small focused tests, parameterized tests when appropriate, test builders for object creation.
- Run: Maven Surefire (mvn test). Keep these extremely fast.

### Component Tests
- Scope: Validate a service module's public behavior (HTTP layer, configuration, serialization) but with external services stubbed.
- Tools: Spring's @WebMvcTest, MockMvc, WireMock for HTTP stubs.
- Practices: Start only the needed slices of Spring context to keep tests fast.
- Run: as part of unit phase (Surefire).

### Integration Tests
- Scope: Service integration with real DB, Kafka, and full Spring context.
- Tools: @SpringBootTest, Testcontainers (Postgres, Kafka), JUnit 5, Maven Failsafe for *IT tests.
- Strategy: Use *IT.java naming for integration tests;
- Isolation: Prefer a shared Testcontainers instance per-module to reduce startup cost, but ensure DB cleanup between tests.

### Contract Tests (Consumer-Driven)
- Purpose: Protect service-to-service boundaries.
- Tools: Pact or Spring Cloud Contract. For messaging use Spring Cloud Contract for Kafka with compatibility checks.
- Flow: Consumers publish contracts. Providers run verification in against those contracts.
- Run: Consumers generate contracts in their consumer-test phase; providers verify contracts.

### End-to-End (E2E) Tests
- Scope: Full system tests that exercise real deployments and real integrations to validate critical business flows.
- Tools: JUnit for API flows, Spring Cloud Contract for messaging flows, Docker Compose or test Kubernetes cluster for environment.
- Practice: Keep E2E to a minimal set of stable smoke and critical path tests. Run them in gated pipelines or nightly builds.

## 4. Messaging & Asynchronous Workflows

- Unit tests for message handlers with mocked brokers.
- Integration tests with Testcontainers Kafka to verify end-to-end processing and retry/ack behavior.
- Contract tests for message payloads using JSON schema compatibility checks.

## 5. Databases

- Use Testcontainers Postgres/MySQL for integration tests. Avoid relying on in-memory H2 semantics alone.

## 6. CI & Maven lifecycle recommendations

- Use Surefire for fast unit/component tests and Failsafe for integration tests.
- Example conventions:
  - Unit & component tests: src/test/java, run in test phase (Surefire)
  - Integration tests: *IT.java, run in verify phase (Failsafe)

## 7. Test Data and Isolation

- Use Test Data Builders and factories to create test objects.
- Prefer fresh data per test; use transactions with rollback or schema reset between integration tests.
- Seed only minimal required data via migrations or test-only SQL fixtures.

## 8. Observability and Reporting

- Publish JUnit XML and use test reporting (Allure or built-in CI test reporting) for triage.
- Store historical test-run data to detect trend/flakiness.

## 9. Enforcement and Governance

- Enforce contract verification for providers in CI and fail builds on contract mismatch.
- Use Jacoco to enforce a sensible minimum coverage (e.g., 70% line coverage) and gate merges accordingly.

## 10. Mapping to This Repository (practical guidance)

- `resource-service`, `resource-processor`, `song-service`: unit tests for business logic; integration tests (Testcontainers DB); consumer/provider contract tests for APIs and message contracts.

## 12. Numeric Targets (starting point)

- Unit/component tests: 60–75% of tests; target ~70% line coverage.
- Integration tests: 20–30% by count; cover DB/Kafka integration.
- Contract tests: cover 100% of public APIs used by consumers.
- E2E: small set (2–3 critical tests).

## 14. Next Steps / Recommendations

1. Add Maven plugin snippets to each module's `pom.xml`:
   - `maven-failsafe-plugin` for integration tests
2. Add Testcontainers dependency and a shared test-utility module with base container setup and test data builders.
3. Choose a contract tooling (Spring Cloud Contract) and add a contract publishing/verification.
4. Create a small E2E smoke suite that can run deterministically against the compose stack.

---

# Tests implemented

1. 14 Unit & Component tests for `resource-service` (JUnit 5, Mockito, AssertJ)
2. Integration (1 Scenario with 5 steps) tests for `resource-processor` (Cucumber BDD framework)
3. Contract tests for `resource-processor` (consumer side) (Spring Cloud Contract + Test Containers) - In Progress
4. E2E tests for `resource-processor` (Spring Cloud Contract + Test Containers) - TBD