# CCMS Database Seeds

This directory contains committed seed data so that a fresh clone
(`cp .env.example .env && docker compose up -d`) starts with a
populated database.

## Files

| File | Purpose |
|---|---|
| `mongo.archive` | `mongodump --archive` snapshot of the `ccms` database (21 collections: users, DCUs, events, meter data, scheduler configurations, etc.) |
| `mysql/01-schema.sql` | MySQL `employee_db` schema. Currently only creates a `ccms_meta` marker table — see header comment in the file for why the app doesn't actually need any MySQL tables |
| `seed.sh` | Orchestrator script. Runs inside the `seed` service container, waits for MongoDB + MySQL to be reachable, then restores from `mongo.archive`, applies the MySQL schema, and syncs users from `USERS_JSON` in `.env` |
| `Dockerfile` | Image build for the `seed` service. Based on `mongo:3.4` (provides `mongo` shell + `mongorestore`) with `bash` and `mysql-client` added |

## Behavior

The `seed` service in `docker-compose.yml` runs once on first
`docker compose up` and exits 0. The `server` and `ccms_ui`
services have `depends_on: seed: { condition: service_completed_successfully }`,
so the app only starts once seeding is complete.

To skip seeding (e.g. when restoring from your own backup), set
`SEED_DATA=false` in `.env`.

To re-seed from scratch:
```bash
docker compose down -v                 # wipe volumes
docker compose up -d --build           # seed runs on first start
```

## User sync (USERS_JSON)

The `ccms_user_details` collection is **not** restored from
`mongo.archive`. It is managed by the `sync_env_users` step in
`seed.sh`, which reads `USERS_JSON` from `.env` and inserts any
users that don't already exist in MongoDB. Existing users
(including those with passwords changed via the UI) are
preserved across restarts.

### Why this design

- **No accidental password resets.** If we restored users from the
  archive with `--drop`, every password change made via the UI
  would be lost on the next restart.
- **Env is the single source of truth for the initial user set.**
  The archive may contain legacy users from the original deployment;
  the env lets you add new operators without code changes.
- **The archive users stay.** The 6 users in `mongo.archive`
  are loaded as part of the user collection's initial state, but
  are never overwritten.

### USERS_JSON format

```json
[
  {
    "email": "admin@example.com",      // required; becomes MongoDB _id
    "password": "ChangeMe123!",         // required; stored plaintext
    "role": "SUPER ADMIN",              // default "USER"
    "first_name": "Admin",              // default ""
    "last_name": "User",                // default ""
    "dist": "ALL",                      // district scope
    "mondal": "ALL",                    // mandal scope
    "gp": "ALL",                        // gp scope
    "all_privileges": true              // grants all 13 UI permissions
  },
  {
    "email": "operator@example.com",
    "password": "...",
    "role": "USER",
    "dist": "YSR Kadapa"
  }
]
```

- `all_privileges: true` grants all 13 boolean permissions
  (`monitor_and_controller`, `history`, `event`, etc.); otherwise
  they are all `false`.
- The first user with `role: "SUPER ADMIN"` is automatically picked
  by `scripts/api-smoke-test.sh` as the login credential.

### Skip-if-exists behavior

For each entry in `USERS_JSON`:
- If a user with the same email already exists in MongoDB, **skip**
  (the existing record is left untouched, including any password
  changed via the UI)
- If no user with that email exists, **insert** the env-defined user

This means:
- To change a password via env, you must first **delete the user
  from MongoDB** (e.g. via the UI's user-management screen or
  `docker exec cspl-mongodb mongo ccms --eval "db.ccms_user_details.deleteOne({_id: 'user@example.com}')"`)
  and then re-run the seed
- To add a new user, append them to `USERS_JSON` and re-run the seed
- To remove a user that was added via env, you must delete it from
  MongoDB (env cannot remove users)

## How the archive was generated

```bash
docker exec cspl-mongodb mongodump --db ccms --archive=/tmp/ccms.archive
docker cp cspl-mongodb:/tmp/ccms.archive db/seeds/mongo.archive
```

The live `data/mongodb` directory on a fresh clone will be empty
before the `seed` service runs; the `seed` service populates Mongo
from the archive.

## Notes

- `mongo.archive` is a binary mongodump stream. Don't edit it by hand.
- The archive is committed to the repo so seeding is self-contained.
  Total size: ~98 MB.
- **Important:** capture the archive inside the container and `docker cp` it out — piping `mongodump --archive` through the host shell (`> file`) produces a corrupted file that `mongorestore` rejects.
- The archive's `ccms_user_details` collection (6 legacy users) is
  **preserved** by the seed (excluded from `--drop`) and is the
  initial state for the user table. The env's `USERS_JSON` is
  applied on top with skip-if-exists semantics.
