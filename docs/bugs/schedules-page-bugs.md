# Schedules Page — Bug Report

> Generated from code review on 2026-07-21
> Scope: Full Schedules configuration stack (AngularJS frontend + Java Spring backend + MongoDB + Netty schedule sync)
> Covers: Schedule List page (`#/schedules`), Add Schedule page (`#/schedules_add`), Edit Schedule page (`#/schedules_edit`), and schedule sync operations

## Severity Legend

| Severity | Meaning |
|---|---|
| 🔴 Critical | Breaks core functionality, causes data loss, or makes the page non-functional |
| 🟠 Major | Causes incorrect behavior, silent failures, or data inconsistency |
| 🟡 Minor | Code quality, maintainability, or UX polish |

---

## Files Examined

| # | File Path | Role |
|---|---|---|
| 1 | `CCMS_UI/.../app/schedules/schedules-list.html` | Schedule list page template (98 lines) |
| 2 | `CCMS_UI/.../app/schedules/schedules-add.html` | Create schedule form template (727 lines) |
| 3 | `CCMS_UI/.../app/schedules/schedules-update.html` | Edit schedule form template (711 lines) |
| 4 | `CCMS_UI/.../app/schedules/schedules-controllers.js` | All schedule controllers (list, add, update, delete) |
| 5 | `CCMS_UI/.../app/schedules/schedules-factory.js` | Schedule API factory (29 lines) |
| 6 | `CCMS_UI/.../app/app.js` | State definitions (lines 252-284) |
| 7 | `CCMS_UI/.../app/navi/leftnavi.html` | Navigation menu (lines 219-222, 364-367) |
| 8 | `CCMS_UI/.../controller/SchedulerController.java` | Schedule CRUD REST controller (96 lines) |
| 9 | `CCMS_UI/.../controller/DeviceConfigurationController.java` | Config sync controller (589 lines) |
| 10 | `CCMS_UI/.../dao/DCUDaoImpl.java` | MongoDB DAO (schedule operations lines 481-513) |
| 11 | `CCMS_UI/.../dao/ControllerDaoImpl.java` | Duplicate MongoDB DAO (full file, 195 lines) |
| 12 | `CCMS_UI/.../pojo/SchedulerConfiguration.java` | Schedule MongoDB POJO (487 lines) |
| 13 | `CCMS_UI/.../services/DCUServices.java` | Service interface |
| 14 | `CCMS_UI/.../services/DCUServicesImpl.java` | Service implementation |
| 15 | `SERVER/.../netty/pojo/SchedulerConfiguration.java` | SERVER-side schedule POJO (522 lines) |
| 16 | `SERVER/.../netty/push/schedule/ScheduleConfProcessor.java` | Binary schedule protocol processor (186 lines) |
| 17 | `SERVER/.../netty/repos/DeviceRequestDataRepository.java` | MongoDB repo for schedule sync state |

---

## 🔴 Critical Bugs

### 1. `DCUDaoImpl.getSchedulerConfigurationById()` queries by `schedules_name`, not `_id`

**File:**
- `DCUDaoImpl.java:490-498`

**Description:**
```java
public SchedulerConfiguration getSchedulerConfigurationById(String id) throws Exception {
    Query query = new Query();
    query.addCriteria(Criteria.where("schedules_name").is(id));  // BUG: queries name field
    List<SchedulerConfiguration> list = mongoTemplate.find(query, SchedulerConfiguration.class);
    return list.isEmpty() ? null : list.get(0);
}
```

The method is named `getById` but queries the `schedules_name` field, not MongoDB's `_id` field. Meanwhile, `deleteSchedulerConfiguration()` (line 507-513) correctly queries by `_id`:

```java
query.addCriteria(Criteria.where("_id").is(Long.parseLong(id)));
```

This means:
- `getSchedulerConfigurationById("schedule_1")` works by accident (using the name as a lookup)
- But the same ID format is used in `delete()` where it's parsed as `Long` for `_id`
- If a document's `schedules_name` differs from its `_id`, get and delete operate on entirely different criteria

The sister DAO `ControllerDaoImpl.java:49-59` has the **correct** implementation:
```java
query.addCriteria(Criteria.where("_id").is(id));  // correct
```

**Fix:**
Decide whether to query by `_id` (consistent with delete) or rename the method to `getSchedulerConfigurationByName()`. `ControllerDaoImpl` has the correct approach — query by `_id`.

---

### 2. `triggerScheduleSync()` blocks the HTTP thread for all DCUs

**File:**
- `SchedulerController.java:50-61`

