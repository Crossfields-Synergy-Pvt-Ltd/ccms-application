# Monitor and Control Page — Bug Report

> Generated from code review on 2026-07-18
> Scope: Full monitor and control page stack (AngularJS frontend + Java Spring backend + MongoDB data access + Netty device commands)

## Severity Legend

| Severity | Meaning |
|---|---|
| 🔴 Critical | Breaks core functionality, shows incorrect data, or throws errors |
| 🟠 Major | Causes incorrect behavior, performance issues, or silent failures |
| 🟡 Minor | Code quality, maintainability, or UX polish |

---

## Files Examined

| # | File Path | Role |
|---|---|---|
| 1 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/monitorandcontrol/monitorandcontrol-controllers.js` | AngularJS controller |
| 2 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/monitorandcontrol/monitorandcontrol-factory.js` | AngularJS factory (API calls) |
| 3 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/monitorandcontrol/monitorandcontrol-list.html` | HTML template |
| 4 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/MonitorController.java` | Spring MVC REST controller |
| 5 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/DeviceConfigurationController.java` | Light ON/OFF command controller |
| 6 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/DashBoardDaoImpl.java` | MongoDB data access |
| 7 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/DCUInstantData.java` | DTO (HandShake + InstantMeterData composite) |
| 8 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/HandShake.java` | MongoDB `handshake_info` document POJO |
| 9 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/MonitorControlCount.java` | Aggregated dashboard counts DTO |
| 10 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/server/InstantMeterData.java` | MongoDB `meter_instant_details` document POJO |

---

## 🔴 Critical Bugs

### 1. "No Output" checkbox filter checks the wrong field — duplicates "Mains Supply Off"

**File:**
- `monitorandcontrol-controllers.js:114`

**Description:**
The checkbox filter array index 5 corresponds to "No Output" (`$scope.nooutput_check`), but the filter logic checks `main_supply_status` instead of `red_phse_no_output`:

```javascript
// Line 114 (No Output — WRONG):
if(checks[5] && item.dcu_details.main_supply_status == 1) add_obj = true;

// Line 111 (Mains Supply Off — checks the same field):
if(checks[2] && item.dcu_details.main_supply_status == 1) add_obj = true;
```

This means:
- The "No Output" filter **never shows any "No Output" devices**.
- It instead **duplicates the "Mains Supply Off" filter** (both show the same devices).
- Devices with `red_phse_no_output == 1` are never highlighted by either filter.

**Fix:**
Change line 114 to:
```javascript
if(checks[5] && item.dcu_details.red_phse_no_output == 1) add_obj = true;
```

---

### 2. `cnt_failure` count is always 0 in dashboard header

**File:**
- `DashBoardDaoImpl.java:83-136`

**Description:**
The `getDahsBoardCountstats()` method iterates over all `HandShake` documents to compute aggregate counts. `cnt_failure` is initialized to `0` (line 73) but **never incremented** in the loop. There is no `if(tmp.getCnt_status() == 1) cnt_failure++;` statement. The UI header will always display "0" for Contactor Failure, regardless of actual field data.

**Fix:**
Add the missing increment in the aggregation loop:
```java
if(tmp.getCnt_status() == 1)
    cnt_failure++;
```

---

### 3. `no_out_put` count is always 0 in dashboard header

**File:**
- `DashBoardDaoImpl.java:83-136`

**Description:**
Same root cause as bug #2. The field `no_out_put` is initialized to `0` and never incremented. The `HandShake` POJO has a `red_phse_no_output` field, but it is never counted.

**Fix:**
Add the missing increment:
```java
if(tmp.getRed_phse_no_output() == 1)
    no_out_put++;
```

---

### 4. `gotoswitchpoint` passes the wrong parameter — sends full object instead of serial number string

**Files:**
- `monitorandcontrol-list.html:196`
- `monitorandcontrol-controllers.js:197-200`

**Description:**
The HTML template calls:
```html
ng-click="gotoswitchpoint(obj)"
```
Here `obj` is the full `DCUInstantData` object (containing `dcu_details`, `meter_data`, `device_name`, etc.).

The controller function expects a string:
```javascript
$scope.gotoswitchpoint = function (gateway_serial_number) {
    $scope.gateway_serial_number = gateway_serial_number;
    $state.go('dashboard.switchpoint', {gateway_serial_number: $scope.gateway_serial_number});
};
```

The entire `DCUInstantData` object is passed as the `gateway_serial_number` parameter. The switchpoint controller will receive `$stateParams.gateway_serial_number` as the full object, not a string — it would need to extract `.dcu_details.gateway_serial_number` to get the actual serial number.

**Fix:**
Change the controller function to:
```javascript
$scope.gotoswitchpoint = function (obj) {
    var serial = obj.dcu_details.gateway_serial_number;
    $state.go('dashboard.switchpoint', {gateway_serial_number: serial});
};
```

