# CCMS Deployment Guide

This guide covers the current CCMS Docker stack for local development and Hostinger VPS deployment.

## Architecture

Nginx Proxy Manager (NPM) is external to this repository and is the only public HTTP reverse proxy. The Compose stack has no Nginx service.

~~~text
Internet :80/:443  -> NPM -> cspl-ccms-ui:8080
DCU devices :9100   -> cspl-server:9100
cspl-ccms-ui:8080    -> cspl-server:8102/user
                      -> cspl-mongodb:27017
                      -> cspl-mysql:3306
~~~

The UI is deployed as ROOT.war at /. The Spring Boot API uses /user.

| Component | Container | Internal port(s) | Public exposure |
|---|---|---:|---|
| NPM | npm_proxy_manager | 80, 443 | VPS 80/443 |
| CCMS UI | cspl-ccms-ui | 8080 | NPM only |
| SERVER | cspl-server | 8102, 9100 | 9100 for DCUs; 8102 only for controlled debugging |
| MongoDB | cspl-mongodb | 27017 | Internal only |
| MySQL | cspl-mysql | 3306 | Internal only |
| Seed | cspl-seed | — | One-shot internal service |

Never expose 8080, 8102, 27017, or 3306 to the Internet.

## Environment configuration

### Hostinger production

Set runtime values in Hostinger's Environment Variables section. Do not create or commit a .env file on the VPS. Paste the complete docker-compose.yml into the YAML editor and set at least:

~~~text
MYSQL_ROOT_PASSWORD=<strong-secret>
~~~

For seeded deployments, set USERS_JSON and make SMOKE_ADMIN_PASSWORD match the selected user's password. Enter USERS_JSON as raw JSON without surrounding shell quotes.

### Local Docker development

~~~bash
cp .env.example .env
# edit .env and replace CHANGE_ME values
~~~

The complete annotated template is [.env.example](.env.example). Supported defaults:

| Variable | Default | Purpose |
|---|---|---|
| MYSQL_ROOT_PASSWORD | required | MySQL root password |
| MYSQL_DATABASE | employee_db | MySQL database |
| MONGODB_DATABASE | ccms | MongoDB database |
| MONGODB_HOST | mongodb | MongoDB hostname |
| MONGODB_PORT | 27017 | MongoDB port |
| MONGODB_USERNAME | empty | Optional MongoDB username |
| MONGODB_PASSWORD | empty | Optional MongoDB password |
| SERVER_PORT | 8102 | Spring Boot HTTP port |
| SERVER_CONTEXT_PATH | /user | Spring context path |
| NETTY_PORT | 9100 | DCU TCP port |
| TOMCAT_PORT | 8080 | Tomcat port |
| APP_CONTEXT | / | UI root context |
| CCMS_SERVER_HOST | server | UI-to-SERVER hostname |
| BACKEND_HTTP_PORT | 8102 | UI-to-SERVER port |
| BACKEND_HTTP_CONTEXT | /user | UI-to-SERVER context |
| SERVER_LOG_DIR | /home/CCMS/roadmap/logs | SERVER log directory |
| SEED_DATA | true | Enable seeding |
| SYNC_USERS | true | Synchronise USERS_JSON independently of seed data |
| USERS_JSON | [] | Users inserted if absent |
| SMOKE_ADMIN_EMAIL | admin@example.com | Smoke-test login |
| SMOKE_ADMIN_PASSWORD | — | Matching smoke-test password |

## Hostinger deployment

1. Create a Docker Compose deployment in Hostinger.
2. Paste the entire docker-compose.yml into the YAML editor.
3. Add MYSQL_ROOT_PASSWORD and the desired user/seed variables.
4. Deploy and wait for mongodb, mysql, seed, server, and ccms_ui to become ready.

The custom images are pulled from GHCR:

~~~text
ghcr.io/crossfields-synergy-pvt-ltd/ccms-server:latest
ghcr.io/crossfields-synergy-pvt-ltd/ccms-ui:latest
ghcr.io/crossfields-synergy-pvt-ltd/ccms-seed:latest
~~~

The build entries are retained for local source builds. Seed data is baked into the seed image; no repository bind mounts are required.

### NPM proxy host

Configure NPM with:

| Setting | Value |
|---|---|
| Domain | apgp.crossfields.in |
| Scheme | http |
| Forward hostname | cspl-ccms-ui |
| Forward port | 8080 |
| SSL | Enabled with Let's Encrypt |

Remove any old / -> /CCMS/ redirect. The UI is served at root. NPM and the CCMS stack must share a Docker network:

~~~bash
docker network ls
docker network inspect <shared-network> | grep -A5 cspl-ccms-ui
docker network connect <ccms-network> npm_proxy_manager
~~~

Only open TCP ports 80, 443, and 9100. SSH on 22 is optional.

### Verification

~~~bash
docker compose ps
docker compose logs --tail 100 seed server ccms_ui
nc -vz <vps-ip> 9100
curl -I https://apgp.crossfields.in/
bash scripts/api-smoke-test.sh
~~~

The application URL is https://apgp.crossfields.in/. Public monitor is /#/public_monitor and login is /#/login.

## Seed data and users

On a new volume, the seed service always waits for MongoDB. When `SEED_DATA=true`, it also waits for MySQL, restores `db/seeds/mongo.archive`, and applies the MySQL schema. When `SYNC_USERS=true`, it synchronises `USERS_JSON` independently of archive/schema seeding.