**Description:**
```java
private void triggerScheduleSync(String schedulesName) {
    List<HandShake> dcus = userServices.findHandShakeBySchedulesName(schedulesName);
    RestTemplate restTemplate = new RestTemplate();
    for (HandShake hs : dcus) {
        String uri = "http://localhost:8080/device_conf/sync_schduler_conf?id="
            + hs.getGateway_serial_number();
        restTemplate.getForObject(uri, String.class);  // synchronous HTTP call per DCU
    }
}
```

This method is called from `SchedulerController.add()` (line 43), which is a `POST /scheduler/create` handler. Every time a schedule is saved, the HTTP thread iterates **all** DCUs that use that schedule name and makes sequential blocking HTTP calls. In a deployment with hundreds of DCUs using the same schedule, the HTTP request can timeout before the sync completes. The user sees no error because the `add()` method catches all exceptions and returns a generic response.

Additionally, the URL hardcodes `localhost:8080` instead of using the proper service hostname and port.

**Fix:**
Use `@Async` or a thread pool to offload the sync operations. Inject server host/port via `@Value`.

---

### 3. Save/Update/Delete all call `$state.reload()` before async API calls complete

**Files:**
- `schedules-controllers.js:449-451` (Add `ok()`)
- `schedules-controllers.js:862-865` (Update)
- `schedules-controllers.js:875-878` (Delete)

**Description:**
All three operations fire `$state.reload()` synchronously after queuing an async HTTP request:

```javascript
// Add (line 449-451):
schedulesFactory.add($scope.schedules);
$state.reload();
$state.go('dashboard.schedules');

// Update (line 862-865):
schedulesFactory.add($scope.schedules);  // uses add() for update too
$state.reload();
$state.go('dashboard.schedules');

// Delete (line 875-878):
schedulesFactory.delete(id);
$modalInstance.close($scope.schedules);
$state.reload();
```

The `$state.reload()` executes immediately after the HTTP request is queued but before the server responds. The page reloads with **old data**. The save/update/delete operations may not have persisted by the time the user sees the list page.

**Fix:**
Chain navigation in the `.then()` callback:
```javascript
schedulesFactory.add($scope.schedules).then(function() {
    $state.go('dashboard.schedules');
});
```

---

### 4. Empty catch block in `syncSchedulerConfigurations()`

**File:**
- `DeviceConfigurationController.java:167-169`

**Description:**
```java
} catch(Exception e){
    // empty — all errors silently swallowed
}
return new Status(200, "success");  // always returns "success"
```

The `sync_schduler_conf` endpoint catches all exceptions and does nothing with them. It always returns HTTP 200 "success". If the HandShake lookup fails, the schedule config is not in MongoDB, or the Netty push fails, the caller has no way to know.

**Fix:**
Log the exception and return an appropriate error status.

---

### 5. Duplicate DAO implementations with different query semantics

**Files:**
- `DCUDaoImpl.java:481-513`
- `ControllerDaoImpl.java:43-72`

**Description:**
Both `DCUDaoImpl` and `ControllerDaoImpl` implement the same four CRUD methods against the `scheduler_details` MongoDB collection, but with different query logic:

