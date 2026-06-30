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

_Not tracked in Git — run on production before code deployment._

```javascript
// 1. Fix Kadapa value in herarchy_details
db.herarchy_details.updateMany(
  {"district": "Kadapa"}, {$set: {"district": "YSR Kadapa"}}
)

// 2. Rename fields in handshake_info
db.handshake_info.updateMany(
  {}, {$rename: {"distict": "district", "mondal": "mandal"}}
)

// 3. Rename fields in dcu_details
db.dcu_details.updateMany(
  {}, {$rename: {"distict": "district", "mondal": "mandal"}}
)

// 4. Rename fields in ccms_user_details
db.ccms_user_details.updateMany(
  {}, {$rename: {"dist": "district", "mondal": "mandal"}}
)
```

After migration, rebuild `mongo.archive`:
```bash
docker compose exec mongodb mongodump --db=ccms --archive=/tmp/mongo.archive
docker cp <container>:/tmp/mongo.archive db/seeds/mongo.archive
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

## Deployment Sequence

**Required — data migration must precede code deployment.**

```
1. Stop ALL CCMS containers (server + ccms_ui)
2. Run MongoDB rename/value migrations (Phase 1)
3. Build new Docker images
4. Deploy new images with docker compose up -d
5. Rebuild mongo.archive and commit to repo
```

## Verification

- [ ] Select "YSR Kadapa" → mandal dropdown populates
- [ ] Select a mandal → GP dropdown populates
- [ ] Dashboard counts for "YSR Kadapa" show correct numbers
- [ ] Map data for "YSR Kadapa" works
- [ ] All other districts (Kurnool, Prakasam, Srikakulam, West Godavari) still work
- [ ] DCU registration works with location fields
- [ ] User privilege scoping works with location fields
- [ ] API smoke test passes
