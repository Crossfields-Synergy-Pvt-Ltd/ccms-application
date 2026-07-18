# History Page — Bug Report

> Generated from code review on 2026-07-18
> Scope: Full history page stack (AngularJS frontend + Java Spring controller + CSV file data access)

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
| 1 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/history/history-controllers.js` | AngularJS controller |
| 2 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/history/history-factory.js` | AngularJS factory (API calls) |
| 3 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/history/history-list.html` | HTML template |
| 4 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/MeterDataControler.java` | Spring MVC REST controller |
| 5 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/file/FileDao.java` | CSV file data access |
| 6 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/file/FileUtil.java` | CSV file reader and parser |
| 7 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/ui/MeterDataUI.java` | Meter data UI DTO |

---

## 🔴 Critical Bugs

### 1. `gateway_identifier` is undefined in View/Export button calls

**Files:**
- `history-list.html:99, 103`
- `history-controllers.js:54, 102`

**Description:**
Both the View button and Export button pass `gateway_identifier` as a function parameter:
```html
ng-click="showdate(gateway_identifier);"
ng-click="export_history(gateway_identifier);"
```

The variable `gateway_identifier` is **never defined** in the controller scope. Inside `showdate()` and `export_history()`, the code reads `$scope.selected_dcu.name.gateway_identifier` directly from the scope, so the passed parameter is ignored. However, if the function signature were ever changed to use the parameter instead, it would break immediately.

**Fix:**
Remove the parameter from both `ng-click` directives in `history-list.html`.

---

### 2. No DCU selection validation — silent TypeError

**File:**
- `history-controllers.js:55, 75, 123`

**Description:**
Both `showdate()` and `export_history()` access `$scope.selected_dcu.name.gateway_identifier` without checking if `$scope.selected_dcu.name` exists:
```javascript
console.log($scope.selected_dcu.name.gateway_identifier);  // line 55
$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + ...;  // lines 75, 123
```

If no DCU is selected from the `ui-select` dropdown, this throws a **TypeError** (`Cannot read property 'gateway_identifier' of undefined`) and the operation silently fails with no user feedback.

**Fix:**
Add a guard clause at the top of both functions:
```javascript
if (!$scope.selected_dcu || !$scope.selected_dcu.name) {
    return;
}
```

---

### 3. Export response overwrites the table data display

**Files:**
- `history-controllers.js:126-128`
- `history-factory.js:34-63`

**Description:**
The `export_history()` function assigns the API response to `$scope.list`:
```javascript
historyFactory.getByIDhistory($scope.qs_params).then(function(data) {
    $scope.list = data.data;  // line 127 — overwrites table data!
});
```

The `/meter/export_history` endpoint returns **CSV binary data**, not JSON. The factory method handles the actual file download in its `.success()` callback (blob creation, link click), but the controller's `.then()` still fires and assigns the raw CSV string to `$scope.list`. This **corrupts the table view** by replacing the paginated meter data with CSV text.

**Fix:**
Remove the `.then()` assignment in the controller — the export should only trigger the download, not modify scope:
```javascript
$scope.export_history = function() {
    // ... date formatting ...
    historyFactory.getByIDhistory($scope.qs_params);
};
```

---

### 4. Two table columns display identical data ("R-Phase Power" and "Consumption" both show `kwh_total`)

**Files:**
- `history-list.html:167-182, 195-196`
- `MeterDataControler.java:211-212`
- `MeterDataUI.java:14, 17`
- `FileUtil.java:109, 113`

**Description:**
The table has two columns with different headers but identical data bindings:

| Column Header | Data Binding |
|---|---|
| R-Phase Power (kW) | `{{ obj.kwh_total }}` (line 195) |
| Consumption (kWh) | `{{ obj.kwh_total }}` (line 196) |

The `MeterDataUI` POJO has both `kwh_total` (line 14) and `consumption` (line 17) fields, and the CSV parser (`FileUtil.java`) populates both from the **same CSV column** (`tmp[10]`):
```java
ui_obj.setConsumption(tmp[10]);  // line 109
ui_obj.setKwh_total(tmp[10]);    // line 113
```

The CSV export header mapping also duplicates `kwh_total`:
```java
String[] header = {"dcu_id", "utc_date", "r_phase_voltage",
        "current_line_1", "pf_1", "kwh_total", "kwh_total"};
