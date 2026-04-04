# TASK-004 - OpenAPI documentation with Swagger UI

## Task ID
`TASK-004`

## Title
Add SpringDoc OpenAPI to expose interactive API documentation

## Goal
Add `springdoc-openapi` to the backend so the project has auto-generated OpenAPI documentation and a Swagger UI available for manual testing.

This makes it easy to explore and test endpoints without external tools like Postman, and serves as living documentation for OpenClaw integration consumers.

## Context
The backend already exposes `GET /api/sales`. As more endpoints are added, having an interactive UI to test them becomes important for both development and integration work.

SpringDoc integrates with Spring Boot and generates OpenAPI 3 specs automatically from existing controllers, with no need to annotate every endpoint manually.

## Requirements
- Add the `springdoc-openapi-starter-webmvc-ui` dependency.
- Swagger UI must be accessible at `/swagger-ui.html` or `/swagger-ui/index.html`.
- The OpenAPI JSON spec must be accessible at `/v3/api-docs`.
- The existing `GET /api/sales` endpoint must appear in the UI with its query parameters documented.
- Basic API metadata must be configured (title, description, version).

## Inputs
No new endpoint inputs. This task only adds documentation infrastructure.

## Outputs
- Swagger UI available at `/swagger-ui.html`
- OpenAPI spec available at `/v3/api-docs`

## API / External Dependency
None. SpringDoc introspects the Spring MVC controllers at startup.

## Response Definition
Not applicable.

## Rules
- Do not add annotations to every field/method unless they improve clarity significantly.
- Do not disable or override the actuator endpoints.
- Do not expose the Swagger UI in a way that requires authentication to access during development.
- Keep the OpenAPI config minimal — title, description, and version are enough.

## Acceptance Criteria
- `springdoc-openapi-starter-webmvc-ui` is present in `build.gradle.kts`.
- The application starts without errors.
- `/swagger-ui.html` redirects to the Swagger UI page.
- `/v3/api-docs` returns a valid OpenAPI 3 JSON document.
- `GET /api/sales` is visible in the Swagger UI with its query parameters listed.
- Basic API info (title, version) is shown in the Swagger UI header.

## Out of Scope
- Annotating every DTO field with `@Schema`.
- Adding request/response examples manually.
- Securing the Swagger UI endpoint.
- Generating client SDKs from the spec.

## Notes

### Suggested dependency (check latest version on Maven Central)
```kotlin
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x.x")
```

### Suggested OpenAPI config bean
```kotlin
@Bean
fun openApi(): OpenAPI = OpenAPI()
    .info(Info()
        .title("GestaoClick MCP API")
        .description("Backend service for OpenClaw MCP integration")
        .version("0.1.0"))
```

### Suggested `application.yml` properties
```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```
