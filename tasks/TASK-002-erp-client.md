# TASK-002 - ERP Base Client

## Task ID
`TASK-002`

## Title
Create base ERP client using RestTemplate

## Goal
Create a reusable HTTP client for all ERP integrations using Spring's RestTemplate.

This client will centralize:
- base URL
- authentication headers
- common configuration

It will be used by all future ERP integrations.

## Context
All ERP requests require specific headers and configuration. This must not be duplicated across the codebase.

This task defines the foundation for all future integrations.

## Requirements
- Create a RestTemplate-based client
- Centralize base URL configuration
- Automatically attach required headers to every request
- Externalize configuration via `application.yml`
- Use environment variables for sensitive data
- Expose a reusable client/gateway

## Inputs
Configuration inputs:
- `ERP_BASE_URL`
- `ERP_ACCESS_TOKEN`
- `ERP_SECRET_ACCESS_TOKEN`
- `ERP_TIMEOUT_MS` (optional)

## Outputs
- A configured RestTemplate bean
- A base ERP client (e.g., `ErpClient` or `ErpGateway`)
- Ability to perform GET requests with headers automatically applied

## API / External Dependency
All ERP endpoints share the same headers:

- `Content-Type: application/json`
- `access-token: <ERP_ACCESS_TOKEN>`
- `secret-access-token: <ERP_SECRET_ACCESS_TOKEN>`

## Response Definition
Not applicable for this task (infrastructure-level setup).

## Rules
- Do not hardcode tokens
- Do not duplicate header logic across services
- Do not call RestTemplate directly outside the integration layer
- Configuration must come from `application.yml`

## Acceptance Criteria
- RestTemplate is configured as a Spring bean
- Headers are automatically included in every request
- Base URL is configurable via environment variables
- Tokens are not hardcoded
- A reusable client class exists for future integrations

## Out of Scope
- Specific ERP endpoints
- Business logic
- Mapping DTOs
