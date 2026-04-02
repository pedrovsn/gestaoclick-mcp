# TASK-001 - Bootstrap Spring Boot Project

## Task ID
`TASK-001`

## Title
Create the initial Spring Boot project with Kotlin and Gradle

## Goal
Create the initial project structure for the backend service using Kotlin, Gradle Kotlin DSL, and Spring Boot.

This task establishes the technical foundation of the repository so the next tasks can focus on integration and business behavior instead of project setup.

## Context
Before implementing ERP integrations, we need a clean and minimal Spring Boot project structure.

This is a small service, so the generated project must stay simple and ready for incremental evolution.

## Requirements
- Create the initial Spring Boot project inside `backend/`
- Use Kotlin
- Use Gradle with Kotlin DSL (`build.gradle.kts`)
- Use Java 25
- Use Spring Boot `4.0.5`
- Create the main application class
- Create a minimal package structure aligned with the project rules
- Add a basic `application.yml`
- Add a basic health endpoint capability through Spring Boot Actuator
- Prepare the project so future tasks can add ERP integrations without restructuring the whole project

## Inputs
Project setup inputs:
- Java version: `25`
- Build tool: `Gradle`
- Gradle DSL: `Kotlin`
- Spring Boot version: `4.0.5`
- Language: `Kotlin`

## Outputs
The task must produce:
- a runnable Spring Boot project in `backend/`
- `build.gradle.kts`
- `settings.gradle.kts`
- Gradle wrapper files if the task includes wrapper generation
- source folders for main and test code
- main application entrypoint
- base `application.yml`

## API / External Dependency
Not applicable for this task.

## Response Definition
Not applicable for this task.

## Rules
- Keep the generated project minimal
- Do not add database dependencies
- Do not add JPA/Hibernate/Flyway/Liquibase
- Do not add unnecessary starters
- Prefer only the dependencies needed for:
  - web
  - validation
  - actuator
  - Kotlin support
  - testing
- Use idiomatic Gradle Kotlin DSL
- The backend must remain ready for the later ERP integration tasks
- Do not implement ERP logic in this task

## Acceptance Criteria
- A Spring Boot project exists under `backend/`
- The project uses Kotlin
- The project uses Gradle Kotlin DSL
- The project is configured for Java 25
- The project uses Spring Boot `4.0.5`
- The application has a main entrypoint class
- The project contains a minimal `application.yml`
- The project can be used as the base for the next tasks without major restructuring
- No database-related dependency is introduced

## Out of Scope
- ERP client implementation
- Business endpoints
- ERP DTOs
- Railway deployment setup
- MCP-specific endpoints

## Notes

### Suggested initial structure

```text
backend/
  build.gradle.kts
  settings.gradle.kts
  src/
    main/
      kotlin/
      resources/
    test/
      kotlin/
```

### Suggested minimal dependencies
- Spring Boot Starter Web
- Spring Boot Starter Validation
- Spring Boot Starter Actuator
- Jackson Kotlin module
- Kotlin reflect
- Spring Boot Starter Test

### Suggested package structure
```text
backend/src/main/kotlin/.../
  api/
  application/
    command/
    query/
  integration/
    erp/
  shared/
    config/
    exception/
```
