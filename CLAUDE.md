# CLAUDE.md

## Project Context

You are working on a backend service written in **Kotlin + Spring Boot**.

This service is part of an MCP (Model Context Protocol) integration used by OpenClaw.

The goal of this service is to:

* Consume data from an external **ERP API**
* Transform and aggregate that data
* Expose **custom report endpoints**
* Serve as a backend for MCP tools used by OpenClaw

---

## Project Structure

This repository has **two main folders**:

* `backend/` → Kotlin + Spring Boot API
* `infrastructure/` → Deployment and environment configuration (Railway)

You must **always respect this separation**:

* Business logic → `backend`
* Deployment/config → `infrastructure`

---

## Task Ordering

Tasks must be implemented in order.

- TASK-001 is foundational and must be completed before TASK-002
- If a task depends on another, do not reimplement shared logic

Always reuse previously implemented components.

---

## Core Principles

### 1. Keep it Simple

This is a **small service**.

* Do NOT overengineer
* Do NOT introduce unnecessary abstractions
* Prefer straightforward implementations over generic frameworks
* Avoid “future-proofing” unless explicitly requested

---

### 2. No Database

This project **MUST NOT use a database**.

* Do NOT add PostgreSQL, MySQL, MongoDB, Redis, etc.
* Do NOT use JPA, Hibernate, or repositories
* Do NOT persist business data

The **ERP API is the source of truth**.

This service:

* fetches data
* transforms it
* returns it

Nothing more.

---

### 3. Architecture (Light CQRS)

Use a **simple CQRS approach**:

* `command` → operations that trigger actions (sync, orchestration, preparation)
* `query` → read-only operations (reports, aggregations)

Important:

* This is **organizational CQRS**, not distributed CQRS
* No event sourcing
* No message brokers
* No command bus frameworks

Direct calls between classes are preferred.

---

## Backend Guidelines (Kotlin + Spring)

### Structure (suggested)

```
backend/src/main/kotlin/.../

  api/
  application/
    command/
    query/
  integration/
    erp/
  mcp/
  shared/
```

Keep it **flat and readable**. Avoid deep hierarchies.

---

### Controllers

* Controllers must be **thin**
* Only:

  * validate input
  * call handlers/services
  * return response

Do NOT:

* embed business logic
* call ERP directly from controllers

---

### ERP Integration

All ERP communication must go through a **dedicated client/gateway**.

Rules:

* No direct HTTP calls scattered in the codebase
* Centralize:

  * base URL
  * headers/auth
  * timeouts
* Handle:

  * timeouts
  * 4xx/5xx errors
  * malformed responses

Map ERP responses into internal models when it improves clarity.

---

### Data Modeling

* Use `data class`
* Prefer immutability
* Avoid leaking ERP response models into API responses
* Build **report-oriented DTOs**, not raw data dumps

---

### Error Handling

* Use a global exception handler
* Return consistent error responses
* Do NOT expose internal stack traces
* Clearly distinguish:

  * validation errors
  * ERP integration errors
  * internal errors

---

### Validation

* Validate inputs at the API boundary
* Validate:

  * date ranges
  * required params
  * filters
* Do not rely on ERP validation only

---

### Kotlin Style

* Prefer idiomatic Kotlin
* Avoid unnecessary abstractions
* Use expressive names
* Keep functions small
* Avoid deeply nested chains
* Use `when`, `let`, etc. only when it improves readability

---

### Testing

Focus on:

* command handlers
* query handlers
* ERP mapping logic

Do NOT overbuild test suites initially.

---

## MCP / OpenClaw Integration

This service will expose **tool-like endpoints**.

Examples:

* sales summary
* top customers
* invoices
* custom aggregations

Responses must be:

* deterministic
* clean JSON
* easy for LLM consumption

Avoid:

* noisy payloads
* unnecessary fields
* inconsistent formats

---

## Infrastructure Guidelines (Railway)

* Keep it simple
* Use environment variables
* Do not hardcode secrets
* Prefer minimal setup

Expected variables:

* `ERP_BASE_URL`
* `ERP_API_KEY`
* `ERP_TIMEOUT_MS`
* `SERVER_PORT`

Optional:

* `JAVA_OPTS`

---

## What NOT to Do

Unless explicitly asked, DO NOT:

* Add a database
* Add caching layers
* Add message queues
* Introduce microservices
* Add complex security/auth
* Add heavy observability tooling
* Split into multiple Gradle modules
* Implement full hexagonal architecture

---

## Decision Making

When multiple approaches are possible, choose the one that:

1. Minimizes complexity
2. Maximizes readability
3. Keeps ERP integration isolated
4. Fits a small service context
5. Is easy to evolve incrementally

---

## How to Work

When implementing something:

1. Think first: is this really needed?
2. Keep changes small and focused
3. Explain trade-offs briefly
4. Avoid introducing new patterns unless justified
5. Respect all constraints above

---

## Expected Behavior from You (Agent)

You must:

* Follow all rules strictly
* Avoid overengineering
* Prefer clarity over cleverness
* Keep the codebase clean and predictable
* Respect the project boundaries

If something seems unclear or risky:
👉 Ask before implementing

---

## Summary

This is a **thin, clean, integration-focused backend**.

* No database
* ERP is the source
* CQRS is lightweight
* Kotlin is idiomatic
* Infrastructure is simple (Railway)

Your job is to **keep it that way**.
