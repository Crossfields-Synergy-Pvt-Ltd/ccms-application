# CCMS Hostinger VPS Deployment Guide

## Architecture

```
Internet
  │
  ├──► VPS port 80  ──► NPM (:80)  ──┐
  ├──► VPS port 443 ──► NPM (:443) ──┤
  └──► VPS port 9100 ──► cspl-server (Netty TCP for DCU devices)
                                      │
                          NPM Proxy Host
                          domain: apgp.crossfields.in
                          forward: cspl-ccms-ui:8080
                                      │
                                      ▼
                            cspl-ccms-ui (Tomcat :8080)
                              serves CCMS at /CCMS/
                                      │
                    ┌─────────────────┼─────────────────┐
                    │                 │                 │
              ┌─────┴─────┐   ┌──────┴──────┐   ┌─────┴─────┐
              │cspl-server│   │cspl-mongodb │   │cspl-mysql │
              │  :8102    │   │   :27017    │   │   :3306   │
              │  :9100    │   │             │   │           │
              └───────────┘   └─────────────┘   └───────────┘
```

> **Note:** `cspl-nginx` exists in docker-compose.yml but is **exited** (not running).
> NPM (Nginx Proxy Manager) is the sole active reverse proxy.

| Component | Container Name | Internal Port | Host-Published | Purpose |
|---|---|---|---|---|
| NPM | `npm_proxy_manager` | 80, 443 | 80, 443 | External reverse proxy (apgp.crossfields.in) |
| ccms_ui | `cspl-ccms-ui` | 8080 | No (internal) | Tomcat/Spring/AngularJS app server |
| server | `cspl-server` | 8102 | Yes (debugging) | Spring Boot REST API |
| server (Netty) | `cspl-server` | 9100 | Yes (DCU devices) | Netty TCP for real-time DCU data |
| mongodb | `cspl-mongodb` | 27017 | No (internal) | Document database |
| mysql | `cspl-mysql` | 3306 | No (internal) | Relational database |
| nginx | `cspl-nginx` | 80 | 8080 | **INACTIVE** — exited, superseded by NPM |
| seed | `cspl-seed` | — | No | One-shot database seeder |

---

## Port Map (Complete Reference)

| Port | Protocol | Host-Published | Who Connects | Purpose |
|---|---|---|---|---|
| **80** | TCP | Yes | Internet | HTTP entry — NPM listens here |
| **443** | TCP | Yes | Internet | HTTPS entry — NPM listens here (SSL) |
| **9100** | TCP | Yes | DCU devices (field) | Netty TCP for real-time DCU data |
| **8080** | TCP | **No** | NPM only (Docker network) | Tomcat — NPM forwards here |
| **8102** | TCP | Yes (for debugging) | ccms_ui (Docker DNS) | Spring Boot REST API |
| **27017** | TCP | No | ccms_ui, server | MongoDB |
| **3306** | TCP | No | ccms_ui | MySQL |

### Do NOT expose these ports publicly

| Port | Why It Must Stay Internal |
|---|---|
| 8080 | Tomcat has no auth at the container level — NPM handles SSL/routing |
| 8102 | Spring Boot REST has no auth at the container level — only ccms_ui should reach it |
| 27017 | MongoDB has no authentication enabled by default |
| 3306 | MySQL root password is the only protection |

---

## Nginx Proxy Manager (NPM) Integration

NPM runs as a **separate container** (not in this docker-compose stack) and is the only public entry point.

### NPM Proxy Host Configuration (Reference — Do Not Change)

| Setting | Value |
|---|---|
| Domain | `apgp.crossfields.in` |
| Scheme | `http` |
| Forward hostname | `cspl-ccms-ui` |
| Forward port | `8080` |
| Redirect rule | `/` → `/CCMS/` (301) |
| SSL | Enabled |

### Critical: Shared Docker Network

NPM reaches `cspl-ccms-ui` by **container name**. This only works if NPM and the CCMS stack are on the **same Docker network**.

To verify:
```bash
docker network ls
docker network inspect <network_name> | grep -A5 "cspl-ccms-ui"
```

If the networks are separate, NPM will fail with `502 Bad Gateway`. To fix:
```bash
docker network connect <ccms_network_name> <npm_container_name>
```

### What breaks if NPM config changes

| NPM Setting | Impact if Changed |
|---|---|
| Forward hostname changed | 502 — NPM can't resolve the container name |
| Forward port changed | 502 — NPM connects to wrong port |
| Domain changed | SSL cert mismatch — browser rejects connection |
| Redirect rule removed | Users must type `/CCMS/` manually — broken UX |
| SSL disabled | Traffic in transit is unencrypted |

---

## Files

### Core Configuration Files

