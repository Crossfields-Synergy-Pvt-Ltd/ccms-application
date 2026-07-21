# DCU/RTU Configuration Page — Bug Report

> Generated from code review on 2026-07-21
> Scope: Full DCU/RTU configuration stack (AngularJS frontend + Java Spring backend + MongoDB + Netty device push)
> Covers: DCU List page, DCU System Configuration modify page, Default DCU Config page, and all config sync operations

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
| 1 | `CCMS_UI/.../app/dcu/dcu-modify.html` | DCU System Configuration form template (1657 lines) |
| 2 | `CCMS_UI/.../app/dcu/dcu-list.html` | DCU/RTU list page template |
| 3 | `CCMS_UI/.../app/dcu/dcu-controllers.js` | All DCU controllers (list, modify, add, update, delete) |
| 4 | `CCMS_UI/.../app/dcu/dcu-factory.js` | DCU API factory |
| 5 | `CCMS_UI/.../app/dcu/conf-sync-details.html` | Config sync details modal |
| 6 | `CCMS_UI/.../app/default/default-add.html` | Default DCU Config template |
| 7 | `CCMS_UI/.../app/default/default-controllers.js` | Default DCU Config controller |
| 8 | `CCMS_UI/.../app/default/default-factory.js` | Default DCU Config factory |
| 9 | `CCMS_UI/.../app/app.js` | State definitions (lines 159-203, 346-355) |
| 10 | `CCMS_UI/.../app/navi/leftnavi.html` | Navigation menu (lines 207-255) |
| 11 | `CCMS_UI/.../controller/DeviceConfigurationController.java` | Config sync REST controller |
| 12 | `CCMS_UI/.../controller/DCUController.java` | DCU CRUD + config retrieval |
| 13 | `CCMS_UI/.../controller/DefultConfigurationsContrller.java` | Default config controller |
| 14 | `CCMS_UI/.../pojo/DCUConfiguration.java` | DCU configuration MongoDB POJO |
| 15 | `CCMS_UI/.../services/DCUServices.java` | Service interface |
| 16 | `CCMS_UI/.../services/DCUServicesImpl.java` | Service implementation |
| 17 | `CCMS_UI/.../dao/DCUDaoImpl.java` | MongoDB DAO |
| 18 | `SERVER/.../controller/UIConfController.java` | Server-side config push receiver |
| 19 | `SERVER/.../netty/push/ConfigurationINITHandler.java` | Netty config init handler |
| 20 | `SERVER/.../netty/push/ConfigurationDownloadHandler.java` | Netty config download handler |
| 21 | `SERVER/.../netty/push/dcu/DCUConfiProcessor.java` | Binary config protocol processor |

---

## 🔴 Critical Bugs

### 1. `$scope` is overwritten with a Promise in `apply()` — destroys Angular scope

**File:**
- `dcu-controllers.js:392-397`

**Description:**
```javascript
$scope.apply = function() {
    $scope.dcu.dcu_id = $scope.gateway_serial_number;
    $scope = dcuFactory.modifysystemconfig($scope.dcu);  // BUG: $scope ← Promise
    $state.reload();
    $state.go('dashboard.dcu');
};
```

`modifysystemconfig()` returns a **Promise** (from `$http.post().then()`). Assigning it to `$scope` replaces the Angular Scope object with a plain Promise, **destroying the scope**. This breaks:
- Angular's digest cycle and dirty-checking
- All data bindings on the template
- The subsequent `$state.reload()` and `$state.go()` calls execute on a destroyed scope
- Navigation may fail silently, leaving the user on a broken page

**Fix:**
```javascript
$scope.apply = function() {
    $scope.dcu.dcu_id = $scope.gateway_serial_number;
    dcuFactory.modifysystemconfig($scope.dcu).then(function() {
        $state.go('dashboard.dcu');
    });
};
```

---

### 2. `loadDefualtconfigsettings()` and `default-controllers.js:apply()` reload the page before async API calls complete

**Files:**
- `dcu-controllers.js:404-408`
- `default-controllers.js:21-26`

