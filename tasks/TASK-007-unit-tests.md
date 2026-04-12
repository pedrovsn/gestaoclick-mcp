# TASK-007 - Unit tests for query handlers and ERP mapping

## Task ID
`TASK-007`

## Title
Add unit tests for query handlers and ERP mapping logic

## Goal
Ensure the core business logic — query handlers and ERP-to-API mapping — is covered by unit tests. These are the components most likely to break when the ERP contract changes or new fields are added.

## Context
The project currently has no unit tests beyond the default Spring Boot context test. Per project guidelines, tests should focus on command/query handlers and ERP mapping logic. Controllers are thin and do not need dedicated tests.

## Requirements
- Test `ListSalesByPeriodHandler`: pagination aggregation, date validation, ERP-to-API mapping
- Test `ListProductsHandler`: pagination aggregation, ERP-to-API mapping including nested price tiers and variations
- Mock the `ErpClient` — do not call the real ERP
- Keep tests simple and focused

## Acceptance Criteria
- `ListSalesByPeriodHandler` has tests for: single page, multi-page pagination, date validation error, mapping of sales/payments/products/services
- `ListProductsHandler` has tests for: single page, multi-page pagination, mapping of products/price tiers/variations
- All tests pass with `./gradlew test`
- No real ERP calls are made

## Out of Scope
- Integration tests
- Controller tests
- ERP client tests (would require mocking RestTemplate internals)
