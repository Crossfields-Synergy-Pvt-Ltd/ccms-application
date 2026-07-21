# Switch Point Information Page — Bug Report

> Generated from code review on 2026-07-21
> Scope: Full switchpoint page stack (AngularJS frontend + Java Spring backend + MongoDB data access)

## Severity Legend

| Severity | Meaning |
|---|---|
| 🔴 Critical | Breaks core functionality, shows incorrect data, or throws errors |
| 🟠 Major | Causes incorrect behavior, silent failures, or data inconsistency |
| 🟡 Minor | Code quality, maintainability, or UX polish |

---

## Files Examined

| # | File Path | Role |
|---|---|---|
| 1 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/switchpoint/switchpoint-list.html` | HTML template |
| 2 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/switchpoint/switchpoint-controllers.js` | AngularJS controller |
| 3 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/switchpoint/switchpoint-factory.js` | AngularJS factory (API calls) |
| 4 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/app.js` | AngularJS state router |
| 5 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/MonitorController.java` | Spring MVC REST controller (`/dashboard/instant_data_id/{id}`) |
| 6 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/DCUInstantData.java` | Response POJO |
| 7 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/DashBoardDaoImpl.java` | MongoDB DAO |

---

## 🔴 Critical Bugs

### 1. `dcu_details` is never set in the backend response — Alerts panel and Light status broken

**Files:**
- `MonitorController.java:199-229`
- `DCUInstantData.java:19`

**Description:**
`getDevicesInstantDataByID()` at `MonitorController.java:210` fetches the `HandShake` object:
```java
HandShake dcu = dashBpardService.getHandShakeByID(id);
```
But **never assigns it** to the response POJO. The method sets `last_communication_time`, `meter_data`, `id`, and `device_name`, but the critical line `dcu_instant_data.setDcu_details(dcu)` is missing.

The template heavily depends on `obj.dcu_details.*`:
- Light ON/OFF image (`switchpoint-list.html:81, 84`)
- Alerts panel: Contactor failure, MCB, Door, Surge protector (`switchpoint-list.html:260-335`)

Since `dcu_details` is `null`, all these sections render blank/undefined. This appears to be a regression from when the `/instant_data_id/{id}` endpoint was moved from `DCUController` to `MonitorController`.

**Fix:**
Add the missing line after `MonitorController.java:210`:
```java
dcu_instant_data.setDcu_details(dcu);
```

---

### 2. `$stateParams.gateway_serial_number` is ignored on page load — navigation broken

**Files:**
- `app.js:360`
- `switchpoint-controllers.js:4`

**Description:**
The state definition declares:
```javascript
params :{gateway_serial_number :null},
```
When navigating from the Monitor & Control page via `gotoswitchpoint(obj)` (`monitorandcontrol-controllers.js:200`), the serial number is passed in the URL state params. However, the switchpoint controller injects `$stateParams` but **never reads it**. The user must manually re-select the DCU from the dropdown every time.

**Fix:**
On controller init, read `$stateParams.gateway_serial_number`. If present, auto-select the DCU and trigger `view()`:
```javascript
var serialParam = $stateParams.gateway_serial_number;
if (serialParam) {
    // find matching DCU in dcu_data and trigger view
}
```

---

### 3. Light toggle icon click calls `lighton` as a variable — no function defined

**File:**
- `switchpoint-list.html:80`

**Description:**
```html
<a type="button" ng-click="lighton">
```
`lighton` is used without parentheses, so Angular evaluates it as a variable lookup rather than a function call. Additionally, no `$scope.lighton` function exists in the controller. Clicking the light icon does nothing.

**Fix:**
Either implement a toggle function and change to `ng-click="lighton()"`, or remove the click handler if light toggling cannot be supported from this page.

---

## 🟠 Major Bugs

### 4. "Phase Power (kW)" displays voltage instead of power

**File:**
- `switchpoint-list.html:120-124`

**Description:**
```html
<div class=" col-md-5 ">Phase Power (kW)</div>
<div class="col-md-4 ">
    <button ...>{{obj.meter_data.r_phase_voltage}}</button>
</div>
```
The label says "Phase Power (kW)" but the binding reads `r_phase_voltage` (voltage in V). This should display actual power data, e.g. `kwh_total` or a calculated real power value.

**Fix:**
Replace `obj.meter_data.r_phase_voltage` with the correct power field (e.g. `obj.meter_data.kwh_total`) or a calculated value.

---

### 5. Configuration panel accesses `dcu_configurations` — a commented-out POJO field

**Files:**
- `DCUInstantData.java:21-22`
- `switchpoint-list.html:141, 148, 156, 163`

**Description:**
The `DCUInstantData` POJO has `dcu_configurations` commented out:
```java
/*DCUConfiguration dcu_configurations;*/
```
But the template reads from it:
```html
0.02{{obj.dcu_configurations.phase_2_current_min}}
5{{obj.dcu_configurations.phase_3_current_min}}
150{{obj.dcu_configurations.phase_2_voltage_min}}
260{{obj.dcu_configurations.phase_3_voltage_min}}
```

Since `dcu_configurations` is `undefined`, each `{{obj.dcu_configurations.*}}` evaluates to nothing. The displayed values are just hardcoded prefixes (`"0.02"`, `"5"`, `"150"`, `"260"`) with no actual device data. This gives the false appearance of real configuration data.

**Fix:**
Either uncomment `dcu_configurations` in `DCUInstantData.java`, populate it from the backend, and bind dynamically; or remove the Configuration panel from the template.

---

### 6. Schedule ON/OFF times are hardcoded

**File:**
- `switchpoint-list.html:179, 186`

**Description:**
```html
<button type="button" class="mdinfo" style="width: 90px">18:00</button>
<button type="button" class="mdinfo" style="width: 90px">6:00</button>
```
Schedule ON and OFF times are static text, not sourced from the backend. These values will be incorrect for any DCU with a different schedule configuration.

**Fix:**
Fetch schedule data from the backend and bind dynamically.

---

### 7. No error handling on API calls

**File:**
- `switchpoint-controllers.js:9-11, 16-21`

**Description:**
Neither `getAllDcuNames()` nor `getByID()` have `.catch()` error handlers:
```javascript
switchpointFactory.getAllDcuNames().then(function(data) {
    $scope.dcu_data = data.data;
});

