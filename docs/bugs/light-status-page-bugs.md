# Light Status Page — Bug Report

> Generated from code review on 2026-07-21
> Scope: Full Light Status page stack (AngularJS frontend + Java Spring backend)
> Also known as: "Light States" (navigation menu), "Modified Operational Hours" (code/directory name), `dashboard.modified_operationalhour` (Angular state), `#/modified_operationalhour` (URL)

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
| 1 | `CCMS_UI/.../app/modified_operationalhours/modified_operationalhours-list.html` | HTML template |
| 2 | `CCMS_UI/.../app/modified_operationalhours/modified_operationalhours-controllers.js` | AngularJS controller |
| 3 | `CCMS_UI/.../app/modified_operationalhours/modified_operationalhours-factory.js` | AngularJS factory |
| 4 | `CCMS_UI/.../app/app.js` | AngularJS state router (lines 420-429) |
| 5 | `CCMS_UI/.../app/navi/leftnavi.html` | Navigation menu (lines 192-196, 336-342) |
| 6 | `CCMS_UI/.../conf/spring-config-docker.xml` | Docker Spring config (lines 95-99) |
| 7 | `CCMS_UI/.../src/main/webapp/WEB-INF/spring-config.xml` | Dev Spring config (lines 101-102) |

---

## 🔴 Critical Bugs

### 1. Entire backend API `/modified_io/*` has no implementation — View button does nothing

**Files:**
- `modified_operationalhours-factory.js:8-14`
- `spring-config-docker.xml:95-99`
- `spring-config.xml:101-102`

**Description:**
The factory calls `GET /modified_io/modified_io_list/` to fetch data:
```javascript
obj.getAllById = function(dcu_id){
    return $http.get(serviceBase + '/modified_io/modified_io_list/' + dcu_id);
}
```

However, the Spring beans that would implement this endpoint are **commented out** in both configuration files:
```xml
<!--
<bean id="ModifiedIOPojoDao" class="com.vnetsoft.ccms.dao.ModifiedIOPojoDaoImpl"></bean>
<bean id="ModifiedIOPojoServices" class="com.vnetsoft.ccms.services.ModifiedIOPojoServicesImpl"></bean>
-->
```

The implementation classes `ModifiedIOPojoDaoImpl` and `ModifiedIOPojoServicesImpl` **do not exist anywhere in the codebase**. No Java `@Controller` maps to `/modified_io/`. The entire backend data pipeline for the Light Status page is missing. Every View click produces a request to a non-existent endpoint.

**Fix:**
Either implement the `/modified_io/` backend (controller, DAO, services, POJO), or remove the page entirely if it is no longer needed.

---

### 2. `gateway_identifier` is undefined in View button call

**File:**
- `modified_operationalhours-list.html:43`
- `modified_operationalhours-controllers.js:36`

**Description:**
```html
<a type="button" ng-click="showdate(gateway_identifier);" ...>View</a>
```

The variable `gateway_identifier` is **never defined** in the controller scope. Inside `showdate()`, the code reads `$scope.selected_dcu.name.gateway_identifier` directly, so the parameter is unused and evaluates to `undefined`. Same pattern as Events page bug #1, History page bug #1, and Operational Hours bug #3.

**Fix:**
Remove the parameter from the `ng-click` directive:
```html
ng-click="showdate();"
```

---

### 3. No DCU selection validation — silent TypeError

**File:**
- `modified_operationalhours-controllers.js:49, 89`

**Description:**
Both `showdate()` and `export_operationalhour()` access `$scope.selected_dcu.name.gateway_identifier` without checking if `$scope.selected_dcu.name` exists:
```javascript
$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + ...;
```

If no DCU is selected, this throws a **TypeError** (`Cannot read property 'gateway_identifier' of undefined`) and the operation silently fails.

**Fix:**
Add a guard clause at the top of both functions:
```javascript
if (!$scope.selected_dcu || !$scope.selected_dcu.name) {
    return;
}
```

---

## 🟠 Major Bugs

### 4. Export response overwrites the table data display

**File:**
- `modified_operationalhours-controllers.js:91-92`

**Description:**
```javascript
modified_operationalFactory.getAllExport($scope.qs_params).then(function(data) {
    $scope.list = data.data;  // overwrites table data with CSV text!
});
```

The `/io/export_operationalhour` endpoint returns CSV binary data. The factory method handles the download in its `.success()` callback, but the controller's `.then()` assigns the raw CSV string to `$scope.list`, **corrupting the table view**.

**Fix:**
Remove the `.then()` assignment — export should only trigger the download.

---

### 5. "Yesterday" date range is duplicated, "Today" preset missing

**File:**
- `modified_operationalhours-controllers.js:23-28`

**Description:**
```javascript
ranges: {
    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],  // duplicate
    'Last 7 Days': [moment().subtract(6, 'days'), moment()],
    'This Month': [moment().startOf('month'), moment().endOf('month')],
    'Last Month': [moment().subtract(29, 'days'), moment()]
}
```

"Yesterday" is declared twice (the second overwrites the first) and there is no "Today" preset.

**Fix:**
Replace the duplicate with:
```javascript
'Today': [moment().startOf('day'), moment()],
```

---

### 6. "Last Month" is actually "Last 30 Days"

**File:**
- `modified_operationalhours-controllers.js:28`

**Description:**
```javascript
'Last Month': [moment().subtract(29, 'days'), moment()],
```
A 30-day rolling window ending today, not the previous calendar month.

**Fix:**
```javascript
'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
```

---

### 7. Export uses deprecated `.success()/.error()` API

**File:**
- `modified_operationalhours-factory.js:23-48`

