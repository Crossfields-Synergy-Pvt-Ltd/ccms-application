# Production Deployment Fixes

Changes made to enable Hostinger VPS deployment with Docker Compose (no `.env` file).

## Files Modified

### `docker-compose.yml`

| Change | Details |
|---|---|
| `env_file: - .env` removed | Removed from all 5 services: mysql, seed, server, ccms_ui, nginx |
| Named volumes added | `mongodb_data:/data/db` replaces `./data/mongodb:/data/db`, `mysql_data:/var/lib/mysql` replaces `./data/mysql:/var/lib/mysql` |
| Missing env vars added to `server` | `SERVER_PORT`, `SERVER_CONTEXT_PATH`, `NETTY_PORT` (were only in port mappings) |
| `ccms_ui` environment refactored | Replaced inline `CATALINA_OPTS` string with individual env vars. Entrypoint constructs `CATALINA_OPTS` from these. |
| Healthchecks added | `server` (HTTP endpoint), `ccms_ui` (Tomcat index), `nginx` (port 80) |
| `ccms_ui` ports | Changed to `ports: []` — no longer exposed directly (nginx proxies to it) |
| nginx | Changed to `image:` only (no `build:` section — for YAML-only pasting). Port 443 removed (HTTP-only). `LETSENCRYPT_LIVE_DIR` removed. |
| `depends_on` updated | `ccms_ui` now waits for `server: condition: service_healthy` |
| Default fallbacks added | All `${VARIABLE}` references now use `:-default` syntax where safe. Only `MYSQL_ROOT_PASSWORD` has no default. |

### `nginx/nginx.conf.template`

- Removed the HTTPS server block (port 443 with SSL)
- HTTP server block now proxies directly to `http://ccms_ui/CCMS/` instead of redirecting
- Added `location /.well-known/acme-challenge/` block for future certbot SSL enablement

### `nginx/docker-entrypoint.sh`

- Removed `LETSENCRYPT_LIVE_DIR` (no longer needed for HTTP-only)
- Simplified `envsubst` to only substitute `${PUBLIC_DOMAIN}`
- Creates `/var/www/html` for future ACME challenges
- No certbot logic (certbot binary remains in the Docker image for future use)

### `.env.example`

- Removed single quotes from `USERS_JSON` value (Docker Compose .env files do not strip quotes like bash does)
- `SMOKE_ADMIN_PASSWORD` now explicitly documented to match password in `USERS_JSON`
- Header updated to describe Hostinger deployment model
- Variables annotated with ✅ (has default, optional) or 🔴 (REQUIRED)
- Added `BACKEND_HTTP_PORT` and `BACKEND_HTTP_CONTEXT` documentation
- `LETSENCRYPT_LIVE_DIR` removed (no longer used in HTTP-only mode)

### `db/seeds/seed.sh`

- Added defensive quote-stripping for `USERS_JSON`: strips leading/trailing `'` or `"` if present
- This protects against Hostinger or Docker Compose passing JSON with surrounding quotes

### `.github/workflows/build-images.yml`

| Change | Details |
|---|---|
| GHCR owner updated | `askamoghb-rgb` → `crossfields-synergy-pvt-ltd` (matches new repo org) |
| Matrix name bug fixed | `name: ccms-nginx` → `name: nginx`, `name: ccms-seed` → `name: seed` — was producing double `ccms-ccms-` tag prefix |
| Action version updated | `docker/build-push-action@v5` → `@v6` (Node 24 compatibility — Node 20 is deprecated on GitHub runners) |

### `docker-compose.yml` (image tags)

All `image:` references updated from `ghcr.io/askamoghb-rgb/*` to `ghcr.io/crossfields-synergy-pvt-ltd/*` for all four custom images:

### `db/seeds/Dockerfile`

- Added `COPY mongo.archive /seeds/mongo.archive` and `COPY mysql/ /seeds/mysql/` — seed data is now baked into the image
- Removes dependency on `./db/seeds:/seeds:ro` bind mount

### `CCMS_UI/STARTUP/ccms_ui/Dockerfile`

- Added `RUN mkdir -p /home/data/dontdelete` — creates the historical CSV directory inside the image
- The `./data/dontdelete` bind mount was replaced with a named volume (`historical_data`)