switchpointFactory.getByID(...).then(function(data) {
    $scope.obj = data.data;
});
```
If the backend is unreachable or returns an error, the user sees no feedback — the dropdown stays empty or the panels remain blank.

**Fix:**
Add `.catch(function(error) { ... })` to both calls to log the error and display a user-friendly message.

---

### 8. No loading indicator during API calls

**File:**
- `switchpoint-list.html:10-13`

**Description:**
A loading indicator exists only as a commented-out placeholder:
```html
<!-- <div loading-indicator class="waiting-box"> -->
```
No spinner, progress bar, or visual feedback is shown during data fetching. The user may click View and see nothing for several seconds.

**Fix:**
Wire up a loading flag (`$scope.loading`) and uncomment/implement the loading indicator.

---

## 🟡 Minor / Code Quality Issues

### 9. Connected Load panel uses hardcoded "N/A"

**File:**
- `switchpoint-list.html:217, 225`

**Description:**
Working lights and Less load always display "N/A" instead of actual device data.

---

### 10. Multiple alert indicators are hardcoded

**File:**
- `switchpoint-list.html:246, 254, 275, 298, 307`

**Description:**
The following alerts show static text regardless of actual device state:

| Alert | Always Shows |
|---|---|
| Overload | "No" |
| Voltage | "In range" |
| Input power supply | "Present" |
| Output supply | "Present" |
| RTU | "On Mains" |

Only Contactor failure, MCB, Door, and Surge protector are dynamically bound to backend data. The static alerts are misleading.

---

### 11. Connectivity icon is always "connected"

**File:**
- `switchpoint-list.html:57-59`

**Description:**
```html
<i class="fa fa-signal headerIcons connectivity changeCursorPointer connected"></i>
```
The CSS class `connected` is hardcoded. The icon should reflect actual online/offline status based on the handshake timestamp or `connection_status`.

---

### 12. `modifydcu()` passes full `HandShake` object instead of serial number string

**Files:**
- `switchpoint-list.html:51`
- `switchpoint-controllers.js:24-28`

**Description:**
The HTML passes the full `HandShake` object:
```html
ng-click="modifydcu(obj.dcu_details)"
```
But the controller parameter is named `gateway_serial_number`:
```javascript
$scope.modifydcu = function(gateway_serial_number) {
    $scope.gateway_serial_number = gateway_serial_number;
    $state.go('dashboard.dcu_edit', {gateway_serial_number: $scope.gateway_serial_number});
};
```
The entire `HandShake` object is passed to the `dcu_edit` state as the serial number. Same pattern as Monitor & Control page bug #4.

**Fix:**
Change to:
```javascript
$scope.modifydcu = function(obj) {
    var serial = obj.dcu_details ? obj.dcu_details.gateway_serial_number : obj.gateway_serial_number;
    ...
};
```

---

### 13. No `ng-if` guard on data display panels

**File:**
- `switchpoint-list.html:44-344`

**Description:**
`$scope.obj` is `undefined` before the user clicks View. All data panels (Meter Data, Configuration, Alerts, etc.) are rendered with empty bindings instead of being hidden until data is available.

**Fix:**
Wrap data panels in `<div ng-if="obj">` to show them only after data is loaded.

---

### 14. Old data is not cleared on re-fetch

**File:**
- `switchpoint-controllers.js:16-21`

**Description:**
When the user clicks View for a new DCU, `$scope.obj` still holds the previous DCU's data until the new response arrives. This can cause a flash of stale data.

**Fix:**
Set `$scope.obj = null` before making the API call.

---

### 15. No unit tests for switchpoint controller or factory

**Description:**
Unlike the Events, History, and Monitor & Control pages, the switchpoint page has no corresponding test files (`switchpoint-controller.test.js`, `switchpoint-factory.test.js`).

---

### 16. Commented-out loading indicator is dead code

**File:**
- `switchpoint-list.html:10-13`

**Description:**
The commented-out loading indicator div should be removed if not used, or implemented if needed.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 3 | `dcu_details` never set in API response, `$stateParams` ignored, `lighton` not a function call |
| 🟠 Major | 5 | Wrong power binding, commented-out config POJO, hardcoded schedule, no error handling, no loading indicator |
| 🟡 Minor | 8 | Static alerts/connectivity, hardcoded N/A, wrong param type in `modifydcu()`, no `ng-if` guard, stale data flash, no tests, dead code |

### Recommended Fix Priority

1. **Critical bugs (1–3)** — `dcu_details` fix restores the entire Alerts panel and Light status; `$stateParams` fix restores cross-page navigation.
2. **Configuration panel (5)** — Currently shows broken/incorrect data.
3. **Hardcoded values (4, 6, 9, 10, 11)** — Misleading data that appears legitimate.
4. **Error handling (7, 8)** — User feedback and UX polish.
5. **Code quality (12–16)** — Maintainability and correctness.

### Root Cause Note

Bug #1 is a regression: the endpoint `/dashboard/instant_data_id/{id}` was moved from `DCUController` (where the original implementation likely set `dcu_details`) to `MonitorController`, but the `setDcu_details()` call was lost in the migration. The same issue is visible in the commented-out code at `DCUController.java:420-453`.
