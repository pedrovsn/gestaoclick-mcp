# TASK-005 - GitHub Actions CI/CD for Railway deployment

## Task ID
`TASK-005`

## Title
Add GitHub Actions workflows for CI on feature branches/PRs and CD to Railway on merge to master

## Goal
Set up two separate GitHub Actions workflows:

1. **CI workflow** — runs on every push to a feature branch and on every pull request. Builds the project and blocks merging if the build fails.
2. **Deploy workflow** — runs only when a commit is merged to `master`. Triggers a Railway deployment after a successful build.

This enforces that nothing broken ever reaches `master` and that every merge to `master` is automatically deployed.

## Context
The backend is a Kotlin + Spring Boot application built with Gradle, deployed to Railway.

The two-workflow approach keeps CI and CD concerns separate:
- Feature branches get fast feedback without triggering deployments.
- `master` is the production gate — merges to it must pass the build and auto-deploy.

The `infrastructure/` folder is the right place for Railway-specific configuration files (e.g. `railway.toml`). GitHub Actions workflows belong in `.github/workflows/`.

## Requirements
- Create `.github/workflows/ci.yml` — triggers on push to any branch except `master`, and on pull requests targeting `master`.
- Create `.github/workflows/deploy.yml` — triggers on push to `master` (i.e. after a merge).
- Both workflows must build the backend using `./gradlew build` from the `backend/` directory.
- The deploy workflow must only trigger the Railway deployment if the build step passes.
- Secrets must never be hardcoded — use GitHub Actions secrets.
- Branch protection on `master` must be documented so that PRs cannot be merged when the CI check is failing.

## Inputs

### Required GitHub Actions secrets
- `RAILWAY_TOKEN` — Railway API token, used by the deploy workflow only

### Environment variables passed to the build
None required at build time. Railway injects runtime env vars directly into the deployed service.

## Outputs
- `.github/workflows/ci.yml` — CI workflow (build on feature branches and PRs)
- `.github/workflows/deploy.yml` — deploy workflow (build + Railway deploy on master)
- `infrastructure/railway.toml` (if needed) — Railway project configuration

## API / External Dependency

### Railway deployment options
Two approaches are supported — choose the simpler one:

**Option A — Railway deploy webhook (recommended for simplicity)**
Railway exposes a deploy webhook URL per service. Trigger it with a simple HTTP POST:
```bash
curl -X POST "${{ secrets.RAILWAY_DEPLOY_WEBHOOK_URL }}"
```
Requires `RAILWAY_DEPLOY_WEBHOOK_URL` secret.

**Option B — Railway CLI**
Install the CLI and run:
```bash
npm install -g @railway/cli
railway up --service <service-name> --detach
```
Requires `RAILWAY_TOKEN` secret.

## Response Definition
Not applicable.

## Rules
- Do not hardcode any tokens, URLs, or credentials.
- The deploy workflow must fail fast — if the Gradle build fails, Railway must not be triggered.
- Keep both workflows simple and linear.
- The working directory for all Gradle commands must be `backend/`.
- Do not introduce Docker builds unless Railway requires it.
- Do not add caching unless trivially simple (e.g. Gradle wrapper cache via `actions/cache`).
- The CI workflow must NOT trigger a Railway deployment under any circumstance.
- The deploy workflow must NOT trigger on pull requests — only on direct push to `master`.

## Acceptance Criteria
- `.github/workflows/ci.yml` exists and triggers on:
  - push to any branch other than `master`
  - pull requests targeting `master`
- `.github/workflows/deploy.yml` exists and triggers on push to `master` only.
- Both workflows run the Gradle build from the `backend/` directory.
- The deploy workflow only calls Railway after a successful build.
- No secrets are hardcoded in any file.
- Both workflows use `ubuntu-latest` and set up Java 25 with the `temurin` distribution.
- Branch protection setup is documented in the Notes section (does not need to be automated).

## Out of Scope
- Automatically configuring branch protection rules via API.
- Running tests as a separate step (covered by `./gradlew build`).
- Building a Docker image manually.
- Multi-environment deployments (staging, production).
- Slack or email notifications.
- Separate lint or static analysis steps.

## Notes

### Suggested CI workflow (`.github/workflows/ci.yml`)
```yaml
name: CI

on:
  push:
    branches-ignore: [master]
  pull_request:
    branches: [master]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'

      - name: Make gradlew executable
        working-directory: backend
        run: chmod +x gradlew

      - name: Build
        working-directory: backend
        run: ./gradlew build
```

### Suggested deploy workflow (`.github/workflows/deploy.yml`)
```yaml
name: Deploy

on:
  push:
    branches: [master]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'

      - name: Make gradlew executable
        working-directory: backend
        run: chmod +x gradlew

      - name: Build
        working-directory: backend
        run: ./gradlew build

      - name: Deploy to Railway
        run: curl -X POST "${{ secrets.RAILWAY_DEPLOY_WEBHOOK_URL }}"
```

### Branch protection setup (manual, done once in GitHub)
To prevent merging when CI fails:
1. Go to **Settings → Branches → Add branch protection rule** for `master`.
2. Enable **Require status checks to pass before merging**.
3. Add the CI job name (e.g. `build`) as a required status check.
4. Enable **Require a pull request before merging** to prevent direct pushes.

### Railway token / webhook
- `RAILWAY_TOKEN`: generated from Railway dashboard under **Account Settings → Tokens**.
- `RAILWAY_DEPLOY_WEBHOOK_URL`: found in Railway under the service settings → **Deploy webhook**.

### Gradle wrapper permissions
The `chmod +x gradlew` step is required on Linux runners when the wrapper was committed from Windows (execute bit is not preserved).