```

Both "R-Phase Power" and "Consumption" columns export identical data.

**Fix:**
Determine the correct CSV column for consumption vs. power, and map them to the appropriate fields. Update the table template and CSV export mapping accordingly.

---

## 🟠 Major Bugs

### 5. Sort column "Name" maps to `name` but data field is `dcu_name`

**File:**
- `history-list.html:121, 190`

**Description:**
The column header sort defines:
```html
ng-click="sortType = 'name'; sortReverse = !sortReverse"> Name
```

But the data cell renders:
```html
<td align="center">{{ obj.dcu_name }}</td>
```

Clicking the "Name" header to sort will try to sort by a field `name` that does not exist on `MeterDataUI` objects. The sort will have no visible effect.

**Fix:**
Change the sort field to `dcu_name`:
```html
ng-click="sortType = 'dcu_name'; sortReverse = !sortReverse"> Name
```

---

### 6. Sort column "R-Phase Current(A)" maps to `r_phase_current` but data field is `current_line_1`

**File:**
- `history-list.html:149, 153, 193`

**Description:**
The column header sorts by `r_phase_current`, but the data renders `{{ obj.current_line_1 }}`. Additionally, line 153 contains a **tab character** inside the sort expression string:
```html
ng-show="sortType == 'r_phase_current	' && sortReverse"
```
(there is a tab character before the closing quote). Angular's `orderBy` will look for a field literally named `r_phase_current	` (with trailing tab), which will never match.

**Fix:**
```html
ng-click="sortType = 'current_line_1'; sortReverse = !sortReverse"> R-Phase Current(A)
```

---

### 7. Sort column "Power Factor" maps to `power_factor` but data field is `pf_1`

**File:**
- `history-list.html:159, 194`

**Description:**
The column header sorts by `power_factor`, but the data renders `{{ obj.pf_1 }}`. The field `power_factor` does not exist on `MeterDataUI`.

**Fix:**
```html
ng-click="sortType = 'pf_1'; sortReverse = !sortReverse"> Power Factor
```

---

### 8. "Today" date range starts from current time, not start of day

**File:**
- `history-controllers.js:46`

**Description:**
```javascript
'Today': [moment(), moment()],
```
Both start and end are set to `moment()` (current time). When used to filter by timestamp, this means only data from the exact current second matches. The start should be the beginning of today.

**Fix:**
```javascript
'Today': [moment().startOf('day'), moment()],
```

---

### 9. "Yesterday" date range includes today

**File:**
- `history-controllers.js:47`

**Description:**
```javascript
'Yesterday': [moment().subtract(1, 'days'), moment()],
```
The end date is `moment()` (today), not yesterday. "Yesterday" should span from start-of-yesterday to end-of-yesterday.

**Fix:**
```javascript
'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
```

---

### 10. "Last Month" label is misleading — actually "Last 30 Days"

**File:**
- `history-controllers.js:50`

**Description:**
```javascript
'Last Month': [moment().subtract(29, 'days'), moment()],
```
This is a 30-day rolling window ending today, not the previous calendar month.

**Fix:**
```javascript
'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
```

---

### 11. Deprecated `.success()` / `.error()` API in export factory

**File:**
- `history-factory.js:37-62`

**Description:**
The `getByIDhistory` method uses AngularJS's deprecated `.success()` and `.error()` promise methods (removed in AngularJS 1.6+). Should be replaced with standard `.then()` and `.catch()` for forward compatibility.

**Fix:**
```javascript
obj.getByIDhistory = function(qs_params) {
    return $http.get(serviceBase + '/meter/export_history/' + qs_params)
        .then(function(response) {
            var headers = response.headers;
            var filename = headers['x-filename'];
            var contentType = headers['content-type'];
            var linkElement = document.createElement('a');
            try {
                var blob = new Blob([response.data], { type: contentType });
                var url = window.URL.createObjectURL(blob);
                linkElement.setAttribute('href', url);
                linkElement.setAttribute("download", filename);
                var clickEvent = new MouseEvent("click", {
                    "view": window, "bubbles": true, "cancelable": false
                });
                linkElement.dispatchEvent(clickEvent);
            } catch (ex) {
                console.log(ex);
            }
        }).catch(function(error) {
            console.log(error);
        });
};
```

---

### 12. No error handling on any API call

**File:**
- `history-controllers.js:18-20, 27-29, 78-98, 126-128, 137-154`

**Description:**
None of the `historyFactory` calls (`getAllDcuNames`, `getByID`, `getByIDhistory`, `getByMandal`, `getByGp`, `getByVillage`) have `.catch()` error handlers. If the backend is unreachable or returns an error, the user will see no feedback.

---

## 🟡 Minor / Code Quality Issues

### 13. Pagination functions redefined on every View click

**File:**
- `history-controllers.js:84-96`

**Description:**
`figureOutTodosToDisplay()` and `pageChanged()` are defined **inside** the `showdate()` promise callback. Every time the user clicks "View", new function objects are assigned to the scope. These should be defined once at controller init.

---

### 14. Village filter selection does nothing

**Files:**
- `history-list.html:47-50`
- `history-controllers.js:22-30`

**Description:**
The Village dropdown (`ng-model="filter.village"`) is rendered in the filter panel, but the `filter()` function only reads `selectedDistrict`, `selectedMandal`, and `select_gp`. The village value is never used.

---

### 15. Cascade dropdowns not cleared on parent change

**File:**
- `history-controllers.js:137-154`

**Description:**
When the user changes District, `getMandalOnSelect()` updates `mandal_list`, but `gp_list` and `village_list` from the previous selection remain visible. Same issue when changing Mandal.

---

### 16. `$scope.start_date` and `$scope.end_date` assigned but never used

**File:**
- `history-controllers.js:60-61, 107-108`

**Description:**
Both `showdate()` and `export_history()` assign `$scope.start_date` and `$scope.end_date` from the date picker, but these scope variables are never referenced in the template or any subsequent logic. Dead code.

---

### 17. `getAll()` factory method is dead code

**File:**
- `history-factory.js:9-11`

**Description:**
`obj.getAll()` calls `/meter/meter_data_list/` but is never invoked from any controller. Dead code.

---

### 18. `dcu_id` and `dcu_name` both set from the same CSV column

**File:**
- `FileUtil.java:108, 114`

**Description:**
In `getMeterData()`, both fields are populated from `tmp[1]`:
```java
ui_obj.setDcu_id(tmp[1]);     // line 108 — DCU identifier
ui_obj.setDcu_name(tmp[1]);   // line 114 — should be DCU name, not ID
```
The `dcu_name` should come from a separate name column in the CSV, not the same column as the ID.

---

### 19. Locale options object contains hardcoded dates instead of config

**File:**
- `history-controllers.js:38-39`

**Description:**
Inside the `locale` options block:
```javascript
startDate: "04/22/2013",
endDate: "04/28/2020",
```
These hardcoded dates appear to be mistakenly placed in the locale config. The date range picker likely ignores these fields within `locale`. They appear to be leftover from an older implementation or a copy-paste error. They should either be removed or replaced with proper `minDate`/`maxDate` options outside the `locale` block.

---

### 20. No loading indicator when fetching data

**File:**
- `history-controllers.js`
- `history-list.html`

**Description:**
When the user clicks "View" for a large date range, CSV file reading can take several seconds. There is no spinner, progress bar, or any visual feedback to indicate that data is being loaded.

---

### 21. Shared bugs carried over from `FileUtil.java`

**File:**
- `FileUtil.java:24, 42, 53`

**Description:**
The History page shares the same CSV utility as the Events page, including:
- **30-day hardcoded limit** (line 42) — date ranges beyond 30 days are truncated
- **Double-slash in base path** (lines 24, 53) — `BASE_PATH = "/home/data/dontdelete/"` + `FILE_SPRATER = "/"` produces `//` in file paths

See `events-page-bugs.md` (bugs 4 and 9) for details.

---

## Summary

| Severity | Count | Key Items |
|---|---|---|
| 🔴 Critical | 4 | Undefined `gateway_identifier`, missing DCU validation, export corrupts table, duplicate power/consumption columns |
| 🟠 Major | 8 | 3 wrong sort fields (`name`, `rphase_current`, `power_factor`), 3 wrong date presets, deprecated API, no error handling |
| 🟡 Minor | 9 | Pagination redefinition, unused village/cascade, dead code, CSV parsing duplicates, locale date debris, no loading indicator, shared FileUtil bugs |

### Recommended Fix Priority

1. **Critical bugs** (1–4) — These cause crashes, corrupted views, or completely wrong data.
2. **Sort columns** (5–7) — Three sortable columns have no effect; sort breakage is immediately visible to users.
3. **Date presets** (8–10) — Misleading ranges cause users to view wrong data.
4. **Export + error handling** (3, 11, 12) — Export corrupts the page; all API calls need error handling.
5. **Code quality** (13–21) — Maintainability and future-proofing.
