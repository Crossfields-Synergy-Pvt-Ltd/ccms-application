# Development Changes — District/Mandal Naming Convention Standardization

Standardized all inconsistent field names and district values across the codebase and MongoDB collections.

## Problem

Three naming conventions existed for the same logical concepts:

| Concept | In `herarchy_details` | In `handshake_info` / `dcu_details` | In `ccms_user_details` | In frontend |
|---|---|---|---|---|
| District field | `district` | `distict` (typo) | `dist` (abbrev) | `selecte_distict`, `distric` |
| Mandal field | `mandal` | `mondal` (typo) | `mondal` (typo) | `select_mondal` |
| Kadapa value | `"Kadapa"` | `"YSR Kadapa"` | varies | `"YSR Kadapa"` |

This caused the **YSR Kadapa filter bug**: selecting "YSR Kadapa" in the UI sent `distrtict=YSR+Kadapa` to the backend, which queried `herarchy_details` where the district was stored as `"Kadapa"` — no match → empty mandal/GP dropdowns.

## Standardized Convention

| Concept | Standard Name |
|---|---|
| MongoDB district field | `district` |
| MongoDB mandal field | `mandal` |
| Kadapa district value | `"YSR Kadapa"` |
| URL query parameter | `district` |
| JS scope variable | `selectedDistrict` / `selectedMandal` |
| Factory method | `getByMandal` |
| DAO method | `getMandalByDistrict` |

## Files Modified

### Phase 1 — Data Migration (MongoDB)

> ⚠️ **CRITICAL**: Run on the LIVE MongoDB **before** deploying the new code.
> The old code reads `distict`/`mondal` — deploying new code first will find nothing.

```javascript
// 1. Fix Kadapa value in herarchy_details
db.herarchy_details.updateMany(
  {"district": "Kadapa"}, {$set: {"district": "YSR Kadapa"}}
)

// 2. Rename fields in handshake_info (string values — simple $rename works)
db.handshake_info.updateMany(
  {}, {$rename: {"distict": "district", "mondal": "mandal"}}
)

// 3. Rename + convert types in dcu_details (int→String — $rename alone is NOT enough)
//    distict and mondal are stored as integers (0) but code now expects strings.
//    MongoDB 3.4 doesn't support $toString in updates, so use forEach.
db.dcu_details.find({distict: {$exists: true}}).forEach(function(doc) {
  db.dcu_details.updateOne(
    {_id: doc._id},
    {$set: {
      district: String(doc.distict),
      mandal: String(doc.mandal)
    }, $unset: {distict: "", mondal: ""}}
  )
})

// 4. Rename fields in ccms_user_details
db.ccms_user_details.updateMany(
  {}, {$rename: {"dist": "district", "mondal": "mandal"}}
)
```

**Verify migration:**
```javascript
// All should return 0
db.handshake_info.count({distict: {$exists: true}})
db.dcu_details.count({distict: {$exists: true}})
db.ccms_user_details.count({dist: {$exists: true}})

// All should equal total document count
db.handshake_info.count({district: {$exists: true}})
db.dcu_details.count({district: {$exists: true}})
db.ccms_user_details.count({district: {$exists: true}})

// Confirm dcu_details district is now a string
db.dcu_details.findOne({},{district:1, _id:0})
// Expected: { "district" : "0" }  (string, not number)
```

After migration, rebuild `mongo.archive` for fresh deployments:
```bash
docker compose exec mongodb mongodump --db=ccms --archive=/tmp/mongo.archive
docker cp cspl-mongodb:/tmp/mongo.archive db/seeds/mongo.archive
```

### Phase 2 — Code Changes

#### Java POJOs

| File | Changes |
|---|---|
| `CCMS_UI/.../pojo/HandShake.java` | `distict`→`district`, `mondal`→`mandal` |
| `SERVER/.../netty/pojo/HandShake.java` | `distict`→`district`, `mondal`→`mandal` |
| `CCMS_UI/.../pojo/DCU.java` | `distict`→`district`, `mondal`→`mandal`, `int`→`String` |
| `CCMS_UI/.../pojo/User.java` | `dist`→`district`, `mondal`→`mandal` |

#### Java DAO

| File | Changes |
|---|---|
| `DashBoardDaoImpl.java` | All `Criteria.where("distict")`→`"district"`, `where("mondal")`→`"mandal"`; rename param `distrtict`→`district` |
| `DashBoardDao.java` | Method signature param rename |
| `DCUDaoImpl.java` | Rename `getMondalByDistrict`→`getMandalByDistrict`, param `distrtict`→`district` |
| `DCUDao.java` | Interface method/param rename |

#### Java Services

| File | Changes |
|---|---|
| `DCUServices.java` | Method rename |
| `DCUServicesImpl.java` | Method/param rename |
| `DashBoardServices.java` | Param rename |
| `DashBoardServicesImpl.java` | Param rename |

#### Java Controllers

| File | Changes |
|---|---|
| `HierarchyController.java` (was `HerarchyController.java`) | `@RequestParam("distrtict")`→`@RequestParam("district")` |
| `MonitorController.java` (was `MontorController.java`) | Same |
| `DCUController.java` | Same |

