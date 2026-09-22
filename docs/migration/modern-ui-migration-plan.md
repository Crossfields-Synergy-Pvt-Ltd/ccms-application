# CCMS UI Modernization Plan

## Purpose

Modernize the existing CCMS UI application from a monolithic AngularJS/Spring MVC WAR into a maintainable modern application stack, while preserving existing user workflows and integrations.

This plan covers the CCMS UI application only.

## Scope boundaries

The following are outside this migration:

- `SERVER/ccms`
- Netty packet reception and DCU protocol parsing
- DCU data ingestion
- Docker and deployment infrastructure
- Nginx/Nginx Proxy Manager
- Database schema and seed-data changes
- Changes to the public deployment topology

The existing SERVER application and its DCU/Netty receiver remain unchanged. The modernized UI initially continues using the existing UI API contracts and SERVER integration endpoints.

## Current application state

The current CCMS UI is a monolithic Java WAR containing:

- AngularJS 1.x frontend
- Spring MVC REST backend
- MySQL and MongoDB access
- CSV/history file processing
- Calls to the existing SERVER application for device commands and configuration synchronization

Current repository indicators:

- Approximately 87 UI Java files
- Approximately 87 frontend JavaScript files
- Approximately 38 HTML templates
- AngularJS 1.4/1.5 with global controllers and factories
- Manual JavaScript and vendor-library loading from `index.html`
- Spring MVC 4.2.8 and XML-based configuration
- Hibernate 4.3 and legacy persistence dependencies
- Java 8 target and Tomcat 7-era packaging
- Custom Base64/localStorage authentication
- Limited JavaScript and Java test coverage
- Known functional issues documented under `docs/bugs/`

## Target architecture

```text
Browser
  |
  v
Modern React/TypeScript SPA
  |
  v
UI API layer
  |
  +--> MySQL
  +--> MongoDB
  +--> CSV/history storage
  +--> Existing SERVER REST API
              |
              v
        Existing DCU/Netty receiver
```

## Recommended technology stack

### Frontend

- React
- TypeScript
- Vite
- React Router
- TanStack Query for server state and caching
- React Hook Form and Zod for forms and validation
- One consistent component library, such as Material UI
- Highcharts and Google Maps retained behind application adapters
- Vitest and Testing Library for unit/component tests
- Playwright for browser-level tests

### UI backend

- Spring Boot
- Java 17 or later
- REST controllers with explicit DTOs
- Spring Data MongoDB and JPA repositories where appropriate
- Centralized validation and exception handling
- OpenAPI documentation
- Secure session or token-based authentication

The backend modernization applies only to the UI application. It does not replace or modify `SERVER/ccms`.

## Migration principles

1. Preserve existing user-facing workflows.
2. Freeze and test current API contracts before replacing implementations.
3. Migrate feature by feature rather than rewriting the entire application at once.
4. Keep the existing SERVER/DCU integration behind an explicit API boundary.
5. Do not change the DCU receiver or Netty protocol as part of this UI migration.
6. Add tests before removing legacy implementations.
7. Remove AngularJS only after equivalent modern routes have been validated.

## Phased plan

### Phase 0 — Baseline and contract inventory

Document and freeze:

- Routes defined in `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/app.js`
- Every frontend factory endpoint
- Request parameters and response shapes
- Authentication and permission behavior
- MongoDB collections and MySQL tables used by the UI backend
- CSV formats and historical-data paths
- Configuration, schedule, and light-control workflows
- Acceptance criteria from `docs/bugs/`

Create API fixtures and contract tests before replacing implementations.

### Phase 1 — Establish the modern frontend platform

Create a feature-oriented TypeScript application structure:

```text
src/
  app/
  auth/
  layouts/
  routing/
  shared/
  api/
  features/
    dashboard/
    monitoring/
    dcu/
    node/
    schedules/
    events/
    history/
    operational-hours/
    users/
    meters/
    filters/
    switchpoint/
```

Replace AngularJS controllers and factories with React components, typed API clients, feature hooks, and route-level layouts.

### Phase 2 — Build shared application foundations

Implement before migrating individual pages:

- Authentication provider and protected routes
- Permission and role model
- Shared API client
- Common request headers
- Centralized error handling
- Request cancellation
- Query caching and invalidation
- Toast and notification handling
- Confirmation dialogs
- Form validation
- Pagination and sorting
- Date/time utilities
- CSV download handling
- Loading, empty, and error states
- Accessibility and responsive-layout conventions

