# CCMS Hostinger VPS Deployment Guide

## Architecture

```
Internet ──► Hostinger VPS
                 │
          ┌──────┴──────┐
          │  nginx:80   │  Reverse proxy (HTTP-only, no SSL)
          └──────┬──────┘
                 │ http://ccms_ui:8080/CCMS/
          ┌──────┴──────┐
          │  ccms_ui    │  Tomcat 7 + Spring MVC + AngularJS
          │  :8080      │
          └──────┬──────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
┌───┴───┐  ┌────┴────┐  ┌───┴────┐
│ server│  │ mongodb │  │ mysql  │
│:8102  │  │:27017   │  │:3306   │
│:9100  │  │         │  │        │
└───────┘  └─────────┘  └────────┘
```

| Component | Internal Port | Public | Purpose |
|---|---|---|---|
| nginx | 80 | ✅ Port 80 | Reverse proxy to UI |
| ccms_ui | 8080 | ❌ Internal | Tomcat web app |
| server | 8102, 9100 | ❌ 8102 internal, ✅ 9100 public | Spring Boot REST + Netty TCP |
| mongodb | 27017 | ❌ Internal | Document database |
| mysql | 3306 | ❌ Internal | Relational database |

## Deployment Workflow

```
Git push ──► GitHub ──► Hostinger pulls from Git repo
                           │
                    ┌──────┴──────┐
                    │ Build images│  (docker compose build)
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │  Deploy     │  (docker compose up -d)
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │  Running    │
                    │  Stack      │
                    └─────────────┘
```

## Files

### Core Configuration Files

| File | Purpose |
|---|---|
| `docker-compose.yml` | Service definitions, volumes, healthchecks |
| `nginx/Dockerfile` | nginx image with certbot + wget pre-installed |
| `nginx/nginx.conf.template` | nginx reverse proxy config template |
| `nginx/docker-entrypoint.sh` | Renders nginx config via envsubst |
| `SERVER/ccms/Dockerfile` | Spring Boot server image |
| `SERVER/ccms/docker-entrypoint.sh` | Converts env vars to JVM -D properties |
| `CCMS_UI/STARTUP/ccms_ui/Dockerfile` | Tomcat UI image |
| `CCMS_UI/STARTUP/ccms_ui/docker-entrypoint.sh` | Converts env vars to JVM -D properties |
| `db/seeds/Dockerfile` | Database seed image |
| `db/seeds/seed.sh` | One-time database seeding script |
| `.env.example` | Reference template for all env vars |

### Data Files

| Path | Purpose | Persistence |
|---|---|---|
| `db/seeds/mongo.archive` | MongoDB seed snapshot (Git-tracked, baked into seed image) | Read-only |
| `db/seeds/mysql/` | MySQL schema (Git-tracked, baked into seed image) | Read-only |

### Docker Volumes (Survive container recreation)

| Volume | Mount | Purpose |
|---|---|---|
| `mongodb_data` | `/data/db` | MongoDB data files |
| `mysql_data` | `/var/lib/mysql` | MySQL data files |
| `historical_data` | `/home/data/dontdelete` | Historical CSV data |

## Environment Variables

All environment variables are set in Hostinger's Environment Variables section. **No .env file** exists on the server.

### Required

| Variable | Description |
|---|---|
| `MYSQL_ROOT_PASSWORD` | MySQL root password |

### Optional (have safe defaults)

| Variable | Default | Description |
|---|---|---|
| `MYSQL_DATABASE` | `employee_db` | MySQL database name |
| `MONGODB_DATABASE` | `ccms` | MongoDB database name |
| `MONGODB_HOST` | `mongodb` | MongoDB service hostname |
| `MONGODB_PORT` | `27017` | MongoDB port |
| `MONGODB_USERNAME` | (empty) | MongoDB auth user |
| `MONGODB_PASSWORD` | (empty) | MongoDB auth password |
| `SERVER_PORT` | `8102` | Spring Boot HTTP port |
| `SERVER_CONTEXT_PATH` | `/user` | Spring Boot context path |
| `NETTY_PORT` | `9100` | Netty TCP port for DCUs |
| `TOMCAT_PORT` | `8080` | Tomcat port |
| `CCMS_SERVER_HOST` | `server` | SERVER hostname (Docker DNS) |
| `BACKEND_HTTP_PORT` | `8102` | SERVER HTTP port (UI → SERVER) |
| `BACKEND_HTTP_CONTEXT` | `/user` | SERVER context path |
| `SMOKE_ADMIN_EMAIL` | `admin@example.com` | Test admin login |
| `SMOKE_ADMIN_PASSWORD` | (match USERS_JSON) | Test admin password |
| `USERS_JSON` | `[]` | JSON array of users |
| `PUBLIC_DOMAIN` | `ccms.example.com` | nginx server_name |
| `SERVER_LOG_DIR` | `/home/CCMS/roadmap/logs` | SERVER log directory |
| `SEED_DATA` | `true` | Enable/disable database seeding |

### USERS_JSON Format

```json
[{"email":"admin@example.com","password":"CHANGE_ME","role":"SUPER ADMIN","first_name":"Admin","last_name":"User","all_privileges":true}]
```