---

### 5. Refresh button calls undefined `login()` function

**Files:**
- `monitorandcontrol-list.html:23`
- `monitorandcontrol-controllers.js`

**Description:**
The Refresh button in the HTML template calls:
```html
<a type="button" ng-click="login();" class="btn btn-info btn-sm">&nbsp;Refresh&nbsp;</a>
```

`$scope.login()` is **never defined** in `monitorandcontrolListControllers`. Clicking the button will throw a **ReferenceError** and silently fail (AngularJS swallows exceptions in event handlers, but the refresh does nothing).

**Fix:**
Replace `login()` with a function that actually refreshes the data, e.g. `$scope.search()` or `$scope.loadPage(0)`.

---

## 🟠 Major Bugs

### 6. Light toggle returns no feedback to the user

**File:**
- `monitorandcontrol-controllers.js:207, 211`

**Description:**
Both the ON and OFF light commands have empty `.then()` callbacks:

```javascript
monitorandcontrolFactory.turnOffLights($scope.qs_params).then(function(data){});
monitorandcontrolFactory.turnOnLights($scope.qs_params).then(function(data){});
```

The user receives no visual confirmation that:
- The command was sent successfully
- The command failed (network error, server down, DCU unreachable)

The light icon (`lighton.png` / `lightoff.png`) does not update until the next full page refresh or filter search. This creates a poor user experience where the button appears to do nothing.

**Fix:**
Add success/error handling:
```javascript
monitorandcontrolFactory.turnOffLights($scope.qs_params).then(function(data) {
    obj.dcu_details.light_status = 0; // optimistic update
}, function(error) {
    alert("Failed to turn off lights: " + error);
});
```

---

### 7. `instant_data_filter` fetches ALL records from MongoDB before paginating in Java

**Files:**
- `MonitorController.java:260`
- `DashBoardDaoImpl.java:486-523`

**Description:**
The `getAllHandShakeData()` DAO method executes `mongoTemplate.find(query, HandShake.class)` without any `.skip()` or `.limit()` on the query. ALL matching documents are loaded into memory.

Then in `MonitorController.java:262-266`, manual pagination is applied:
```java
int start = page * size;
int end = Math.min(start + size, dcu_list.size());
List<HandShake> paged_list = dcu_list.subList(start, end);
```

For a database with thousands of devices, the entire collection is transferred from MongoDB to Java heap every time the user clicks Next. This will cause:
- High memory usage on the server
- Slow response times for subsequent pages
- Increased MongoDB load

**Fix:**
Add `.skip()` and `.limit()` to the MongoDB query in `DashBoardDaoImpl`:
```java
query.skip(page * size).limit(size);
```

---

### 8. `getByID()` called with no argument on controller init

**File:**
- `monitorandcontrol-controllers.js:193-195`

**Description:**
On controller initialization:
```javascript
monitorandcontrolFactory.getByID().then(function(data) {
    $scope.meter_data = data.data;
});
```

The `getByID()` factory method expects a `gateway_serial_number` parameter:
```javascript
obj.getByID = function(gateway_serial_number) {
    return $http.get(serviceBase + '/dashboard/meter_data_by_id/' + gateway_serial_number);
};
```

This fires a request to `/dashboard/meter_data_by_id/undefined` which will return an error. The result (`$scope.meter_data`) is never used in the template.

**Fix:**
Either pass the correct parameter or remove the dead code.

---

### 9. "Today" date range starts from current time, not start of day

**File:**
- `monitorandcontrol-controllers.js:41`

**Description:**
```javascript
'Today': [moment(), moment()],
```

Both start and end are set to `moment()` (right now). When used to filter by `installation_date`, this means only devices installed at the exact current second would match. The start should be the beginning of today.

**Fix:**
```javascript
'Today': [moment().startOf('day'), moment()],
```

---

### 10. "Last Month" date range is actually "Last 30 Days"

**File:**
- `monitorandcontrol-controllers.js:45`

**Description:**
```javascript
'Last Month': [moment().subtract(29, 'days'), moment()],
```
This is a rolling 30-day window ending today, heavily overlapping with "Yesterday" and "Last 7 Days". It is NOT the previous calendar month (e.g., June 1–30 for a July 18 view).

**Fix:**
```javascript
'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
```

---

### 11. Hardcoded port 8102 in server push URLs

**File:**
- `DeviceConfigurationController.java:69, 185, 218, 243, 350`

**Description:**
All server forward URLs hardcode port `8102`:
```java
sb.append("http://"+serverHost+":8102/user/push/manuval_on?...");
```

While `serverHost` is configurable via `@Value("${ccms.server.host}")`, the port is not. If `SERVER_PORT` environment variable is changed from the default `8102`, all light ON/OFF, scheduler sync, and node configuration push commands will fail silently.