**Description:**
Both functions fire a `$state.reload()` synchronously after initiating an async HTTP call, ignoring the returned promise:

```javascript
// dcu-controllers.js
$scope.loadDefualtconfigsettings = function(dcu_id) {
    $scope.dcu_id = dcu_id;
    dcuFactory.load_default_conf($scope.dcu_id);  // async — promise discarded
    $state.reload();  // fires BEFORE the request completes
};

// default-controllers.js
$scope.apply = function(dcu) {
    $scope.config = dcu;
    defaultFactory.add($scope.config);  // async — promise discarded
    $state.reload();  // fires BEFORE the save completes
};
```

The `$state.reload()` executes immediately after the HTTP request is queued but before the server responds. The page reloads with the **old data** still in place. The user sees a flash/refresh but their changes may not have been persisted.

**Fix:**
Chain the reload in the `.then()` callback:
```javascript
dcuFactory.load_default_conf(dcu_id).then(function() {
    $state.reload();
});
```

---

## 🟠 Major Bugs

### 3. "Sync All" operations have no confirmation and silently swallow errors

**Files:**
- `dcu-controllers.js:136-146`
- `DeviceConfigurationController.java:126-128, 167-169`

**Description:**
The "Sync All Node Config" and "Sync All Scheduler Config" buttons trigger operations on **every DCU** without any confirmation dialog. In a deployment with hundreds of DCUs, this floods the network with sync requests.

Additionally, the backend catch blocks are completely empty:
```java
// DeviceConfigurationController.java lines 126-128, 167-169
} catch(Exception e) {
    // empty — all errors silently swallowed
}
return new Status(200, "success");  // always returns success
```

The controller returns HTTP 200 "success" regardless of whether the operation worked. The frontend shows no feedback either way.

**Fix:**
Add a confirmation modal before "Sync All" operations. Add proper error logging and propagate errors to the frontend.

---

### 4. Hardcoded port `8102` in config sync URL constructions

**File:**
- `DeviceConfigurationController.java:69, 186, 219, 249, 362`

**Description:**
All five URLs that push configuration to the SERVER hardcode port `8102`:
```java
sb.append("http://"+serverHost+":8102/user/push/sys_conf?...");
sb.append("http://"+serverHost+":8102/user/push/sync_node_conf?...");
sb.append("http://"+serverHost+":8102/user/push/manuval_on?...");
sb.append("http://"+serverHost+":8102/user/push/manuval_off?...");
sb.append("http://"+serverHost+":8102/user/push/sync_scheduler_conf?...");
```

If `SERVER_PORT` is changed from the default `8102` (see AGENTS.md config coupling map), all config sync push requests to the SERVER fail silently. Same pattern as Monitor & Control bug #11.

**Fix:**
Inject the port via `@Value("${ccms.server.port:8102}")` and use it in all URL constructions.

---

### 5. No user feedback on any sync or config operation

**Files:**
- `dcu-controllers.js:119-146, 392-397, 404-408`
- `default-controllers.js:21-26`

**Description:**
None of the following operations show success/failure feedback to the user:

| Operation | `.catch()`? | User Feedback |
|---|---|---|
| `syncnodeconfig()` | No | None |
| `syncsheduleconfig()` | No | None |
| `syncAllNodeConfig()` | No | None |
| `syncAllSchedulerConfig()` | No | None |
| `loadDefualtconfigsettings()` | No | None |
| `apply()` (modify config) | No | None |
| `apply()` (default config) | No | None |

The user clicks a button and sees no indication of whether the operation succeeded or failed.

---

### 6. Default config uses hardcoded MongoDB ID `"100"`

**File:**
- `DefultConfigurationsContrller.java:33, 47, 66`

**Description:**
```java
obj = userServices.getDCUConfigurationByID("100");
obj.setDcu_id("100");
userServices.addDCUConfiguration(obj);
```

The default configuration is always stored and retrieved with MongoDB `_id = "100"`. If another document with this ID exists in the `dcu_system_conf_details` collection (e.g., from a real DCU with serial number "100"), it will be **overwritten**. The hardcoded ID is a collision risk.

