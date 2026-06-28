# CCMS — Street Light Management System

> **Public personal mirror of an internal CSPL project.**
> The application stack is a Docker Compose orchestration of MongoDB,
> MySQL, a Spring Boot + Netty server, an AngularJS-on-Tomcat UI, and an
> nginx reverse proxy. The stack self-seeds on first start from committed
> seed files.

---

## Security warning

> **The bundled `db/seeds/mongo.archive` contains a default admin user
> with publicly known credentials.** Anyone with read access to this repo
> (including anonymous GitHub visitors and crawlers) can see them.
>
> | Field | Value |
> |---|---|
> | Email | `admin@crossfieldssmart.com` |
> | Password | `Cspl@1234` (plaintext, in the BSON dump) |
> | Role | `SUPER ADMIN` |
>
> **Action required on first deploy:** log in with the above
> credentials, then change the password via the UI. If you do not
> intend to keep the seed data, set `SEED_DATA=false` in `.env` and
> start with empty databases.

---

## 30-second quick start

```bash
git clone https://github.com/askamoghb-rgb/SERVER_CCMS_UI_App.git
cd SERVER_CCMS_UI_App
cp .env.example .env
# edit .env — at minimum, change MYSQL_ROOT_PASSWORD and PUBLIC_DOMAIN
docker compose up -d --build
# wait ~5 min for the first build, then:
open http://localhost:8080/CCMS/
```

The `seed` service automatically runs on first start and populates both
databases from `db/seeds/`. See `Deployment_Guide.md` for the full
first-run flow, troubleshooting, and the per-component reference.

---

## Public Docker images

All four custom images are built and published to GitHub Container
Registry on every push to `main`:

| Image | Pull |
|---|---|
| Spring Boot + Netty server | `docker pull ghcr.io/askamoghb-rgb/ccms-server:latest` |
| AngularJS UI on Tomcat | `docker pull ghcr.io/askamoghb-rgb/ccms-ui:latest` |
| nginx reverse proxy | `docker pull ghcr.io/askamoghb-rgb/ccms-nginx:latest` |
| One-shot seeder | `docker pull ghcr.io/askamoghb-rgb/ccms-seed:latest` |

The `mongodb` and `mysql` images come from the official Docker Hub
registries — no custom images needed for those.

### Pin to a specific version

The CI workflow tags every image with both `:latest` and the short
commit SHA. To pin:

```yaml
image: ghcr.io/askamoghb-rgb/ccms-server:1c05717
```

To find the SHA for a release, look at the GitHub Actions run that
built it (the short SHA appears in the workflow run title and the
pushed tag list).

---

## For maintainers — publishing a new image

You do **not** need to do anything special. The workflow at
`.github/workflows/build-images.yml` runs automatically on every push
to `main` (excluding docs-only changes). It builds all four images
in parallel and pushes them to `ghcr.io/askamoghb-rgb/`.

To trigger a build manually: go to the **Actions** tab, select
**Build and push Docker images**, click **Run workflow**.

The first push takes ~5–10 min (Maven downloads, npm install). Later
pushes take ~30–90 seconds thanks to GitHub Actions layer caching.

---

## Repo layout

```
SERVER_CCMS_UI_App/
├── .github/workflows/      # CI: build & push images to GHCR
├── CCMS_UI/                # AngularJS UI + Spring MVC (Tomcat)
├── SERVER/                 # Spring Boot + Netty TCP server
├── db/seeds/               # Committed seed data (mongo.archive, SQL)
├── nginx/                  # nginx reverse proxy
├── scripts/                # backup / restore / test runners
├── docker-compose.yml      # Production stack (pulls public images)
├── .env.example            # Template for runtime config
├── Deployment_Guide.md     # Full deployment reference
└── README.md               # This file
```

---

## Tests

```bash
bash scripts/run-all-tests.sh
```

Runs three suites (SERVER JUnit, CCMS_UI JUnit, CCMS_UI AngularJS).
Current status: **217/217 pass**. No live database required.

---

## License

Personal/internal project. No public license declared.