**Fix:**
Inject the port via `@Value("${ccms.server.port:8102}")` and use it in all URL constructions.

---

## 🟡 Minor / Code Quality Issues

### 12. `filter()` function is an incomplete stub

**File:**
- `monitorandcontrol-controllers.js:189-191`

**Description:**
```javascript
$scope.filter = function () {
    console.log($scope.selected_dcu.name.name);
};
```
The function only logs to console and does nothing. There is no DCU dropdown in the template that would call this function either. Dead code.

---

### 13. DCU name list is fetched but never used

**File:**
- `monitorandcontrol-controllers.js:184-187`

**Description:**
`getAllDcuNames()` fetches the DCU list and stores it in `$scope.dcu_data`, but the template never references this data. The initial `$scope.selected_dcu = {};` and the fetched `dcu_data` are never rendered in any dropdown or UI element.

---

### 14. `delete()` references undefined `userFactory`

**File:**
- `monitorandcontrol-controllers.js:217-219`

**Description:**
```javascript
$scope.delete = function(id) {
    userFactory.delete(id);
};
```
`userFactory` is not injected into the controller. If this function were ever called, it would throw a ReferenceError. The companion function `deleteconf()` uses `$modal.open()` which would work independently.

---

### 15. Cascade dropdowns not cleared on parent change

**File:**
- `monitorandcontrol-controllers.js:239-253`

**Description:**
When the user changes the District selection, `getMandalOnSelect()` updates `mandal_list`, but `gp_list` and any `village_list` from the previous selection remain visible. Same when Mandal changes — `gp_list` is updated but `village_list` is not cleared. This can show stale/incorrect options from the previous hierarchy branch.

---

### 16. Village filter selection does nothing

**File:**
- `monitorandcontrol-list.html:150-151`
- `monitorandcontrol-controllers.js`

**Description:**
The GP dropdown has `ng-change="getVillageOnSelect();"` and `getVillageOnSelect` is defined in the controller. However, the selected village is never read or used in any query parameter. The `qs_params` only include district, mandal, and gp.

---

### 17. `show_more` variable is dead code

**File:**
- `monitorandcontrol-controllers.js:9`

**Description:**
`$scope.show_more = 'true'` (a string) is declared but never used anywhere. The template uses a separate variable `checked` via `ng-model="checked"` on the "Show more information" checkbox (line 18). The detailed meter data section uses `ng-show="checked"`, which works correctly via `$scope.checked`, making `$scope.show_more` entirely dead code.

---

### 18. `turn_on_light` function name is misleading

**File:**
- `monitorandcontrol-controllers.js:202-215`

**Description:**
The function is named `turn_on_light` but it **toggles** the light state:
```javascript
if($scope.obj.dcu_details.light_status == 1) {
    monitorandcontrolFactory.turnOffLights(...);  // currently ON → turn OFF
} else {
    monitorandcontrolFactory.turnOnLights(...);   // currently OFF → turn ON
}
```
The name should be `toggle_light` to accurately describe its behavior.

---

### 19. `hasMoreData()` triggers one unnecessary API call at the end

**File:**
- `monitorandcontrol-controllers.js:176-178`

**Description:**
```javascript
$scope.hasMoreData = function() {
    return $scope.handshake_Data.length > 0
        && $scope.handshake_Data.length >= ($scope.currentPage + 1) * $scope.pageSize;
};
```

When the total number of devices is exactly divisible by `pageSize` (e.g., 100 devices, pageSize=50), after loading page 1 (50 items) then page 2 (50 items), `handshake_Data.length = 100` and `(1+1)*50 = 100`, so `100 >= 100` is true. The "Next" button remains enabled, allowing the user to make one extra API call that returns 0 items.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 5 | Wrong filter field (No Output = Mains Supply Off), two always-zero counts (`cnt_failure`, `no_out_put`), wrong parameter type passed to switchpoint, undefined `login()` for Refresh |
| 🟠 Major | 6 | No user feedback for light toggle, full DB scan per page, no-arg API call on init, wrong "Today"/"Last Month" ranges, hardcoded port 8102 |
| 🟡 Minor | 8 | Stub `filter()` function, unused DCU list, undefined `userFactory`, cascading selects, unused village filter, dead `show_more`, misleading function name, extra API call at end |

### Recommended Fix Priority

1. **Filter logic fixes** (bugs 1–3) — Dashboard counts and filters showing wrong data.
2. **Parameter and function fixes** (bugs 4–5, 8) — Broken navigation and button actions.
3. **User feedback** (bug 6) — Light toggle should show success/failure.
4. **Performance** (bug 7) — Pagination loads entire DB into memory.
5. **Date ranges** (bugs 9–10) — Incorrect preset ranges.
6. **Config coupling** (bug 11) — Hardcoded port violates deployment config rules.
7. **Code cleanup** (bugs 12–19) — Dead code, misleading names, UX polish.
