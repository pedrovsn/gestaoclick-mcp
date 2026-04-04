# backend/AGENTS.md

## Scope

The `backend` folder contains the Kotlin + Spring Boot API responsible for:

- consuming the ERP API
- transforming returned data
- exposing internal endpoints for reports and custom queries
- serving as the foundation for OpenClaw/MCP integration

## Mandatory rules

### 1. Stack and language
- Use idiomatic Kotlin.
- Use Spring Boot.
- Prefer native Spring features before adding extra libraries.
- Keep the number of dependencies as low as possible.

### 2. No persistence
- Do not create JPA entities.
- Do not add Spring Data JPA.
- Do not create database repositories.
- Do not introduce migrations.
- Do not depend on a database to function.

### 3. Simple CQRS
Separate responsibilities into:
- `command`: operations that trigger an action or orchestration
- `query`: read-only operations

This separation is organizational, not distributed.

#### Base interfaces (mandatory)

There are two marker interfaces in `handler/`:

```kotlin
// handler/Command.kt
interface Command

// handler/Query.kt
interface Query
```

**Every input object for a write operation must implement `Command`.**
**Every input object for a read operation must implement `Query`.**

Usage example:

```kotlin
// application/command/SyncSalesCommand.kt
data class SyncSalesCommand(val fromDate: LocalDate) : Command

// application/query/GetSalesSummaryQuery.kt
data class GetSalesSummaryQuery(val fromDate: LocalDate, val toDate: LocalDate) : Query
```

Handlers receive the respective type:

```kotlin
// application/command/SyncSalesHandler.kt
@Component
class SyncSalesHandler(private val erpClient: ErpClient) {
    fun handle(command: SyncSalesCommand): SyncResult { ... }
}

// application/query/GetSalesSummaryHandler.kt
@Component
class GetSalesSummaryHandler(private val erpClient: ErpClient) {
    fun handle(query: GetSalesSummaryQuery): SalesSummaryResponse { ... }
}
```

#### What this means in practice
- Queries return data and do not mutate domain state.
- Commands can trigger flows such as on-demand sync, composition, validation, or report preparation.
- Do not build a complex command/query bus if direct calls suffice.
- Always use the `Command` and `Query` interfaces to type input objects — this keeps the contract explicit and makes it easy to track what is a read and what is a write.

### 4. Current structure

```text
backend/src/main/kotlin/com/gestaoclick/mcp/
  config/              ← ErpConfig, ErpProperties
  controller/          ← thin controllers (delegate to handlers)
  exception/           ← ErpIntegrationException, GlobalExceptionHandler
  handler/             ← Command and Query interfaces
  integration/
    erp/               ← ErpClient and ERP response models
  application/
    command/           ← handlers and objects implementing Command
    query/             ← handlers and objects implementing Query
```

If needed, the following may also be added:
- `mcp/`
- `dto/` (our API's response DTOs)

Avoid creating unnecessary layers.

### 5. ERP integration
Every ERP call must:
- go through a dedicated client/gateway
- have a configured timeout
- handle failures explicitly
- map external responses to internal models when that improves the contract

#### Integration rules
- Do not mix HTTP logic directly in controllers.
- Do not scatter ERP calls throughout the application.
- Centralize authentication, headers, and base URL in configuration.
- Handle 4xx/5xx codes with specific exceptions when it makes sense.

### 6. Controllers
- Controllers must be thin.
- Controllers only receive the request, validate input, and delegate.
- Business logic must live in handlers/services.
- Use clear request/response DTOs.
- Never return stack traces or internal messages to the consumer.

### 7. Error handling
- Create a global exception handler.
- Standardize the error payload.
- Messages must be useful and safe.
- Distinguish between validation errors, external integration errors, and internal errors.

### 8. Validation
- Validate inputs at the boundary.
- Use Bean Validation when it helps.
- Validate date parameters, pagination, and filters.
- Do not rely on the ERP to do all validation for us.

### 9. Modeling
- Internal models must reflect the report domain, not just the raw ERP format.
- Avoid leaking external DTOs directly into the public API.
- Use `data class` wherever it makes sense.
- Prefer explicit nullability over magic values.

### 10. Kotlin code style
- Prefer immutability.
- Use `when`, `let`, `run`, `takeIf`, etc. only when they improve clarity.
- Avoid excessive chaining.
- Avoid large functions.
- Names must be descriptive and direct.
- Avoid generic suffixes like `Util`, `Helper`, `Manager` without a real need.

### 11. Testing
Initial priorities:
- unit tests for handlers
- tests for critical mappers
- tests for client/integration when there is relevant logic

Do not build an excessive test suite from the start.
Focus on the main flows and the most important error cases.

### 12. Observability
Add only the basics:
- useful logs
- health check
- readiness/liveness if needed

Avoid complex instrumentation at this stage.

### 13. Security
If authentication is not yet defined:
- do not invent a complex solution
- prepare the application to receive auth in the future
- protect secrets via external configuration
- never hardcode tokens, passwords, or sensitive URLs

### 14. What to avoid
- databases
- distributed cache
- async events
- unnecessary reflection
- overly generic abstractions
- multiple modules
- heavy domain layer without need

## Decision criteria

When in doubt between two approaches, choose the one that:
1. reduces complexity
2. improves clarity
3. keeps ERP integration isolated
4. facilitates incremental evolution