### `nginx/Dockerfile`

- Added `wget` to `apk add` — nginx healthcheck was failing because `nginx:alpine` does not include `wget` by default. The healthcheck `CMD wget --spider ...` silently failed, causing nginx to never become healthy.
- Now: `RUN apk add --no-cache certbot certbot-nginx wget`

### `docker-compose.yml` (bind mounts → named volumes)

| Change | Details |
|---|---|
| `./db/seeds/mysql:/docker-entrypoint-initdb.d:ro` removed | MySQL schema is applied by the seed container instead |
| `./db/seeds:/seeds:ro` removed from seed | `mongo.archive` and `mysql/` are baked into the seed image |
| `./data/dontdelete:/home/data/dontdelete` → `historical_data:/home/data/dontdelete` | Named volume for historical CSV data — no host path needed |

The stack no longer requires **any** bind mount to function. All persistent data uses named Docker volumes.

## Volumes Summary

| Volume | Mount | Purpose |
|---|---|---|
| `mongodb_data` | `/data/db` | MongoDB data files |
| `mysql_data` | `/var/lib/mysql` | MySQL data files |
| `historical_data` | `/home/data/dontdelete` | Historical CSV data |

## How SSL Can Be Added Later

When a domain is obtained:

1. Add an A record: `ccms.yourdomain.com` → VPS IP
2. Add `443:443` to nginx's port mapping in `docker-compose.yml`
3. Set `PUBLIC_DOMAIN=ccms.yourdomain.com` in Hostinger's environment variables
4. Run: `docker compose exec nginx certbot --nginx -d ccms.yourdomain.com`

The nginx Dockerfile already has `certbot` installed. The `/.well-known/acme-challenge/` location block is already in the template.

## Variable Dependency Map

```
MYSQL_ROOT_PASSWORD ─┬── mysql (env, healthcheck)
                     ├── seed (MYSQL_ROOT_PASSWORD)
                     └── ccms_ui (MYSQL_PASSWORD)

MYSQL_DATABASE ───────┬── mysql (env)
                      ├── seed (MYSQL_DATABASE)
                      └── ccms_ui (MYSQL_DATABASE)

MONGODB_HOST ─────────┬── seed (MONGO_HOST)
                      ├── server (SPRING_DATA_MONGODB_HOST)
                      └── ccms_ui (MONGODB_HOST)

MONGODB_PORT ─────────┬── seed (MONGO_PORT)
                      ├── server (SPRING_DATA_MONGODB_PORT)
                      └── ccms_ui (MONGODB_PORT)

MONGODB_DATABASE ─────┬── server (SPRING_DATA_MONGODB_DATABASE)
                      └── ccms_ui (MONGODB_DATABASE)

MONGODB_USERNAME ─────┬── server (SPRING_DATA_MONGODB_USERNAME)
                      └── ccms_ui (MONGODB_USERNAME)

MONGODB_PASSWORD ─────┬── server (SPRING_DATA_MONGODB_PASSWORD)
                      └── ccms_ui (MONGODB_PASSWORD)

SERVER_PORT ──────────┬── server (env, ports, healthcheck)
                      └── ccms_ui (SERVER_PORT)

SERVER_CONTEXT_PATH ──┬── server (env)
                      └── ccms_ui (via entrypoint)

CCMS_SERVER_HOST ─────── ccms_ui (CCMS_SERVER_HOST)
BACKEND_HTTP_PORT ────── ccms_ui (BACKEND_HTTP_PORT)
BACKEND_HTTP_CONTEXT ─── ccms_ui (BACKEND_HTTP_CONTEXT)

PUBLIC_DOMAIN ────────── nginx (server_name)

USERS_JSON ───────────── seed (user sync), api-smoke-test.sh

SEED_DATA ────────────── seed
```

## Service Start Order

```
mongodb (healthy) ──→ seed (completed_successfully) ──→ server (healthy)
     ↓                            ↓                         ↓
mysql (healthy) ────→ seed (completed_successfully) ──→ ccms_ui (healthy)
                                                            ↓
                                                         nginx (healthy)
```