| Operation | `DCUDaoImpl` | `ControllerDaoImpl` |
|---|---|---|
| `getSchedulerConfigurationById()` | Queries by `schedules_name` (bug #1) | Correctly queries by `_id` |
| `addSchedulerConfiguration()` | Saves to `scheduler_details` | Saves to `scheduler_details` |
| `deleteSchedulerConfiguration()` | Parses ID as `Long` for `_id` | Uses ID as `String` for `_id` |
| `getSchedulerConfigurationList()` | `findAll()` | `findAll()` |

Only one DAO is wired via Spring config. The existence of two implementations with different query behavior creates a maintenance hazard — someone editing one may not know the other exists.

**Fix:**
Remove one implementation. Standardize on a single DAO class.

---

## 🟠 Major Bugs

### 6. Hardcoded port `8102` in config sync URL constructions

**File:**
- `DeviceConfigurationController.java:69, 208, 232, 252, 359`

**Description:**
All five URLs that push configuration to the SERVER hardcode port `8102`:
```java
sb.append("http://"+serverHost+":8102/user/push/sys_conf?dcu_id=")        // line 69
uri = "http://" + serverHost + ":8102/user/push/manuval_on?...";          // line 208
uri = "http://" + serverHost + ":8102/user/push/manuval_off?...";         // line 232
sb.append("http://"+serverHost+":8102/user/push/sync_scheduler_conf?...") // line 252
sb.append("http://"+serverHost+":8102/user/push/sync_node_conf?...")      // line 359
```

If `SERVER_PORT` is changed from the default `8102`, all config sync push requests silently fail.

**Fix:**
Inject the port via `@Value("${ccms.server.port:8102}")` and use it in all URL constructions.

---

### 7. Timezone selector uses wrong `ng-model`

**Files:**
- `schedules-add.html:47`
- `schedules-update.html:47`

**Description:**
```html
<select class="form-control " ng-model="schedule.timezoneid">
```
Note `schedule.timezoneid` (without the trailing 's'). The controller stores the form data as `$scope.schedules` (plural), so `$scope.schedule` is undefined. The timezone selection is **never bound** to the data model and is always `undefined` when the form is submitted.

**Fix:**
Change to `ng-model="schedules.timezoneid"`.

---

### 8. "Apply to Light" and "Apply to RTU/DCU" buttons call undefined functions

**File:**
- `schedules-list.html:81-85`

**Description:**
```html
<a type="button" ng-click="light();" ...>Apply to Light</a>
<a type="button" ng-click="rtu/dcu();" ...>Apply to RTU/DCU</a>
```

Neither `light()` nor `rtu/dcu()` is defined in `schedulesListControllers`. Clicking either button does nothing (Angular silently ignores undefined function calls in expression context, or throws a console error depending on the Angular version).

Also note: `rtu/dcu()` is not a valid JavaScript identifier due to the `/`.

**Fix:**
Define the functions in the controller and implement the sync logic, or remove the buttons if the feature is not ready.

---

### 9. Edit controller maps handle times with off-by-one shift

**File:**
- `schedules-controllers.js:477-481`

**Description:**
```javascript
$scope.handle_0_time = $scope.schedules.handle_1_time;
$scope.handle_1_time = $scope.schedules.handle_2_time;
$scope.handle_2_time = $scope.schedules.handle_3_time;
$scope.handle_3_time = $scope.schedules.handle_4_time;
$scope.handle_4_time = $scope.schedules.handle_5_time;  // handle_5_time doesn't exist
```

All five handle times are shifted by one index. Handle 0 gets handle 1's time, handle 1 gets handle 2's time, etc. Handle 4 is assigned from `handle_5_time` which does not exist on the model (there are only handles 0-4). This means:
- The first time slot always shows the wrong time (it shows handle 1's time)
- The last time slot shows `undefined`
- When the user saves, the wrong times are persisted

**Fix:**
Remove the off-by-one shift. Assign directly:
```javascript
$scope.handle_0_time = $scope.schedules.handle_0_time;
// ... etc
```

---

### 10. `enable_fault_detection` initialized to two conflicting values

**File:**
- `schedules-controllers.js:329-343`

**Description:**
In the default schedule object:
```javascript
"enable_fault_detection": false,    // line 332
// ...
"enable_fault_detection": 20,       // line 334 — overwrites line 332
```

The same property is set to `false` then immediately overwritten to `20` (a truthy number). The later declaration wins, so `enable_fault_detection` defaults to `20` — which is truthy — making the checkbox appear checked by default. The "Fault data collection interval" field depends on this checkbox being checked, but the initial value makes it look like fault detection is enabled when it may not be.

**Fix:**
Remove the duplicate. Keep only one declaration:
```javascript
"enable_fault_detection": false,
```

---

### 11. Pagination references `todos.length` but `$scope.todos` is never defined

**File:**
- `schedules-list.html:94-96`

**Description:**
```html
<pagination boundary-links="true" max-size="3"
    items-per-page="itemsPerPage" total-items="todos.length"
    ng-model="currentPage" ng-change="pageChanged()"></pagination>
```

The pagination directive reads `todos.length` for `total-items`, but `$scope.todos` is never defined in the controller — only `$scope.listData` is set. The pagination displays `0` total items, showing no pages regardless of how many schedules exist.

Additionally, `itemsPerPage` and `currentPage` and `pageChanged()` are never defined in the controller. The pagination is completely non-functional.

**Fix:**
Set `total-items="listData.length"` and implement `currentPage`, `itemsPerPage`, and `pageChanged()`.

---

### 12. `schedulesFactory.add()` is used for both create and update

**File:**
- `schedules-controllers.js:449` (add), `schedules-controllers.js:862` (update)

**Description:**
Both the Add controller's `ok()` and the Update controller's `update()` call `schedulesFactory.add()`, which POSTs to `/scheduler/create`. The backend `SchedulerController.add()` method handles both creation and update (it checks `scheduleId < 1` to decide), but using the same factory method named `add` for updates is misleading and increases the risk of data overwrite.

Additionally, `$scope.schedules` in the update controller is assigned directly from `$stateParams.schedule` (line 461), which means the update controller mutates the passed object directly. If the same object reference exists elsewhere, it gets corrupted.

**Fix:**
Add a separate `schedulesFactory.update()` method that PUTs to `/scheduler/update` and a dedicated backend endpoint.

---

### 13. `$scope.schedules.handle_3_val` is missing from the default model

**File:**
- `schedules-controllers.js:329-376`

**Description:**
The default schedule initializer defines `handle_0_val` through `handle_4_val` implicitly via the `ok()` function's form reading. But there is no property like `schedul_0_dim_value` initialization. The dim values are never initialized to a safe default, so if a handle has no status set, it stores `undefined`.

More critically, `handle_3_apply_sunrise_sunset` is properly initialized (line 338), but `handle_3_val` is not initialized anywhere in the default object.

**Fix:**
Initialize all dim values to a safe default (e.g., 0) in the default schedule object.

---

### 14. `$scope.delete()` bypasses confirmation modal

**File:**
- `schedules-controllers.js:19-21`

**Description:**
```javascript
$scope.delete = function(id){
    schedulesFactory.delete(id);  // directly deletes without confirmation
};
```

There is a separate `deleteconf()` function (line 23) that opens a confirmation modal. The list template calls `deleteconf()` (via `ng-click="deleteconf(obj.scheduleId)"`), so the unsafe `delete()` is not currently reachable from the UI. But if any future code path invokes it, the schedule is deleted without confirmation.

**Fix:**
Remove the unsafe `delete()` function or make it private.

---

## 🟡 Minor / Code Quality Issues

### 15. Typo `schedul_0_dim_value` through `schedul_4_dim_value`

**Files:**
- `schedules-add.html:248, 342, 444, 551, 647`
- `schedules-update.html:242, 332, 430, 536, 632`
- `SchedulerConfiguration.java` (POJO field)

All instances use `schedul_` (missing 'e') instead of `schedule_`. The field is `schedul_0_dim_value` but should be `schedule_0_dim_value`. Inconsistent with all other fields which use the full `schedule` prefix.

---

### 16. Typo `schduler` in URL mappings

**Files:**
- `DeviceConfigurationController.java:135`
- `dcu-factory.js`

The URL path uses `sync_schduler_conf` and `sync_schduler_conf_all` (missing 'e' in scheduler). This makes API discovery inconsistent with the rest of the codebase.

---

### 17. `scheduleId` generation uses `System.currentTimeMillis()` — collision risk

**File:**
- `SchedulerController.java:39-40`

```java
if(obj.getScheduleId() < 1)
    obj.setScheduleId(System.currentTimeMillis());
```

Under high concurrency, two requests in the same millisecond can get the same ID. MongoDB will overwrite the existing document with the same `_id`. Use `UUID.randomUUID()` or a sequence-based ID generator.

---

### 18. `getByID` constructs URL without a slash separator

**File:**
- `schedules-factory.js:11-12`

```javascript
obj.getByID = function(customerID){
    return $http.get(serviceBase + '/scheduler/list' + customerID);
}
```

The URL becomes `/scheduler/list123` instead of the intended `/scheduler/list/123`. This method is also never called from any controller — dead code.

---

### 19. "Mater data" typo in both add and update templates

**Files:**
- `schedules-add.html:160`
- `schedules-update.html:160`

```html
<label class="col-md-2 control-label formTextWeight">Mater data interval collection : </label>
```

Should be "Master data interval collection".

---

### 20. "Summary" column shows only handle_0 and handle_1 regardless of time slot count

**File:**
- `schedules-list.html:71`

```html
<td align="center">
    <b>OFF @&nbsp;{{ obj.handle_0_time }}</b><br>
    <b>ON @ &nbsp;{{ obj.handle_1_time }}</b>
</td>
```

The summary column always shows exactly two entries (handle_0 and handle_1), even when a schedule has 3, 4, or 5 time slots. Users cannot see the full schedule from the list view.

---

### 21. "Create Daily Schedule" heading on edit page

**File:**
- `schedules-update.html:5-6`

```html
<div align="left" style="font-size: 30px; color: #c8ea4d; padding-top: 10px">
    Create Daily Schedule
</div>
```

The edit page displays "Create Daily Schedule" instead of "Update Daily Schedule" or "Edit Daily Schedule".

---

### 22. `handle__sunrise_offset` with double underscore in slot 3

**Files:**
- `schedules-add.html:490`
- `schedules-update.html:476`

```html
<input type="text" class="form-control"
    ng-model="schedules.handle__sunrise_offset"  <!-- double underscore, missing '3' -->
    ng-disabled="schedules.handle_2_apply_sunrise_sunset==false">
```

The ng-model is `handle__sunrise_offset` (double underscore, no slot number) instead of `handle_2_sunrise_offset`. This field's value is never saved because it doesn't match any POJO field. The off-by-one pattern persists: it's slot 2 (third time slot) but the ng-disabled reads `handle_2_apply_sunrise_sunset`.

---

### 23. Fault detection interval has both `ng-disabled` and `disabled="disabled"`

**Files:**
- `schedules-add.html:181-182`
- `schedules-update.html:181-182`

```html
<input type="text" class="form-control" min="1" max="1440"
    ng-disabled="schedules.enable_fault_detection==false"
    ng-model="schedules.fault_detection" disabled="disabled">
```

The field is permanently disabled by the static `disabled="disabled"` attribute, regardless of the `enable_fault_detection` checkbox state. The `ng-disabled` directive is overridden by the HTML attribute.

**Fix:**
Remove `disabled="disabled"` and rely on `ng-disabled` alone.

---

### 24. `scheduleone()` through `schedulefive()` are duplicated in both add and edit controllers

**Files:**
- `schedules-controllers.js:379-417` (add controller)
- `schedules-controllers.js:803-826` (update controller)

Five identical functions (`scheduleone`, `scheduletwo`, `schedulethree`, `schedulefour`, `schedulefive`) are declared in both controllers. These could be shared via a service or inherited scope.

---

### 25. No loading indicators on save, update, delete, or sync operations

**Files:**
- All schedule templates and controllers

None of the operations show a spinner or progress indicator. The page appears unresponsive during API calls, especially the save operation which triggers synchronous DCU syncs.

---

### 26. `leftnavi.html` has Schedule in two menu sections

**File:**
- `leftnavi.html:219-222` (Configuration dropdown)
- `leftnavi.html:364-367` (Settings dropdown)

The Schedule menu item appears in both the Configuration and Settings sections of the left navigation. Both point to the same `#/schedules` URL. This is redundant and confusing.

---

### 27. `update()` function in edit controller does not set `schedules.handle_0_time` through `handle_4_time`

**File:**
- `schedules-controllers.js:828-866`

The `update()` function reads time values from the DOM and sets `$scope.handle_0_time` through `handle_4_time`, and then sets `$scope.schedules.handle_0_time` through `handle_4_time` (lines 842-846). But `$scope.schedules` already has the old `handle_*_time` values from the state params. If the slider update event hasn't fired (user didn't drag the slider), the DOM values may be stale or formatted as `HH:MM` strings rather than numeric minutes. The `split(':')` conversion in the slider event handler (lines 486-516) converts these correctly, but only when the slider is explicitly moved.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 5 | `getById` queries wrong field, sync blocks HTTP thread, reload before async completes, empty catch in sync, duplicate DAOs with different semantics |
| 🟠 Major | 9 | Hardcoded port 8102, wrong ng-model on timezone, undefined functions for apply buttons, off-by-one handle time mapping in edit, duplicate enable_fault_detection init, broken pagination, add used for update, missing dim value init, unsafe delete function |
| 🟡 Minor | 13 | sched_ul_ typo, schduler typo, ID collision, missing slash in URL, "Mater" typo, limited summary display, wrong edit heading, double underscore in ng-model, permanently disabled field, duplicated functions, no loading indicators, dual menu entries, stale DOM values |

### Recommended Fix Priority

1. **Critical bug #1** — `getSchedulerConfigurationById` returns wrong documents; can lead to incorrect data being edited or deleted.
2. **Critical bug #3** — Save/Update/Delete page reload means the user sees stale data and changes may not persist.
3. **Critical bug #4** — Silent failures on sync operations mislead users into thinking operations succeeded.
4. **Critical bug #2** — HTTP thread blocks for all DCUs; can timeout in production.
5. **Major #8** — "Apply to Light" and "Apply to RTU/DCU" buttons are non-functional.
6. **Major #9** — Edit page shifts all handle times by 1; data corruption on every edit.
7. **Major #7** — Timezone selection is silently discarded.

### Related Bugs

This page shares the **hardcoded port 8102** issue with DCU/RTU Config bug #4 (same coupling problem, same controller — `DeviceConfigurationController.java`). Both need to be fixed together if `SERVER_PORT` changes.

The **empty catch block** in `syncSchedulerConfigurations()` mirrors the same pattern in DCU/RTU Config bug #3. Both are in `DeviceConfigurationController.java` and should be fixed together.
