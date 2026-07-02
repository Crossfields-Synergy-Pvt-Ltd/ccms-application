# Installation Date Migration

## Summary

A new field `installation_date` (BSON `Date` type) was added to the `handshake_info` collection in MongoDB to enable date-based filtering of CCMS boxes by installation date, alongside existing district/mandal/gp filters.

## What Changed

| Collection | New Field | Type | Source | Example |
|---|---|---|---|---|
| `handshake_info` | `installation_date` | BSON Date | Parsed from existing `date` string (`DD-MM-YYYY HH:mm`) | `ISODate("2019-07-11T00:00:00Z")` |

The existing `date` string field is **preserved untouched**. The migration only adds, never modifies or removes existing data.

## How the Migration Runs

**File:** `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/migration/InstallationDateMigration.java`

Runs **automatically** via `@PostConstruct` when `cspl-ccms-ui` (Tomcat) container starts:

1. Queries `handshake_info` for documents missing `installation_date` that have a non-empty `date` string
2. Parses `"DD-MM-YYYY HH:mm"` → `java.util.Date` and writes it to `installation_date`
3. If all documents already have the field, logs "skipping" and does nothing
4. The migration is **idempotent** — safe to run on every startup

### Bean Registration

The migration class uses `@Component` but the Spring component-scan in `spring-config-docker.xml` is limited to `com.vnetsoft.ccms.controller`. An **explicit bean definition** is required:

```xml
<bean id="installationDateMigration"
    class="com.vnetsoft.ccms.migration.InstallationDateMigration" />
```

## How New Boxes Get Auto-Captured

**File:** `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/repos/DeviceRequestDataRepository.java`

The Netty handshake upsert includes `$setOnInsert: { installation_date: new Date() }`, so the first-ever handshake from a new CCMS box auto-sets its installation date. Subsequent handshakes never overwrite it.

## API Usage

All three endpoints on each controller accept optional `start_date` and `end_date` query parameters.

### MonitorController

| Endpoint | Params |
|---|---|
| `/dashboard/count` | `district`, `mandal`, `gp`, `start_date`, `end_date` |
| `/dashboard/map_data` | `district`, `mandal`, `gp`, `start_date`, `end_date` |
| `/dashboard/instant_data_filter` | `district`, `mandal`, `gp`, `page`, `size`, `start_date`, `end_date` |

### DCUController

| Endpoint | Params |
|---|---|
| `/dcu/handshake_list` | `district`, `mandal`, `gp`, `start_date`, `end_date` |
| `/dcu/handshake_info_by_id` | `district`, `mandal`, `gp`, `name`, `start_date`, `end_date` |
| `/dcu/dcu_name_list` | `district`, `mandal`, `gp`, `start_date`, `end_date` |

### Example

```bash
curl "http://localhost:8080/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL&start_date=2019-01-01&end_date=2019-12-31"
```

Date format: `YYYY-MM-DD`. Parsed server-side with `SimpleDateFormat("yyyy-MM-dd")`.

If `start_date` or `end_date` is missing or unparseable, `null` is used and no date filtering is applied.

## Frontend

Date range pickers are available on three pages:

| Page | Route | File |
|---|---|---|
| Dashboard | `/dashboard` | `dashboard-view.html` + `dashboard-controller.js` |
| Monitor & Control | `/monitorandcontrol` | `monitorandcontrol-list.html` + `monitorandcontrol-controllers.js` |
| Public Monitor (Map) | `/public_monitor` | `map-view.html` + `map-controller.js` |

The date picker uses the `date-range-picker` AngularJS directive (`vendor/daterangepicker/`). Predefined ranges (Today, Yesterday, Last 7 Days, This Month, Last Month) are available.

## Migration Result

| Metric | Count |
|---|---|
| Total documents in `handshake_info` | 17,450 |
| Migrated successfully | 17,442 |
| Failed (header row) | 1 |
| Skipped (no `date` field) | 7 |

The 8 documents without `installation_date`:
- `#Serial Number` (header row, `date: "Installation Date"`)
- 6 DCU serials with no `date` field at all
- 1 ObjectId document with no `date` field

## Deployment

Images must be rebuilt locally and pushed or deployed:

```bash
docker build -t ghcr.io/crossfields-synergy-pvt-ltd/ccms-ui:latest CCMS_UI/STARTUP/ccms_ui
docker compose up -d
```

## Verification

```bash
# Check migration logs
docker compose logs ccms_ui | grep -i InstallationDateMigration

# Check data in MongoDB
docker compose exec mongodb mongo ccms --quiet --eval '
  db.handshake_info.count({installation_date: {$exists: true}})
'

# Smoke test API
curl "http://localhost:8080/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL&start_date=2019-01-01&end_date=2019-12-31"
```