**Rules:**
- Do NOT wrap in single or double quotes
- `SMOKE_ADMIN_PASSWORD` must match the password in USERS_JSON
- Existing users are never overwritten (skip-if-exists)

## Deployment Steps (YAML-Only Paste)

The stack is designed for Hostinger's YAML editor — no Git repository URL required.

### 1. Hostinger VPS

1. Create a Hostinger VPS
2. Open ports in firewall:
   - **80** (TCP) — HTTP web dashboard
   - **9100** (TCP) — DCU device connections
   - **22** (TCP) — SSH (optional, for admin access)
3. Close all other ports (3306, 27017, 8080, 8102 are internal only)

### 2. Hostinger Docker Compose Deployment

1. Create a new Docker Compose deployment in Hostinger
2. Paste the **entire** `docker-compose.yml` file into the YAML editor
3. In the Environment Variables section, paste all required variables:
   - `MYSQL_ROOT_PASSWORD` (required)
   - `USERS_JSON` (recommended — without quotes)
4. Click Deploy

Hostinger will:
- Pull all pre-built images from GHCR
- Run `docker compose up -d`

### 3. Verify Deployment

Check that all containers start successfully:

```
http://<VPS-IP>/
```

This should load the CCMS login page through nginx.

### 4. Verify Deployment

Check that all containers start successfully:

```
http://<VPS-IP>/
```

This should load the CCMS login page through nginx.

## Docker Images

### Pre-built Images (GHCR)

The CI pipeline (`.github/workflows/build-images.yml`) builds and pushes these images on every push to `main`:

| Image | Source |
|---|---|
| `ghcr.io/crossfields-synergy-pvt-ltd/ccms-server:latest` | `SERVER/ccms/Dockerfile` |
| `ghcr.io/crossfields-synergy-pvt-ltd/ccms-ui:latest` | `CCMS_UI/STARTUP/ccms_ui/Dockerfile` |
| `ghcr.io/crossfields-synergy-pvt-ltd/ccms-nginx:latest` | `nginx/Dockerfile` |
| `ghcr.io/crossfields-synergy-pvt-ltd/ccms-seed:latest` | `db/seeds/Dockerfile` |

For local changes, `docker compose build` uses the `build: context:` directives.

## Service Dependencies

```
mongodb (healthy) ──→ seed (completed_successfully) ──→ server (healthy)
     ↓                            ↓                         ↓
mysql (healthy) ────→ seed (completed_successfully) ──→ ccms_ui (healthy)
                                                            ↓
                                                         nginx (healthy)
```

**Seed:** Runs once (`restart: "no"`), exits 0 after seeding. On subsequent starts it skips if `SEED_DATA=true` (the data volumes persist).

## Healthchecks

All long-running services have healthchecks:

| Service | Healthcheck | Start Period |
|---|---|---|
| mongodb | `mongo --eval "db.adminCommand('ping').ok"` | — |
| mysql | `mysqladmin ping -uroot -p${MYSQL_ROOT_PASSWORD}` | — |
| server | `wget --spider http://localhost:8102/user/push/hafe_open_connections` | 30s |
| ccms_ui | `wget -qO- http://localhost:8080/CCMS/` | 60s |
| nginx | `wget --spider http://localhost:80/CCMS/` | — |

## Adding SSL Later (When Domain Is Available)

1. Add an A record: `ccms.yourdomain.com` → VPS IP
2. Add `443:443` to nginx's port mapping in `docker-compose.yml`
3. Set `PUBLIC_DOMAIN=ccms.yourdomain.com` in Hostinger env vars
4. Run: `docker compose exec nginx certbot --nginx -d ccms.yourdomain.com`

The nginx Dockerfile already has certbot pre-installed. The nginx template has the `/.well-known/acme-challenge/` location block ready.

## Data Persistence

| Data | Storage | Survives |
|---|---|---|
| MongoDB | Named volume `mongodb_data` | `docker compose down`, container restart |
| MySQL | Named volume `mysql_data` | `docker compose down`, container restart |
| Let's Encrypt certs | Named volume (when SSL is active) | `docker compose down`, container restart |
| Historical CSVs | Named volume `historical_data` | Container restart |

To wipe everything and re-seed:
```bash
docker compose down -v
docker compose up -d
```

## Maintenance Commands

### View Logs
```bash
docker compose logs -f nginx
docker compose logs -f ccms_ui
docker compose logs -f server
```

### Backup
```bash
bash scripts/backup.sh
```

### Restore
```bash
bash scripts/restore.sh backups/<timestamp>
```

### API Smoke Test
```bash
bash scripts/api-smoke-test.sh
```

### Update Images
```bash
docker compose pull
docker compose up -d
```

## Rules

1. **No secrets in Git** — Secrets go in Hostinger's Environment Variables, never in `.env` or any committed file.
2. **No `.env` file on server** — All configuration comes from Hostinger environment variables.
3. **USERS_JSON without quotes** — Do not wrap JSON values in single or double quotes.
4. **SMOKE_ADMIN_PASSWORD must match USERS_JSON** — The smoke test login uses this password.
5. **Only expose ports 80 and 9100 publicly** — All other ports are internal only.
6. **Data survives container restarts** via named Docker volumes.
7. **SSL is optional** — The stack works without a domain. SSL can be added later.