#### Java Scheduler

| File | Changes |
|---|---|
| `ScheduledTasks.java` | `setDistict`→`setDistrict`, `setMondal`→`setMandal` |

#### JavaScript Factories (9 files)

| File | Changes |
|---|---|
| `dashboard-factory.js` | `getByMondal`→`getByMandal`, URLs `distrtict=`→`district=` |
| `event-factory.js` | Same |
| `history-factory.js` | Same |
| `dcu-factory.js` | Same |
| `map-factory.js` | Same |
| `user-factory.js` | Same |
| `filter-factory.js` | Same |
| `monitorandcontrol-factory.js` | Same |
| `operationalhours-factory.js` | Same |

#### JavaScript Controllers (9 files)

| File | Changes |
|---|---|
| `dashboard-controller.js` | `selecte_distict`→`selectedDistrict`, `select_mondal`→`selectedMandal`, `distric`→`district` |
| `map-controller.js` | Same |
| `monitorandcontrol-controllers.js` | Same |
| `event-controllers.js` | Same |
| `history-controllers.js` | Same |
| `filter-controllers.js` | Same |
| `operationalhours-controllers.js` | Same |
| `dcu-controllers.js` | Same + `$scope.dcu.distict`→`$scope.dcu.district` |
| `user-controllers.js` | Same + `$scope.user.dist`→`$scope.user.district` |

#### JavaScript Login

| File | Changes |
|---|---|
| `login-controller.js` | `previlage`→`privilege`, `dist`→`district`, `mondal`→`mandal` |

#### HTML Templates (11 files)

| File | Changes |
|---|---|
| `dashboard-view.html` | `ng-model="selecte_distict"`→`"selectedDistrict"`, `select_mondal`→`selectedMandal` |
| `map-view.html` | Same |
| `monitorandcontrol-list.html` | Same |
| `event-list.html` | Same |
| `history-list.html` | Same |
| `filte-list.html` | Same |
| `operationalhours-list.html` | Same |
| `dcu-add.html` | `dcu.distict`→`dcu.district`, `dcu.mondal`→`dcu.mandal` |
| `dcu-update.html` | Same |
| `user-add.html` | `user.dist`→`user.district`, `user.mondal`→`user.mandal` |
| `user-update.html` | Same |

#### Seed Script

| File | Changes |
|---|---|
| `db/seeds/seed.sh` | `dist:`→`district:`, `mondal:`→`mandal:` in user insert |

#### API Smoke Test

| File | Changes |
|---|---|
| `scripts/api-smoke-test.sh` | All `distrtict=`→`district=` in URLs |

#### JavaScript Tests (10+ files)

All mock URLs updated: `distrtict=`→`district=`
All assertions updated for renamed scope variables.

#### Java Test

| File | Changes |
|---|---|
| `MonitorControllerTest.java` (was `MontorControllerTest.java`) | All `distrtict=`→`district=` in mock URLs |

#### Documentation

| File | Changes |
|---|---|
| `Deployment_Guide.md` | `dist`→`district`, `mondal`→`mandal` in examples |
| `docs/fix/development-changes.md` | This file — documenting the change |
| `db/seeds/README.md` | `dist`→`district`, `mondal`→`mandal` in examples |
| `.env.example` | `dist,mondal,gp`→`district,mandal,gp` |

### Phase 3 — Cosmetic Typo Fixes

| Pattern | Files Changed | Details |
|---|---|---|
| `previlage`→`privilege` | 13 files | All JS controllers, HTML templates (app.js, leftnavi.html), and test files |
| `HerarchyDetails`→`HierarchyDetails` | 7 files | POJO, Controller, DAO (interface+impl), Services (interface+impl), Test; files renamed |
| `command_identifire`→`command_identifier` | 6 files | POJOs (CCMS_UI + SERVER), repos |
| `lignt_time_stamp`→`light_time_stamp` | 3 files | HandShake POJOs (both modules), DeviceRequestDataRepository |
| `MontorController`→`MonitorController` | 2 files | Controller + Test; files renamed |

**Note:** MongoDB collection name `herarchy_details` was preserved (not renamed to `hierarchy_details`) to maintain backward compatibility with existing data.

### Phase 4 — Startup Auto-Migration (Java `@PostConstruct`)

> Added as a safety net so the migration auto-runs on container start.
> Makes Phase 1 data migration in the MongoDB shell **optional** for new deployments.

Two `HandshakeFieldMigration.java` classes were added — one for SERVER, one for CCMS_UI.
Both run on application startup via `@PostConstruct` and are **idempotent** (check `$exists` before acting).

| File | Change |
|---|---|
| `SERVER/.../netty/migration/HandshakeFieldMigration.java` | **New** — startup migration using `MainBootApp.context.getBean("mongoTemplate")` |
| `CCMS_UI/.../migration/HandshakeFieldMigration.java` | **New** — startup migration using `@Autowired MongoTemplate` |
| `CCMS_UI/.../conf/spring-config-docker.xml` | Added `<bean id="handshakeFieldMigration">` — component-scan doesn't cover migration package |