Authentication should no longer send credentials in URL query parameters or rely on unprotected custom authorization values in localStorage.

### Phase 3 — Migrate lower-risk CRUD features

Migrate first:

1. User management
2. Node management
3. Meter management
4. Filter management
5. Default configuration

Each feature should include typed models, list/detail/form components, validation, error handling, loading states, confirmed deletes, and automated tests.

The AngularJS pages may remain temporarily available through a compatibility boundary during this phase.

### Phase 4 — Migrate DCU configuration and schedules

Migrate:

- DCU list and edit screens
- Configuration forms
- Default configuration
- Node configuration
- Schedule creation and editing
- Sync to one DCU
- Sync to all DCUs
- Sync status and error display

The modern UI should initially call the existing UI backend endpoints. It should not modify the DCU receiver or Netty protocol.

The UI should represent explicit operation states:

- Queued
- Sending
- Accepted
- Failed
- Timed out
- Unknown result

### Phase 5 — Migrate monitoring and control

Migrate:

- Dashboard counts
- Map view
- Monitor and Control
- Switchpoint
- Light ON/OFF operations
- DCU search and configuration search
- Instant meter data

Use TanStack Query for polling, cache invalidation, and consistent refresh behavior. Preserve the existing data model initially. WebSockets or other real-time transport should be considered separately and are not required for this migration.

### Phase 6 — Migrate historical and reporting features

Migrate:

- Events
- History
- Operational Hours
- Light Status

Centralize date ranges, time-zone conversion, CSV exports, pagination, sorting, DCU selection, and hierarchy filters.

CSV responses must be treated as file downloads and must not overwrite table state.

### Phase 7 — Modernize the UI backend

After the modern frontend is operating against stable APIs:

- Replace XML Spring configuration with Spring Boot configuration
- Upgrade the UI backend from the Java 8/Tomcat 7-era stack
- Separate controllers, services, repositories, integration clients, and DTOs
- Stop exposing persistence objects directly as API responses
- Add Bean Validation
- Add centralized exception handling
- Remove duplicate DAO implementations
- Standardize API response and error formats
- Move SERVER host/port values into typed configuration
- Remove hardcoded URLs and ports
- Add OpenAPI documentation
- Add integration tests for MongoDB, MySQL, CSV processing, and existing SERVER calls

This phase changes only the UI application backend. The existing SERVER/Netty application remains unchanged.

### Phase 8 — Remove the legacy frontend

After feature parity and validation:

- Remove AngularJS dependencies
- Remove legacy controllers and factories
- Remove old HTML templates
- Remove obsolete vendor libraries
- Remove the AngularJS Karma setup
- Remove duplicate and unused CSS
- Remove compatibility routes
- Make the modern frontend the only UI entry point

## Recommended migration order

```text
API contracts and tests
        |
        v
Frontend shell, authentication, and shared services
        |
        v
Lower-risk CRUD features
        |
        v
DCU configuration and schedules
        |
        v
Monitoring and control
        |
        v
History, events, and reporting
        |
        v
UI backend modernization
        |
        v
AngularJS removal
```

## Testing strategy

Add testing at four levels:

1. API contract tests for existing endpoint compatibility.
2. Unit tests for validation, formatting, permissions, and data transformations.
3. Component tests for forms, tables, loading states, errors, and confirmations.
4. End-to-end tests for login, dashboard access, DCU configuration, schedule sync, light control, history, and exports.

Existing tests should be retained as regression references until their modern equivalents are passing.

## Completion criteria

The migration is complete when:

- Every existing UI route has a modern equivalent.
- Existing API workflows are covered by typed clients and contract tests.
- Login and permissions work without credentials in URLs.
- DCU configuration and light-control workflows retain their current integration behavior.
- Loading, error, empty, and confirmation states are consistently handled.
- Historical CSV downloads and date filters work correctly.
- Frontend tests cover every migrated feature.
- UI backend integration tests cover all UI-side data sources.
- AngularJS and obsolete vendor code are removed.
- The existing SERVER/DCU receiver remains operational and unchanged.

## Explicitly excluded from this plan

This migration does not include changes to:

- `SERVER/ccms`
- `ServerHandler`
- Netty handshake, event, meter, or configuration parsers
- DCU packet formats
- DCU data ingestion
- Docker Compose
- Nginx or Nginx Proxy Manager
- MySQL schema
- MongoDB schema
- Seed snapshots