**Fix:**
Use a distinct ID prefix or a separate collection for default configurations.

---

### 7. `delete()` bypasses confirmation modal

**File:**
- `dcu-controllers.js:147-149`

**Description:**
```javascript
$scope.delete = function(id) {
    dcuFactory.delete(id);  // directly deletes without confirmation
};
```

There is a separate `deleteconf()` function (line 151) that opens a confirmation modal before deleting. But `delete()` calls the API directly. No button in the current template calls `delete()`, but if any future code path invokes it, the DCU is deleted without confirmation.

---

### 8. `dcu-list.html` sort column maps `gateway_identifier` but data binding uses `gateway_serial_number`

**File:**
- `dcu-list.html:60, 87`

**Description:**
```html
<!-- Sort header (line 60): -->
ng-click="sortType = 'gateway_identifier'; sortReverse = !sortReverse"> Serial Number

<!-- Data cell (line 87): -->
{{ obj.gateway_serial_number }}
```

The sort field is `gateway_identifier` but the data field is `gateway_serial_number`. Clicking the "Serial Number" header to sort will look for a field `gateway_identifier` that does not exist on the data objects. The sort has no visible effect.

**Fix:**
Change to `sortType = 'gateway_serial_number'`.

---

### 9. Pagination functions defined in three duplicate blocks

**File:**
- `dcu-controllers.js:31-41, 53-63, 82-92`

**Description:**
Three identical copies of `figureOutTodosToDisplay()` and `pageChanged()` are defined inside three separate promise callbacks (`getAllHandShake`, `getByID`, and `search`). Every time a search or filter runs, new function objects are created. Same pattern as Events/History pages.

---

### 10. No null guard on `conf-sync-details` modal data

**File:**
- `dcu-controllers.js:190-193`

**Description:**
```javascript
dcuFactory.getAllConfSyncStatus($scope.gateway_serial_number).then(function(data) {
    $scope.listData = data.data;
});
```

`$scope.listData` is used in `conf-sync-details.html` with `ng-repeat` in a table. If the API returns `null` or a non-array, the `ng-repeat` throws an error and the modal displays nothing.

---

## 🟡 Minor / Code Quality Issues

### 11. Typo in Java class name — `DefultConfigurationsContrller`

**File:**
- `DefultConfigurationsContrller.java:1`

Both "Default" and "Controller" are misspelled in the class name — should be `DefaultConfigurationsController`. This affects readability and discoverability.

---

### 12. Typo in tooltip text — "Configuation"

**File:**
- `dcu-list.html:102`

```html
title="Modify DCU Configuation"
```
Should be "Configuration".

---

### 13. `$stateParams.gateway_serial_number` is read but never used

**File:**
- `dcu-controllers.js:6`

```javascript
$scope.gateway_serial_number = $stateParams.gateway_serial_number;
```

This value is assigned to `$scope` but never referenced again in the `dcuListControllers`. Dead code.

---

### 14. `dcu.dcu_id` is set redundantly

**File:**
- `dcu-controllers.js:388, 393`

```javascript
// line 388 — already set in the init callback
$scope.dcu.dcu_id = $scope.gateway_serial_number;
// ...
// line 393 — set again in apply()
$scope.dcu.dcu_id = $scope.gateway_serial_number;
```

The second assignment is redundant.

---

### 15. Dead factory method `getAll()`

**File:**
- `dcu-factory.js:8-10`

```javascript
obj.getAll = function() {
    return $http.get(serviceBase + '/dcu/list/');
}
```

Calls `/dcu/list/` but is never invoked from any controller. Dead code.

---

### 16. No range validation on critical configuration fields

**File:**
- `dcu-modify.html`

Only `change_interval` has a `pattern` validator with a range hint ("Range is between 5 to 255"). Most fields lack device-constraint validation:

| Field | Acceptable Range (Device) | Validation |
|---|---|---|
| RF frequency | 865–868 MHz | None |
| TX power | 0–15 dBm | None |
| Baud rates | 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200 | None |
| Voltage min/max | 0–500 V | None |
| Current min/max | 0–999 A | None |