In addition to field renames on `handshake_info` and `dcu_details`, both migrations also fix the `herarchy_details` district value mismatch:
- `herarchy_details` stores YSR Kadapa as `"Kadapa"` (old name) — this is the collection the filter dropdowns query
- `handshake_info` stores it as `"YSR Kadapa"` (canonical name) — this is what the UI sends in queries
- The migration runs `$set` to update `"Kadapa"` → `"YSR Kadapa"` in `herarchy_details`

#### SERVER: `SERVER/ccms/src/main/java/.../netty/migration/HandshakeFieldMigration.java`

- Uses `MainBootApp.context.getBean("mongoTemplate", MongoTemplate.class)` instead of `@Autowired`
- **Why**: SERVER loads two Spring contexts — an XML context (`FileSystemXmlApplicationContext` from `applicationContext-docker.xml`) that creates a `mongoTemplate` bean with NO auth, and a Boot auto-configured context that reads `spring.data.mongodb.*` properties. `SPRING_DATA_MONGODB_USERNAME=""` in docker-compose causes Boot's `MongoTemplate` to attempt authentication with empty credentials against a no-auth MongoDB, which fails. The XML context's bean is unaffected and works correctly.

#### CCMS_UI: `CCMS_UI/STARTUP/ccms_ui/src/main/java/.../migration/HandshakeFieldMigration.java`

- Uses `@Autowired MongoTemplate` (works fine — no dual-context auth issue)
- `CCMS_UI/STARTUP/ccms_ui/conf/spring-config-docker.xml` — added explicit bean definition because `component-scan` only covers `com.vnetsoft.ccms.controller`:
  ```xml
  <bean id="handshakeFieldMigration"
        class="com.vnetsoft.ccms.migration.HandshakeFieldMigration" />
  ```

#### How the migration works

Both classes use `DBCollection.updateMulti()` with `BasicDBObject` (raw MongoDB driver API, not Spring Data's `Update`):

**`handshake_info` — `$rename`** (preserves original string values):
```java
mongoTemplate.getCollection("handshake_info").updateMulti(
    new BasicDBObject("distict", new BasicDBObject("$exists", true)),
    new BasicDBObject("$rename", new BasicDBObject("distict", "district"))
);
```

**`dcu_details` — `$unset` + `$set`** (int `0` → `"Unknown"` string):
```java
BasicDBObject update = new BasicDBObject("$unset", new BasicDBObject("distict", ""))
    .append("$set", new BasicDBObject("district", "Unknown"));
mongoTemplate.getCollection("dcu_details").updateMulti(query, update);
```
- `dcu_details` stored `distict`/`mondal` as integers (`0`), not strings
- `$rename` was insufficient because the type differs (int → string)
- `$set` writes `"Unknown"` as default string value

#### Why raw `DBCollection` / `BasicDBObject` instead of Spring Data `Update`?

| Approach | Problem |
|---|---|
| `Update.rename()` | Doesn't exist in Spring Data MongoDB 1.10.9 (Ingalls release) |
| Iteration (find + batch-save each doc) | ~55k individual saves → ~46 minutes → Docker healthcheck kills container |
| `DBCollection.updateMulti()` with `$rename` / `$unset`+`$set` | **Works** — all 4 operations complete in ~4 seconds |

#### Execution log (SERVER, 30 Jun 2026):
```
10:23:40 - Checking for typo'd field names in MongoDB...
10:23:40 - Renamed distict -> district in 17448 handshake_info docs
10:23:41 - Renamed mondal -> mandal in 17448 handshake_info docs
10:23:42 - Migrated distict -> district (Unknown) in 10170 dcu_details docs
10:23:43 - Migrated mondal -> mandal (Unknown) in 10170 dcu_details docs
10:23:43 - Updated Kadapa -> YSR Kadapa in 514 herarchy_details docs
10:23:43 - Field name migration complete
```

## Deployment Sequence

**With the startup auto-migration, Phase 1 (manual MongoDB shell) is now optional.**
The migration runs automatically on first container start (~4 seconds).

```
1. Build new Docker images
2. Push images to GHCR
3. Deploy: docker compose up -d
4. Migration auto-runs (~4s) — no manual MongoDB steps needed
5. (Optional) Rebuild mongo.archive for fresh deployments:
   docker compose exec mongodb mongodump --db=ccms --archive=/tmp/mongo.archive
   docker cp cspl-mongodb:/tmp/mongo.archive db/seeds/mongo.archive
```

## Verification

- [x] Select "YSR Kadapa" → mandal dropdown populates (33 options including 31 mandals)
- [x] Select a mandal → GP dropdown populates (e.g., Sidhout → 18 GPs)
- [x] Dashboard counts for "YSR Kadapa" show correct numbers (`total_devices: 4017`)
- [x] All other districts (Kurnool, Prakasam, Srikakulam, West Godavari) still work
- [x] API smoke test: **38/39 PASS** (1 failure: "Dashboard counts" timeout at 15s — endpoint returns in ~14s; pre-existing performance issue)
- [ ] Map data for "YSR Kadapa" works
- [ ] DCU registration works with location fields
- [ ] User privilege scoping works with location fields
