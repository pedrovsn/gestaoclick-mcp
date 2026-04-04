# AGENTS.md

This repository contains two main areas:

- `backend`: Kotlin + Spring Boot API
- `infrastructure`: infrastructure code for deployment

## Project goal

Build a simple service for use with OpenClaw/MCP that consumes data from an ERP API and exposes custom reports.

## Mandatory principles for all agents

1. **Simplicity first**
   - This service is simple.
   - Do not introduce unnecessary complexity.
   - Avoid generic abstractions "for the future".
   - Avoid sophisticated patterns if a direct flow solves the problem.

2. **No database**
   - Do not add PostgreSQL, MySQL, MongoDB, Redis, or any local/remote persistence for domain data.
   - The source of truth is the ERP API.
   - The service only queries, transforms, and exposes data.
   - Only use storage if there is a strictly operational need that has been explicitly approved.

3. **Architecture**
   - Use Kotlin + Spring Boot.
   - Follow a **simple CQRS approach**, clearly separating reads from writes.
   - Since the service will initially be mostly read-oriented, prioritize queries and read flows.
   - Commands should only exist when they make sense for orchestration, refresh, validation, or transformation.
   - Do not use event sourcing.
   - Do not use messaging.
   - Do not create microservices.
   - Do not prematurely split the project into modules.

4. **Repository structure**
   - `backend/`: Kotlin/Spring API code
   - `infrastructure/`: deployment and provisioning code
   - Do not move responsibilities between these two areas without a clear reason.

5. **ERP integration**
   - All external integration must be well isolated behind interfaces/gateways/clients.
   - ERP API models must not leak unnecessarily throughout the application.
   - Create DTOs/mappers when that improves clarity.
   - Handle timeouts, HTTP errors, and unexpected responses explicitly.

6. **Endpoints**
   - Our API endpoints may mirror or reorganize ERP endpoints, as long as that improves the consumption experience.
   - Prefer stable and clear contracts.
   - Do not expose unnecessary internal details.
   - Always favor report/query-oriented responses.

7. **Code quality**
   - Prefer idiomatic Kotlin.
   - Prefer small functions and clear names.
   - Avoid obvious comments.
   - Avoid duplication.
   - Fail with useful error messages.
   - Keep coupling low.

8. **Before implementing**
   - Always confirm whether the change belongs in `backend` or `infrastructure`.
   - Verify that the solution maintains the project's simplicity.
   - Verify that it does not introduce persistence without authorization.
   - Verify that the change actually needs to exist now.

9. **When proposing changes**
   - Briefly explain why.
   - List trade-offs when they exist.
   - Prefer small, incremental changes.

10. **Do not do without explicit alignment**
    - add a database
    - add queues/messaging
    - add distributed cache
    - create complex authentication
    - create sophisticated observability beyond the basics
    - create multiple Gradle modules
    - introduce full hexagonal architecture if the benefit is not clear

## Expected agent deliverables

When working in this repository, the agent must:
- preserve simplicity
- maintain clear separation between backend and infrastructure
- follow lightweight CQRS
- prioritize readability and maintainability
- avoid overengineering
