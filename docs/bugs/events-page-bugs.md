# Events Page — Bug Report

> Generated from code review on 2026-07-18
> Scope: Full events page stack (AngularJS frontend + Java Spring backend + CSV file data access)

## Severity Legend

| Severity | Meaning |
|---|---|
| 🔴 Critical | Breaks core functionality, crashes the page, or produces wrong results |
| 🟠 Major | Causes incorrect behavior or data loss |
| 🟡 Minor | Code quality, maintainability, or UX polish |

---

## 🔴 Critical Bugs

### 1. `gateway_identifier` is undefined in View/Export button calls

**Files:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-list.html:119, 123`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:56, 103`

**Description:**
Both the View button (`ng-click="showdate(gateway_identifier)"`) and Export button (`ng-click="export_events(gateway_identifier)"`) pass a variable `gateway_identifier` that is **never defined** in the controller scope. Inside `showdate()` and `export_events()`, the code correctly reads `$scope.selected_dcu.name.gateway_identifier` directly, so the passed parameter is completely unused — but the parameter itself evaluates to `undefined`. This could confuse future developers into thinking a parameter is expected, and if the function signature were ever changed to use the parameter instead, it would break.

**Fix:**
Remove the parameter from both `ng-click` directives in `event-list.html`.

---

### 2. No DCU selection validation — silent TypeError

**Files:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:73, 120`

**Description:**
Both `showdate()` and `export_events()` access `$scope.selected_dcu.name.gateway_identifier` without checking if `$scope.selected_dcu.name` is defined. If no DCU has been selected from the `ui-select` dropdown, this throws a **TypeError** (`Cannot read property 'gateway_identifier' of undefined`) and the operation silently fails with no user feedback.

**Fix:**
Add a guard clause at the top of both functions:
```javascript
if (!$scope.selected_dcu || !$scope.selected_dcu.name) {
    // show user-facing error message
    return;
}
```

---

### 3. `moment().today` is not a valid moment.js API — "Today" range broken

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:48`

**Description:**
The "Today" preset uses `moment().today`, which is **not a standard moment.js method**. The expression `[moment().today, moment()]` evaluates to `[undefined, moment()]`. This means the date range picker's "Today" option will have an undefined start date.

**Fix:**
Replace `moment().today` with `moment().startOf('day')`:
```javascript
'Today': [moment().startOf('day'), moment()],
```

---

## 🟠 Major Bugs

### 4. Hardcoded 30-day limit silently truncates results

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/file/FileUtil.java:42`

**Description:**
The `getDataFilenamesBetweenDate()` method iterates with `for (int day = 0; day < 30; day++)`, imposing an arbitrary 30-day cap. If a user selects a date range spanning more than 30 days (e.g., a full month of 31 days), all days beyond 30 are **silently dropped** with no warning to the user.

**Fix:**
The cap is likely a safety valve. Either:
- Remove the cap and iterate based on the actual difference between start and end dates, or
- Set a reasonable max (e.g., 365) and log a warning when results are truncated.

---

### 5. "Yesterday" range includes today

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:49`

**Description:**
```javascript
'Yesterday': [moment().subtract(1, 'days'), moment()],
```
The end date is `moment()` (right now, today), not yesterday. "Yesterday" should span from start-of-yesterday to end-of-yesterday, not include today.

**Fix:**
```javascript
'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
```

---

### 6. "Last Month" label is misleading — actually "Last 30 Days"

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:52`

**Description:**
```javascript
'Last Month': [moment().subtract(29, 'days'), moment()],
```
This is a 30-day rolling window ending today, overlapping heavily with "Yesterday" and "Last 7 Days". It is NOT the previous calendar month. For example, on July 18, "Last Month" should be June 1–June 30.

**Fix:**
```javascript
'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
```

---

### 7. Village filter selection does nothing

**Files:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-list.html:50-53`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:22-30`

**Description:**
The Village dropdown (`ng-model="filter.village"`) is rendered in the filter panel, but the `filter()` function in the controller only reads `selectedDistrict`, `selectedMandal`, and `select_gp`. The village value is never passed to the API or used in any query. The DCU list is filtered only by district/mandal/GP.

**Fix:**
Either:
- Remove the Village dropdown from the filter panel, or
- Add village to the `filter()` function and the API call.

---

### 8. Cascade dropdowns don't clear children on parent change

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:132-152`

**Description:**
When a user changes the District selection, `getMandalOnSelect()` updates `mandal_list`, but `gp_list` and `village_list` from the previous selection remain visible. The same issue applies when changing Mandal — the `village_list` is not cleared. This can show stale/incorrect options that belong to a different hierarchy branch.

