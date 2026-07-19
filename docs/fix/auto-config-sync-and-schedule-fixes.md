# Auto Config Sync & Schedule Fixes

## Bugs Fixed

### 1. Node Page Crashes on Backend Exception

**Root Cause:** `NodeController.getAll()` (`CCMS_UI/.../controller/NodeController.java:46`) returned `null` when `nodeServices.getEntityList()` threw an exception. The AngularJS frontend then called `.slice()` on `null`.

**Fix:** Return `Collections.emptyList()` instead of `null` in the catch block.

### 2. NodeDaoImpl Fails When MySQL Is Down

**Root Cause:** `NodeDaoImpl` (`CCMS_UI/.../dao/NodeDaoImpl.java:32`) had `@Autowired SessionFactory` even though the class only uses MongoDB. When MySQL container was unhealthy, Spring failed to create the bean, breaking Node page.

**Fix:** Removed unused `@Autowired SessionFactory`, `Session`, and `Transaction` fields, plus unused Hibernate imports.

**Same issue in** `DCUDaoImpl.java:45` — identical unused `SessionFactory` removed.

### 3. Hardcoded Schedule Name in DeviceConfigurationController

**Root Cause:** `syncSchedulerConfigurations()` (`CCMS_UI/.../controller/DeviceConfigurationController.java:148`) queried `getSchedulerConfigurationById("schedule_1")` instead of using the actual schedule name associated with the DCU.

**Fix:** Changed to `userServices.getSchedulerConfigurationById(hand_shake.getSchedules_name())`.

### 4. Hardcoded Total File Size in ScheduleConfProcessor

**Root Cause:** `getSchedulerConfigurationFileInitResponse()` (`SERVER/.../schedule/ScheduleConfProcessor.java:107`) sent hardcoded `0xAB` (171 B) as the total file size. Each per-record size is 93 B, so 3 records need 279 B (`0x117`). The truncated size caused RTU to request fewer bytes than needed.

**Fix:** Accept schedule data as a parameter and calculate `totalFileSize = data.length() / 2` (hex to bytes).

### 5. Hardcoded Zero-Byte Response Offset in ScheduleConfProcessor

**Root Cause:** `getSchedulerConfZeroByteResponse()` sent `000000AB` as the chunk offset instead of the actual file size.

**Fix:** Accept `totalFileSize` parameter and use it as the offset.

### 6. Schedule Data Not Passed to Init Response Builder

**Root Cause:** `ConfigurationINITHandler.java:100` called `getSchedulerConfigurationFileInitResponse(obj)` without passing the actual schedule configuration data, so file size could not be computed dynamically.

**Fix:** Pass `sche_conf_change_request.getData()` as second argument.

### 7. No Auto-Sync on Node Add/Delete

**Root Cause:** Adding or deleting a node did not trigger sync to DCUs, requiring manual per-DCU sync.

**Fix:** Added `triggerNodeConfigSync()` to `NodeController.add()` and `delete()`, calling the new `sync_node_conf_all` endpoint via `RestTemplate`.

### 8. No Auto-Sync on Schedule Save

**Root Cause:** Saving a schedule did not push updates to DCUs using that schedule.

**Fix:** Added `triggerScheduleSync()` to `SchedulerController.add()` — looks up all DCUs by `schedules_name` and triggers per-DCU sync via `RestTemplate`.

### 9. No Bulk Sync-All Buttons

**Root Cause:** No UI to trigger config sync for all DCUs at once.

**Fix:** Added `syncAllNodeConfig()` and `syncAllSchedulerConfig()` methods on both frontend (factory + controller) and backend endpoints (`sync_node_conf_all`, `sync_schduler_conf_all`).

### 10. Typo in node-controllers.js

**Root Cause:** `$scope.delete` used `userFactory` instead of `nodeFactory`, causing delete to silently fail.

**Fix:** Changed to `nodeFactory.delete(id)`.

### 11. Missing Error Handling on Node Add/Update

**Root Cause:** `nodeFactory.add()` calls had no `.catch()`, so errors went silently unnoticed.