| File | Purpose |
|---|---|
| `docker-compose.yml` | Service definitions, volumes, healthchecks |
| `nginx/Dockerfile` | nginx image (cspl-nginx — **inactive**) |
| `nginx/nginx.conf.template` | nginx reverse proxy config (cspl-nginx — **inactive**) |
| `nginx/docker-entrypoint.sh` | Renders nginx config via envsubst (cspl-nginx — **inactive**) |
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

---

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
| `PUBLIC_DOMAIN` | `ccms.example.com` | nginx server_name (cspl-nginx — inactive) |
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

---

## Deployment Steps (YAML-Only Paste)

The stack is designed for Hostinger's YAML editor — no Git repository URL required.

### 1. Hostinger VPS

1. Create a Hostinger VPS
2. Open ports in firewall:
   - **80** (TCP) — HTTP web dashboard
   - **443** (TCP) — HTTPS (SSL)
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

### 3. NPM Configuration

After the CCMS stack is running, configure NPM:
1. Access NPM admin UI
2. Create a Proxy Host:
   - Domain: `apgp.crossfields.in`
   - Forward hostname: `cspl-ccms-ui`
   - Forward port: `8080`
   - Enable SSL (Let's Encrypt)
   - Add redirect: `/` → `/CCMS/` (301)
3. Ensure NPM and the CCMS stack share the same Docker network

### 4. Verify Deployment

Check that all containers start successfully:

```
http://<VPS-IP>/
```

This should redirect through NPM to the CCMS login page.

---

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

---

## Service Dependencies

```
mongodb (healthy) ──→ seed (completed_successfully) ──→ server (healthy)
     ↓                            ↓                         ↓
mysql (healthy) ────→ seed (completed_successfully) ──→ ccms_ui (healthy)
                                                            ↓
                                                    NPM (external)
```

**Seed:** Runs once (`restart: "no"`), exits 0 after seeding. On subsequent starts it skips if `SEED_DATA=true` (the data volumes persist).

---

## Healthchecks

All long-running services have healthchecks:

| Service | Healthcheck | Start Period |
|---|---|---|
| mongodb | `mongo --eval "db.adminCommand('ping').ok"` | — |
| mysql | `mysqladmin ping -uroot -p${MYSQL_ROOT_PASSWORD}` | — |
| server | `wget --spider http://localhost:8102/user/push/hafe_open_connections` | 30s |
| ccms_ui | `wget -qO- http://localhost:8080/CCMS/` | 60s |
| nginx (cspl-nginx) | `wget --spider http://localhost:80/` | — |

> **Note:** cspl-nginx healthcheck in deployed VPS uses `http://localhost:80/` (no `/CCMS/` path).
> The repo version uses `http://localhost:80/CCMS/` which is a more thorough end-to-end check.
> cspl-nginx is currently **exited** — this healthcheck is inactive.

---

## Data Persistence

| Data | Storage | Survives |
|---|---|---|
| MongoDB | Named volume `mongodb_data` | `docker compose down`, container restart |
| MySQL | Named volume `mysql_data` | `docker compose down`, container restart |
| Historical CSVs | Named volume `historical_data` | Container restart |

To wipe everything and re-seed:
```bash
docker compose down -v
docker compose up -d
```

---

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

---

## Installation Date Migration

See [`docs/installation_date_migration.md`](docs/installation_date_migration.md) for full documentation.

Quick reference:
- **Migration class:** `CCMS_UI/.../migration/InstallationDateMigration.java`
- **Auto-capture:** `SERVER/.../repos/DeviceRequestDataRepository.java` (`$setOnInsert`)
- **Bean must be declared in** `spring-config-docker.xml` (component-scan is controller-only)
- **Frontend date pickers:** Monitor & Control page + Public Monitor (`/public_monitor`) map page
- **API params:** `start_date` / `end_date` (format: `YYYY-MM-DD`)
- **Migration result:** 17,442 documents migrated, 1 failed, 7 skipped

---

## Config Coupling Map

Environment variables flow through multiple layers. Changing a value in one place requires understanding the full chain:

```
Hostinger ENV VARS (or .env for local dev)
  │
  ├─► docker-compose.yml (${VAR} substitution)
  │     │
  │     ├─► Container ENV (set at runtime)
  │     │     │
  │     │     ├─► docker-entrypoint.sh
  │     │     │     │
  │     │     │     └─► JVM -D system properties
  │     │     │           │
  │     │     │           └─► Spring XML ${placeholders}
  │     │     │                 (applicationContext.xml,
  │     │     │                  spring-config-docker.xml)
  │     │     │
  │     │     └─► application.properties ${placeholders}
  │     │
  │     └─► Healthcheck commands (use same port values)
  │
  └─► NPM Proxy Host config (manual, not in this repo)
        │
        └─► Forward hostname/port must match container name + Tomcat port
```

### If you change a port, you must update ALL of these:

| What Changed | Also Update |
|---|---|
| `SERVER_PORT` (8102) | docker-compose.yml server port mapping, server healthcheck URL, ccms_ui `BACKEND_HTTP_PORT`, NPM forward port (if proxying to server) |
| `NETTY_PORT` (9100) | docker-compose.yml server port mapping, VPS firewall rules |
| `TOMCAT_PORT` (8080) | NPM forward port, ccms_ui healthcheck URL |
| `MONGODB_HOST` | docker-compose.yml server + ccms_ui env vars, Spring XML configs |
| `MYSQL_HOST` | docker-compose.yml ccms_ui env var, spring-config-docker.xml JDBC URL |
| Container name | NPM forward hostname, all Docker DNS references, docker-compose.yml `depends_on` |

---

## DO NOT CHANGE — DevOps Lockdown

The following files and values are **infra-critical**. Modifying them without understanding the full impact will break the deployment. Changes require PR review from the repo owner.

### Container Names (Never Rename)

| Container | Used By |
|---|---|
| `cspl-ccms-ui` | NPM forward target, Docker DNS references |
| `cspl-server` | ccms_ui `CCMS_SERVER_HOST`, Docker DNS references |
| `cspl-mongodb` | docker-compose.yml service names, Docker DNS |
| `cspl-mysql` | docker-compose.yml service names, Docker DNS |
| `cspl-seed` | docker-compose.yml `depends_on` |
| `cspl-nginx` | docker-compose.yml (inactive) |
| `npm_proxy_manager` | External — not in this repo |

### Port Numbers (Never Change Without Updating All References)

| Port | Value | Where Referenced |
|---|---|---|
| Tomcat | `8080` | NPM forward port, ccms_ui healthcheck, Dockerfile EXPOSE |
| Spring Boot | `8102` | docker-compose.yml, server healthcheck, ccms_ui env var, application.properties |
| Netty TCP | `9100` | docker-compose.yml, VPS firewall, netty-server.properties |
| MongoDB | `27017` | docker-compose.yml, application.properties, Spring XML configs |
| MySQL | `3306` | docker-compose.yml, spring-config-docker.xml JDBC URL |

### Context Paths (Never Change)

| Path | Service | Where Referenced |
|---|---|---|
| `/CCMS/` | ccms_ui | NPM redirect rule, nginx.conf.template, ccms_ui healthcheck, web.xml |
| `/user` | server | docker-compose.yml `SERVER_CONTEXT_PATH`, application.properties, Spring XML |

### Files — NEVER Edit Without Approval

| File | Why |
|---|---|
| `docker-compose.yml` | Deployment contract — service definitions, ports, healthchecks, depends_on, volumes |
| `SERVER/ccms/Dockerfile` | Build logic, exposed ports (8102, 9100), entrypoint |
| `CCMS_UI/STARTUP/ccms_ui/Dockerfile` | Build logic, exposed port (8080), entrypoint, copies spring-config-docker.xml |
| `nginx/Dockerfile` | nginx image build (cspl-nginx — inactive but don't break it) |
| `db/seeds/Dockerfile` | Seed image build, baked-in mongo.archive |
| `SERVER/ccms/docker-entrypoint.sh` | Env var → JVM -D property mapping for server |
| `CCMS_UI/STARTUP/ccms_ui/docker-entrypoint.sh` | Env var → JVM -D property mapping for ccms_ui |
| `nginx/docker-entrypoint.sh` | envsubst rendering for nginx config |
| `db/seeds/seed.sh` | Database seeding orchestrator |
| `.github/workflows/build-images.yml` | CI/CD pipeline — builds and pushes all 4 Docker images |
| `nginx/nginx.conf.template` | Internal reverse proxy routing (cspl-nginx — inactive) |
| `SERVER/ccms/conf/*` | Docker-deployed config overrides (application.properties, Spring XML, log4j, netty) |
| `CCMS_UI/STARTUP/ccms_ui/conf/*` | Docker-deployed Spring MVC config (spring-config-docker.xml) |
| `db/seeds/mongo.archive` | MongoDB seed data (~98 MB) — regenerating requires mongodump from live DB |
| `db/seeds/mysql/01-schema.sql` | MySQL schema — never edit, only append new numbered files |

### NPM Configuration (External to This Repo)

| Setting | Value | Do Not Change |
|---|---|---|
| Domain | `apgp.crossfields.in` | Changing requires new SSL cert |
| Forward hostname | `cspl-ccms-ui` | Must match container name exactly |
| Forward port | `8080` | Must match Tomcat port |
| Redirect | `/` → `/CCMS/` | Must match UI context path |
| SSL | Enabled | Never disable in production |

---

## SAFE TO EDIT — Team Development Zones

The following files are safe for team members to modify without affecting deployment or infrastructure:

### Java Source Code (SERVER)

| Path | What's There |
|---|---|
| `SERVER/ccms/src/main/java/**/controller/*.java` | REST API controllers |
| `SERVER/ccms/src/main/java/**/services/*.java` | Business logic |
| `SERVER/ccms/src/main/java/**/repos/*.java` | MongoDB repositories |
| `SERVER/ccms/src/main/java/**/netty/*.java` | Netty TCP server + handlers |
| `SERVER/ccms/src/main/java/**/model/*.java` | Data models / POJOs |

### Java Source Code (CCMS UI)

| Path | What's There |
|---|---|
| `CCMS_UI/STARTUP/ccms_ui/src/main/java/**/controller/*.java` | Spring MVC controllers |
| `CCMS_UI/STARTUP/ccms_ui/src/main/java/**/services/*.java` | Business logic |
| `CCMS_UI/STARTUP/ccms_ui/src/main/java/**/dao/*.java` | Data access (MongoDB + MySQL) |
| `CCMS_UI/STARTUP/ccms_ui/src/main/java/**/pojo/*.java` | Hibernate entities |

### Frontend (AngularJS)

| Path | What's There |
|---|---|
| `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/**/*.js` | AngularJS controllers, services, directives |
| `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/**/*.html` | Views / templates |
| `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/**/*.css` | Stylesheets |
| `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/vendor/**` | Third-party JS libs |

### Tests

| Path | What's There |
|---|---|
| `SERVER/ccms/src/test/**/*.java` | Server JUnit tests (76 tests) |
| `CCMS_UI/STARTUP/ccms_ui/src/test/**/*.java` | UI JUnit tests (75 tests) |
| `CCMS_UI/STARTUP/ccms_ui/src/test/javascript/**/*.js` | AngularJS Karma tests (66 tests) |
| `CCMS_UI/STARTUP/ccms_ui/karma.conf.js` | Karma test runner config |

### Build Dependencies

| File | What's Safe |
|---|---|
| `SERVER/ccms/pom.xml` | Adding/updating Maven dependencies |
| `CCMS_UI/STARTUP/ccms_ui/pom.xml` | Adding/updating Maven dependencies |
| `CCMS_UI/STARTUP/ccms_ui/package.json` | Adding/updating npm dev dependencies |

### SQL Migrations

| Path | Rule |
|---|---|
| `db/seeds/mysql/` | **ADD** new numbered files (e.g., `02-indexes.sql`) — never edit `01-schema.sql` |

---

## Rules

1. **No secrets in Git** — Secrets go in Hostinger's Environment Variables, never in `.env` or any committed file.
2. **No `.env` file on server** — All configuration comes from Hostinger environment variables.
3. **USERS_JSON without quotes** — Do not wrap JSON values in single or double quotes.
4. **SMOKE_ADMIN_PASSWORD must match USERS_JSON** — The smoke test login uses this password.
5. **Only expose ports 80, 443, and 9100 publicly** — All other ports are internal only.
6. **NPM is the only public reverse proxy** — cspl-nginx is inactive; do not re-enable without removing NPM.
7. **Container names are fixed** — `cspl-ccms-ui`, `cspl-server`, `cspl-mongodb`, `cspl-mysql` must not change (NPM and Docker DNS depend on them).
8. **Context paths are fixed** — `/CCMS/` for UI, `/user` for SERVER. Changing these breaks NPM routing and all API calls.
9. **Tomcat port 8080 is fixed** — NPM forwards here. Changing it breaks the NPM proxy host.
10. **Netty port 9100 is fixed** — DCU devices connect externally to VPS:9100.
11. **Data survives container restarts** via named Docker volumes.
12. **SSL is handled by NPM** — Not by cspl-nginx. Do not enable SSL inside the CCMS containers.
13. **Network requirement** — NPM and the CCMS stack must share the same Docker network for NPM to resolve `cspl-ccms-ui` by container name.
14. **Conf/ directories are Docker-deployed overrides** — `SERVER/ccms/conf/*` and `CCMS_UI/.../conf/*` are copied into Docker images at build time. They are NOT the same as `src/main/resources/*`. Edit them only if you understand the env var → JVM -D → Spring ${} chain.
15. **Seed data is baked into the image** — `db/seeds/mongo.archive` and `db/seeds/mysql/` are read-only at runtime. To update seed data, you must rebuild the `ccms-seed` image.