**Fix:**
Clear child selections when a parent changes:
```javascript
$scope.getMandalOnSelect = function(selectedDistrict) {
    $scope.gp_list = [];       // clear GP
    $scope.village_list = [];  // clear village
    $scope.select_gp = null;   // reset GP selection
    $scope.filter.village = null; // reset village selection
    eventFactory.getByMandal($scope.selectedDistrict).then(...);
};
// Similar for getGpOnSelect()
```

---

## 🟡 Minor / Code Quality Issues

### 9. Double slash in CSV file path construction

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/file/FileUtil.java:24-25, 53`

**Description:**
`BASE_PATH = "/home/data/dontdelete/"` (trailing slash) combined with `FILE_SPRATER = "/"` produces a double slash in file paths (e.g., `/home/data/dontdelete//2019/06/...`). Linux treats `//` as `/`, so it works, but it's fragile and may break on non-Linux systems or some libraries.

**Fix:**
Remove the trailing slash from `BASE_PATH` or the leading slash from `FILE_SPRATER`.

---

### 10. Pagination functions redefined on every View click

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:84-97`

**Description:**
`figureOutTodosToDisplay()` and `pageChanged()` are defined **inside** the promise callback of `showdate()`. Every time the user clicks "View", new function objects are created and assigned to the scope. This is wasteful and makes the functions difficult to test or override.

**Fix:**
Move these function definitions to the top level of the controller (outside any callback), and have them reference `$scope.todos` which gets updated by the API call.

---

### 11. Deprecated `.success()/.error()` API

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-factory.js:36-61`

**Description:**
The `exportEventData` factory method uses `$http`'s deprecated `.success()` and `.error()` methods, which were removed in AngularJS 1.6+. These should be replaced with standard `.then()` and `.catch()` for forward compatibility.

**Fix:**
```javascript
obj.exportEventData = function(qs_params) {
    return $http.get(serviceBase + '/events/export_events/' + qs_params)
        .then(function(response) {
            var headers = response.headers;
            // ... download logic using response.data
        })
        .catch(function(error) {
            console.log(error);
        });
};
```

---

### 12. No error handling on any API call

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js:18-20, 27-29, 76-98, 133-152`

**Description:**
None of the `eventFactory` calls (getAllDcuNames, getByID, getByMandal, getByGp, getByVillage, exportEventData) have `.catch()` error handlers. If the backend is unreachable or returns an error, the user will see no feedback — the page will silently remain in its previous state or show empty data.

**Fix:**
Add `.catch(function(error) { ... })` to every `eventFactory` call to log the error and display a user-friendly message.

---

### 13. No loading/feedback indicator

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-list.html`
- `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/event/event-controllers.js`

**Description:**
When the user clicks "View" or "Export" for a large date range, CSV file reading can take several seconds. There is no spinner, progress bar, or any visual feedback to indicate that work is in progress. The user may think the click did nothing and click again, causing duplicate requests.

---

### 14. Empty CSV export returns headers-only file

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/EventsController.java:196-234`

**Description:**
If no events exist for the selected DCU and date range, `exportHistory()` still generates a CSV file with just column headers. The response includes a 200 status with a legitimate-looking CSV. The user downloads a file that appears valid but has no data rows. Ideally this should return a `204 No Content` or include a message row indicating no data was found.

---

### 15. `event_list` endpoint is dead code

**File:**
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/EventsController.java:45-86`

**Description:**
`getAllEvents()` is fully commented out and returns `null`. The corresponding factory method `eventFactory.getAll()` exists but is never called from any controller. This is dead code that should be either removed or restored with a working implementation.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 3 | Undefined `gateway_identifier` var, missing DCU validation, invalid `moment().today` |
| 🟠 Major | 5 | 30-day truncation, wrong "Yesterday" range, misleading "Last Month" label, unused village filter, cascade dropdowns not cleared |
| 🟡 Minor | 7 | Double-slash path, pagination redefinition, deprecated API, missing error handling, no loading indicator, empty export, dead code |

### Recommended Fix Priority

1. **Critical bugs** (1–3) — These cause crashes or broken functionality immediately.
2. **Date range presets** (5, 6) — These mislead users about what data they're viewing.
3. **Data truncation** (4) — Users may unknowingly miss data beyond 30 days.
4. **Error handling** (12, 13) — Users need feedback when things go wrong.
5. **Cascade & village** (7, 8) — Filter panel usability.
6. **Code quality** (9, 10, 11, 14, 15) — Maintainability improvements.