**Fix:** Added `.catch()` handlers that log errors to console.

### 12. getSchedulerConfigurationById Null Safety

**Root Cause:** `DCUDaoImpl.getSchedulerConfigurationById()` returned `list.get(0)` unconditionally, which throws `IndexOutOfBoundsException` when no schedule matches.

**Fix:** Return `null` when `list.isEmpty()`.

## Files Changed

### CCMS UI (Java)

| File | Change |
|---|---|
| `.../controller/NodeController.java` | Fixed null return in `getAll()`, added `triggerNodeConfigSync()` |
| `.../controller/DeviceConfigurationController.java` | Fixed schedule name lookup, added `syncNodeConfigToAllDCUs()`, `syncSchedulerConfigToAllDCUs()` |
| `.../controller/SchedulerController.java` | Added `triggerScheduleSync()` |
| `.../dao/NodeDaoImpl.java` | Removed unused `SessionFactory` |
| `.../dao/DCUDaoImpl.java` | Removed unused `SessionFactory`, added `findHandShakeBySchedulesName()`, null-safe `getSchedulerConfigurationById()` |
| `.../dao/DCUDao.java` | Added `findHandShakeBySchedulesName()` |
| `.../services/DCUServices.java` | Added `findHandShakeBySchedulesName()` |
| `.../services/DCUServicesImpl.java` | Added `findHandShakeBySchedulesName()` pass-through |

### CCMS UI (Frontend)

| File | Change |
|---|---|
| `.../webapp/app/node/node-controllers.js` | Fixed `userFactory` → `nodeFactory`, added `.catch()` handlers |
| `.../webapp/app/dcu/dcu-factory.js` | Added `syncAllNodeConf()`, `syncAllSchedulerConf()` |
| `.../webapp/app/dcu/dcu-controllers.js` | Added `$scope.syncAllNodeConfig()`, `$scope.syncAllSchedulerConfig()` |
| `.../webapp/app/dcu/dcu-list.html` | Added "Sync All Node Config" and "Sync All Schedule Config" buttons |

### SERVER

| File | Change |
|---|---|
| `.../netty/push/ConfigurationINITHandler.java` | Pass schedule data to `getSchedulerConfigurationFileInitResponse()` |
| `.../netty/push/ConfigurationDownloadHandler.java` | Pass total file size to `getSchedulerConfZeroByteResponse()` |
| `.../netty/push/schedule/ScheduleConfProcessor.java` | Dynamic file size calculation, signature changes |

## Auto-Sync Flow

### Node Auto-Sync

```
NodeController.add(obj)
  → save to MongoDB
  → triggerNodeConfigSync()
    → RestTemplate GET http://localhost:8080/device_conf/sync_node_conf_all
      → DeviceConfigurationController.syncNodeConfigToAllDCUs()
        → for each HandShake: syncNodeConfigurations(dcuId)
```

### Schedule Auto-Sync

```
SchedulerController.add(obj)
  → save to MongoDB
  → triggerScheduleSync(obj.getSchedules_name())
    → findHandShakeBySchedulesName(schedulesName)
    → for each matching HandShake:
      → RestTemplate GET /device_conf/sync_schduler_conf?id=<dcuId>
```

## New API Endpoints

| Endpoint | Method | Purpose |
|---|---|---|
| `/device_conf/sync_node_conf_all` | GET | Sync node config to ALL connected DCUs |
| `/device_conf/sync_schduler_conf_all` | GET | Sync schedule config to ALL connected DCUs |

## Verification

1. **Node page**: Navigate to DCU > Node — should load without JS errors
2. **Node add/delete**: Creates/deletes node and automatically triggers config push to all DCUs
3. **Schedule save**: Saves schedule and pushes to all DCUs referencing that schedule name
4. **Sync All buttons**: In DCU list page, "Sync All Node Config" and "Sync All Schedule Config" buttons push to all DCUs
5. **Schedule download**: Monitor RTU logs for successful schedule download (file size should no longer be truncated at 171 B)
6. **MySQL down**: Node and DCU pages should still load when MySQL is unavailable (MongoDB operations unaffected)