For a fresh deployment without the committed application data, use `SEED_DATA=false` and `SYNC_USERS=true` so an admin user is still created and can log in. This does not restore schedules, hierarchy, DCUs, or other archive data; those must come from a separate backup or be created through the UI.

~~~json
[{"email":"admin@example.com","password":"CHANGE_ME","role":"SUPER ADMIN","first_name":"Admin","last_name":"User","all_privileges":true}]
~~~

Existing users are preserved using skip-if-exists semantics. To reset from the committed seed, back up first, then:

~~~bash
docker compose down -v
docker compose up -d
~~~

This removes all named data volumes and is destructive.

## Persistence, backup, and restore

| Volume | Container path | Purpose |
|---|---|---|
| mongodb_data | /data/db | MongoDB data |
| mysql_data | /var/lib/mysql | MySQL data |
| historical_data | /home/data/dontdelete | Historical CSV files |

~~~bash
bash scripts/backup.sh
bash scripts/restore.sh backups/<timestamp>
~~~

The backup scripts reliably cover MongoDB ccms data and the employee_db MySQL
dump. Their historical-data path still targets the legacy repository
directory data/dontdelete; when using the production historical_data named
volume, back up that volume separately before maintenance.

For example, create a temporary archive from the running UI container:

~~~bash
docker run --rm --volumes-from cspl-ccms-ui -v "$PWD/backups/<timestamp>:/backup" alpine \
  tar -czf /backup/historical-data.tar.gz -C /home/data dontdelete
~~~

## Maintenance

~~~bash
docker compose pull
docker compose up -d

docker compose build
docker compose up -d

docker compose build ccms_ui
docker compose up -d ccms_ui

docker compose logs -f ccms_ui
docker compose logs -f server
docker compose ps
~~~

The CI workflow publishes the three custom images on pushes to main. Never put secrets in GitHub, the Compose file, or a committed .env.

## Health and dependencies

~~~text
mongodb (healthy) ──┐
                    ├──► seed (completed successfully) ──► server (healthy)
mysql (healthy) ────┘                                      └──► ccms_ui
~~~

Healthchecks use http://localhost:8102/user/push/hafe_open_connections for SERVER and http://localhost:8080/ for the UI.

## Current operational behavior

- Handshake parsing fails closed on malformed packets and logs database failures with gateway context.
- Channel registration is safe across reconnects; stale channels cannot remove newer connections.
- SERVER file logging uses Log4j2 and SERVER_LOG_DIR.
- DCU create/update/delete normalises gateway identity, preserves protocol fields, and reports business failures.
- Pending configurations retry on a newly registered reconnect.
- installation_date is captured for new handshakes and migrated for older records when needed.

These changes preserve the existing DCU protocol, ports, service names, MongoDB collections, and API routes.

## Testing

~~~bash
bash scripts/run-all-tests.sh
bash scripts/api-smoke-test.sh
~~~

The first command runs SERVER Maven tests, CCMS_UI Java tests, and AngularJS tests. The smoke test requires a running deployment and valid SMOKE_ADMIN_EMAIL/SMOKE_ADMIN_PASSWORD values.

## Troubleshooting

| Symptom | Checks |
|---|---|
| NPM returns 502 | Confirm cspl-ccms-ui is healthy, NPM shares its Docker network, and forwarding uses port 8080. |
| Root redirects to /CCMS/ | Remove the old NPM redirect and confirm ROOT.war is deployed. |
| seed fails | Inspect docker compose logs seed and verify database health, SEED_DATA, SYNC_USERS, and USERS_JSON. |
| Login fails with SEED_DATA=false | Confirm SYNC_USERS=true, USERS_JSON contains a valid user, and rerun the one-shot seed service. |
| MongoDB is empty | Check whether seeding was disabled or existing volumes were reused; do not wipe volumes without a backup. |
| UI cannot reach SERVER | Verify CCMS_SERVER_HOST=server, BACKEND_HTTP_PORT=8102, and BACKEND_HTTP_CONTEXT=/user. |
| DCUs cannot connect | Check the firewall and host mapping for TCP 9100, then inspect server logs. |
| Handshake is rejected | Inspect SERVER logs for CRC, serial validation, persistence, and channel-registration errors. |
| Historical files are missing | Check historical_data and restore it from backup if necessary. |
| Browser shows stale UI | Hard-refresh or clear browser cache after deployment. |

## Configuration files

| File | Purpose |
|---|---|
| docker-compose.yml | Services, ports, healthchecks, dependencies, and volumes |
| .env.example | Local environment template |
| SERVER/ccms/Dockerfile | SERVER image and exposed ports |
| SERVER/ccms/docker-entrypoint.sh | SERVER environment-to-JVM mapping |
| SERVER/ccms/conf/* | Docker SERVER configuration overrides |
| CCMS_UI/STARTUP/ccms_ui/Dockerfile | ROOT.war/Tomcat image |
| CCMS_UI/STARTUP/ccms_ui/docker-entrypoint.sh | UI environment-to-JVM mapping |
| CCMS_UI/STARTUP/ccms_ui/conf/* | Docker UI Spring configuration |
| db/seeds/Dockerfile | Seed image with baked-in data |
| db/seeds/seed.sh | Database seeding and user synchronisation |
| scripts/backup.sh | Backup |
| scripts/restore.sh | Restore |
| scripts/api-smoke-test.sh | Live API verification |

For repository safety rules and configuration coupling, see [AGENTS.md](AGENTS.md).