Users can submit values the device will reject, and the error only surfaces after the config is pushed to the field device.

---

### 17. Primary mobile number field is disabled with no explanation

**File:**
- `dcu-modify.html:75`

```html
<input type="text" ... ng-model="dcu.mobile_no" disabled="">
```

The "Mobile number" field is disabled. Users cannot enter or edit the primary mobile number. No tooltip or help text explains why.

---

### 18. Default DCU Config page has no heading or title

**File:**
- `default-add.html`

The Default DCU Config page has no `<h3>` heading, unlike every other page in the application. The breadcrumb shows nothing, and the page content starts without any identifying title.

---

### 19. Hardcoded AngularJS-generated CSS classes in template

**File:**
- `dcu-modify.html:49, 72, 1571, 1599, 1625`

Throughout the template, AngularJS-generated class strings are hardcoded:
```html
class="form-control ng-pristine ng-untouched ng-valid-pattern ng-valid-maxlength ng-valid ng-valid-required"
```

These are runtime Angular classes that should not be in the static HTML source. They may conflict with Angular's actual runtime class application.

---

### 20. Serial number field uses `readonly` but is still tabbable

**File:**
- `dcu-modify.html:35`

```html
<input type="text" ... ng-model="dcu.dcu_id" readonly="readonly">
```

The field is `readonly`, not `disabled`. Users can tab into it and attempt to edit, but changes are silently discarded. Should use `disabled` or `tabindex="-1"`.

---

### 21. No loading indicator on any config operation

**Files:**
- `dcu-modify.html`
- `dcu-controllers.js`
- `default-controllers.js`

None of the config operations (apply, load defaults, sync) show a spinner or progress indicator. The page appears unresponsive during API calls, especially config sync which contacts the SERVER and then the field device over Netty.

---

### 22. `defaultFactory.add()` posts without `Content-Type` header

**File:**
- `default-factory.js`

```javascript
obj.add = function(dcu_conf) {
    return $http.post(serviceBase + '/conf/add_defult_dcu_conf', dcu_conf).then(function(results) {
        return results;
    });
}
```

Does not explicitly set `Content-Type: application/json`, unlike `dcuFactory.modifysystemconfig()` which relies on the implicit Angular default. The backend controller expects `consumes = MediaType.APPLICATION_JSON_VALUE`, so this works incidentally but is fragile.

---

### 23. `sync_schduler_conf` endpoint misspelled "scheduler"

**File:**
- `DeviceConfigurationController.java:135, 148, 150`

The method name `syncSchedulerConfigurations` and field `getSchedulerConfBuffer` are correctly spelled, but the URL mapping and all references in `dcu-factory.js` use the misspelled `schduler`. This makes API discovery inconsistent.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 2 | `$scope` overwritten with Promise in `apply()`, page reloads before async calls complete |
| 🟠 Major | 8 | Silent sync failures, hardcoded port 8102, no user feedback, hardcoded default config ID "100", delete bypasses modal, wrong sort column, duplicate pagination, no null guard on sync modal |
| 🟡 Minor | 13 | Typos, dead code, no range validation, disabled mobile field, missing heading, hardcoded CSS, readonly vs disabled, no loading indicator, fragile Content-Type, inconsistent spelling |

### Recommended Fix Priority

1. **Critical bug #1** — `$scope = promise` destroys the Angular scope. The Apply button on the DCU System Configuration page is completely broken.
2. **Critical bug #2, Major #7** — Config changes may not persist because the page reloads before async requests complete.
3. **Major #3** — Silent failures on all sync operations mislead users into thinking operations succeeded.
4. **Major #4** — Port coupling with deployment config (see AGENTS.md config coupling map).
5. **Major #5, #9** — No user feedback; code quality.
6. **Major #8, #10** — Sort functionality broken; modal may crash on null response.

### Related Bugs

This page shares the **hardcoded port 8102** issue with Monitor & Control bug #11 (same coupling problem, different controller — `DeviceConfigurationController.java` vs `DeviceConfigurationController.java` lines 69/186/219/249/362). Both need to be fixed together if `SERVER_PORT` changes.
