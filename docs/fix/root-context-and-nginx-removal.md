# Root Context Path & Nginx Removal

Changes made to serve the CCMS UI at root `/` instead of `/CCMS/` and remove the inactive `cspl-nginx` container.

## Why

- The `/CCMS/` context path required a redirect rule in NPM (`/` → `/CCMS/`)
- Without the redirect, users saw the default Tomcat page instead of the app
- The `cspl-nginx` container was exited/inactive — dead weight in the stack
- NPM (Nginx Proxy Manager) handles all proxy duties externally

## What Changed

### 1. Context Path: `/CCMS/` → `/` (root)

| File | Change |
|---|---|
| `CCMS_UI/STARTUP/ccms_ui/pom.xml` | `artifactId`: `CCMS` → `ROOT`, `name`: `CCMS` → `ROOT`, `context.path`: `CCMS` → `ROOT` |
| `CCMS_UI/STARTUP/ccms_ui/Dockerfile` | `CCMS.war` → `ROOT.war`, added `RUN rm -rf` to remove default Tomcat ROOT webapp |
| 16 factory JS files | `var serviceBase = '/CCMS'` → `var serviceBase = ''` |
| 11 test files (84 occurrences) | All `/CCMS/` prefixed mock URLs updated to `/` |
| `docker-compose.yml` | Healthcheck: `http://localhost:8080/CCMS/` → `http://localhost:8080/` |
| `scripts/api-smoke-test.sh` | Default base URL: `http://localhost:8080/CCMS` → `http://localhost:8080` |
| `.env.example` | `APP_CONTEXT=/CCMS` → `APP_CONTEXT=/` |

### 2. Nginx Removal

| File | Change |
|---|---|
| `nginx/` directory | Deleted `Dockerfile`, `nginx.conf.template`, `docker-entrypoint.sh` |
| `docker-compose.yml` | Removed entire `nginx:` service block |
| `.github/workflows/build-images.yml` | Removed nginx from build matrix |
| `.env.example` | Removed `PUBLIC_DOMAIN` variable |
| `AGENTS.md` | Updated all nginx references |

### 3. Local Build Support

| File | Change |
|---|---|
| `docker-compose.yml` | Added `build:` directives to `seed`, `server`, `ccms_ui` services |

## URLs Before & After

| URL | Before | After |
|---|---|---|
| App root | `apgp.crossfields.in/CCMS/` | `apgp.crossfields.in/` |
| Public monitor | `apgp.crossfields.in/CCMS/#/public_monitor` | `apgp.crossfields.in/#/public_monitor` |
| Login | `apgp.crossfields.in/CCMS/#/login` | `apgp.crossfields.in/#/login` |
| API calls | `apgp.crossfields.in/CCMS/dashboard/count` | `apgp.crossfields.in/dashboard/count` |

## How It Works Now

1. User visits `apgp.crossfields.in/`
2. NPM forwards to `cspl-ccms-ui:8080/` (no redirect needed)
3. Tomcat serves `ROOT.war` at root context
4. AngularJS `$urlRouterProvider.otherwise("/public_monitor")` catches root
5. Public monitor page loads (no auth required)
6. Lock icon in top-right navigates to login page

## Manual Step Required

After deploying this change, update your NPM proxy host:

1. Access NPM admin UI
2. Edit the proxy host for `apgp.crossfields.in`
3. **Remove** the redirect rule: `/` → `/CCMS/` (301)
4. Save

## Test Results

- 66/66 AngularJS tests: PASS
- Maven tests run inside Docker during CI/CD (not locally)

## Migration

No data migration required. This is a pure URL/deployment change.

- MongoDB data: untouched
- MySQL data: untouched
- Historical CSV files: untouched

## Rollback

To revert, restore the previous values:
- `pom.xml`: artifactId/name back to `CCMS`, context.path back to `CCMS`
- `Dockerfile`: `ROOT.war` back to `CCMS.war`, remove `RUN rm -rf` line
- Factory files: `serviceBase = ''` back to `serviceBase = '/CCMS'`
- Re-add nginx service to `docker-compose.yml` and restore `nginx/` directory
- Remove `build:` directives from docker-compose.yml