**Description:**
The `getAllExport` method uses AngularJS's deprecated `.success()` and `.error()` promise methods (removed in AngularJS 1.6+).

---

### 8. No error handling on any API call

**File:**
- `modified_operationalhours-controllers.js:32-34, 52-72, 91-92`

**Description:**
None of the factory calls (`getAllDcuNames`, `getAllById`, `getAllExport`) have `.catch()` error handlers. The user sees no feedback when the backend is unreachable (especially critical given bug #1 — the backend endpoint doesn't exist).

---

### 9. Pagination functions redefined on every View click

**File:**
- `modified_operationalhours-controllers.js:60-71`

**Description:**
`figureOutTodosToDisplay()` and `pageChanged()` are defined **inside** the promise callback of `showdate()`. Every View click creates new function objects.

**Fix:**
Move these function definitions to the top level of the controller.

---

### 10. "Node" column always displays "R-Phase" — hardcoded

**File:**
- `modified_operationalhours-list.html:127`

**Description:**
```html
<td align="center">R-Phase</td>
```
This is hardcoded. It should display the actual node name from the backend data.

---

### 11. No loading indicator during API calls

**Files:**
- `modified_operationalhours-list.html`
- `modified_operationalhours-controllers.js`

**Description:**
No spinner or progress feedback is shown during data fetching.

---

## 🟡 Minor / Code Quality Issues

### 12. Typo "opration" in all field names

**Files:**
- `modified_operationalhours-list.html:93, 97, 98, 102, 103, 111, 115, 116`

**Description:**
All data field names use `opration_*` instead of `operation_*`:
- `opration_type` → should be `operation_type`
- `opration_value` → should be `operation_value`
- `opration_resone` → should be `operation_reason` (also misspelled "resone")

These are embedded in both the sort bindings (`ng-click="sortType = 'opration_type'"`) and the data bindings (`{{ obj.opration_type }}`), so they must match the backend field names.

---

### 13. Page heading mismatch

**File:**
- `modified_operationalhours-list.html:6-8`
- `app.js:426`

**Description:**
The page heading displays "LIGHT STATUS" but the breadcrumb label says "MODIFIED_OPERATIONALHOUR", the navigation menu says "Light States", and the URL is `#/modified_operationalhour`. Three different names for the same page.

---

### 14. Data binding field `obj.yymmdd` instead of `obj.date`

**File:**
- `modified_operationalhours-list.html:128`

**Description:**
```html
<td align="center">{{ obj.yymmdd }}</td>
```
The date column binds to `yymmdd` while the sibling Operational Hours page binds to `date`. This is inconsistent — if the backend returns a field named `date`, this will display nothing.

---

### 15. `getAll()` factory method is dead code

**File:**
- `modified_operationalhours-factory.js:12-14`

```javascript
obj.getAll = function(){
    return $http.get(serviceBase + '/modified_io/modified_io_list/');
}
```
This method calls the same endpoint without a DCU ID but is never invoked from any controller. Dead code.

---

### 16. No unit tests

No test files exist for `modified_operationalhours-controllers.js` or `modified_operationalhours-factory.js`.

---

### 17. Unclosed `<span>` tag used as closing `</label>`

**File:**
- `modified_operationalhours-list.html:17, 33`

```html
<label class="col-md-2 " >Data Concentrator Unit (DCU)</span></label>
<label class="col-md-1 " style="margin-top: 5px; padding-left: 10px">Date range</span>
```
Both `<label>` tags are closed with `</span>` instead of `</label>`.

---

### 18. Different date format from sibling page

**File:**
- `modified_operationalhours-controllers.js:39, 45`

**Description:**
The Light Status page formats dates as `YYYYMMDD` (no separators):
```javascript
var dayString = dayWrapper.format("YYYYMMDD");
```
The sibling Operational Hours page formats dates as `DD/MM/YYYY`. This is inconsistent and may confuse users who switch between the two reports.

---

### 19. Export button calls `export_operationalhour()` — inconsistent naming

**File:**
- `modified_operationalhours-list.html:49`

**Description:**
The Export button calls `export_operationalhour()` even though the page is called "Light Status". The function name is inherited from the sibling Operational Hours page and does not describe what this page does.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 3 | Backend `/modified_io/` endpoint has no implementation, undefined `gateway_identifier`, missing DCU validation |
| 🟠 Major | 8 | Export corrupts table, duplicate/absent date presets, wrong "Last Month", deprecated API, no error handling, pagination redefined, hardcoded "R-Phase", no loading indicator |
| 🟡 Minor | 8 | `opration_*` typos, heading mismatch, `yymmdd` binding, dead code, no tests, mismatched tags, inconsistent date format, misleading function name |

### Recommended Fix Priority

1. **Critical bug #1** — The backend is completely missing. The page cannot function without it.
2. **Critical bugs #2–3** — Undefined variables and missing guards cause crashes.
3. **Export corruption (#4)** — Clicking Export breaks the table.
4. **Date presets (#5, #6)** — Misleading/absent date range options.
5. **Error handling (#7, #8, #11)** — User feedback and forward compatibility.
6. **Code quality (#12–19)** — Maintainability and consistency.

### Root Cause Analysis

**Bug #1** is the most critical: the entire backend for the Light Status page was never implemented or was removed. The Spring beans for `ModifiedIOPojoDaoImpl` and `ModifiedIOPojoServicesImpl` are commented out in both Spring config files, and no Java controller maps to the `/modified_io/` URL prefix. The `getAllById()` and `getAll()` factory methods call endpoints that have no handler, making the View button non-functional regardless of frontend fixes. This page may be an incomplete feature or a remnant of an earlier architecture.
