# Operational Hours Report Page — Bug Report

> Generated from code review on 2026-07-21
> Scope: Full operational hours stack (AngularJS frontend + Java Spring backend + CSV file data access)
> Covers both sub-pages: Operational Hours (`#/operationalhour`) and Light Status / Modified Operational Hours (`#/modified_operationalhour`)

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
| 1 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/operationalhours/operationalhours-list.html` | Operational Hours HTML template |
| 2 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/operationalhours/operationalhours-controllers.js` | Operational Hours AngularJS controller |
| 3 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/operationalhours/operationalhours-factory.js` | Operational Hours AngularJS factory |
| 4 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/modified_operationalhours/modified_operationalhours-list.html` | Light Status HTML template |
| 5 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/modified_operationalhours/modified_operationalhours-controllers.js` | Light Status AngularJS controller |
| 6 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/modified_operationalhours/modified_operationalhours-factory.js` | Light Status AngularJS factory |
| 7 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/IOController.java` | Spring MVC REST controller (`/io/*`) |
| 8 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/file/FileDao.java` | CSV file data access |
| 9 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/file/FileUtil.java` | CSV file reader and parser (`getIOUiData`) |
| 10 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/IOUIObject.java` | IO data response POJO |
| 11 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/util/IOStatusTmpPojo_delete.java` | Intermediate IO processing POJO |
| 12 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/util/IOUIObject_delete.java` | Legacy IO response POJO |
| 13 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/User.java` | User entity (`operational_hour` privilege) |
| 14 | `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/UserController.java` | Privilege assignment |
| 15 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/app.js` | AngularJS state router |
| 16 | `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/navi/leftnavi.html` | Navigation menu |

---

## 🔴 Critical Bugs

### 1. Static mutable field `previous_Op_Val` corrupts data across concurrent requests

**File:**
- `IOController.java:34, 110-147`

**Description:**
```java
static int previous_Op_Val = 0;
```

This is a **static field** in a Spring singleton `@Controller`. The `getIODetails()` method at line 110 compares `obj.getOpration_value()` against this static field and **skips entries** (returns null) if they match the previous value. Since the field is shared across all HTTP requests, concurrent users querying different DCUs will interfere with each other's results. Entries are randomly dropped depending on request interleaving. This is a classic shared-mutable-state concurrency bug.

Additionally, the method returns `null` for consecutive identical operation values. This dedup logic is itself broken — it skips adjacent duplicates but the field carries over between requests, so even non-adjacent values in a single response can be incorrectly skipped.

**Fix:**
Remove the static field entirely. Restructure the algorithm to use a local variable:
```java
int previousOpVal = 0;
for(IOPojo tmp : io_list) {
    IOStatusTmpPojo_delete obj = getIODetails(tmp, previousOpVal);
    if(obj != null) {
        tmp_io_list.add(obj);
        previousOpVal = obj.getOpration_value();
    }
}
```

---

### 2. `getIODetails()` never sets `date`, `hour`, or `min` — commented-out date parsing

**File:**
- `IOController.java:110-147`

**Description:**
The date/time extraction from the IO data's MongoDB `_id` timestamp is entirely **commented out**:

```java
public static IOStatusTmpPojo_delete getIODetails(IOPojo obj) {
    /*final DateTimeFormatter formatter = ...        // COMMENTED OUT
    long seconds = Long.valueOf(...)                 // COMMENTED OUT
    final String formattedDtm = ...                  // COMMENTED OUT
    int hour = ...                                   // COMMENTED OUT
    int min = ... */                                 // COMMENTED OUT
    ...
    IOStatusTmpPojo_delete tmp_obj = new IOStatusTmpPojo_delete();
    /*tmp_obj.setDate(formattedDtm);                 // COMMENTED OUT
    tmp_obj.setHour(hour);                           // COMMENTED OUT
    tmp_obj.setMin(min); */                          // COMMENTED OUT
    tmp_obj.setOpration_value(obj.getOpration_value());
    return tmp_obj;
}
```

The returned object only has `opration_value` set. The `/get_io_details/{id}` endpoint produces `IOUIObject_delete` objects where `on_hour_min`, `off_hour_min`, and `date` are all null or derived from zero values. This endpoint is non-functional — it cannot compute ON/OFF times from the IO data because the timestamp-to-hour-minute conversion was removed.

**Fix:**
Uncomment and fix the date/time parsing logic. The `seconds` value is extracted from the MongoDB ObjectId (`obj.getId().substring(0, 10)` converted to epoch seconds). Restore the `Instant.ofEpochSecond()` parsing.

---

### 3. `gateway_identifier` is undefined in View button calls

**Files:**
- `operationalhours-list.html:98`
- `modified_operationalhours-list.html:43`
- `operationalhours-controllers.js:57`
- `modified_operationalhours-controllers.js:36`

**Description:**
Both View buttons pass `gateway_identifier` as a parameter:
```html
ng-click="showdate(gateway_identifier);"
```

The variable `gateway_identifier` is **never defined** in either controller scope. Inside both `showdate()` functions, the code reads `$scope.selected_dcu.name.gateway_identifier` directly from the scope, so the passed parameter is completely unused and evaluates to `undefined`. This is the same bug pattern as Events page bug #1 and History page bug #1.

**Fix:**
Remove the parameter from both `ng-click` directives:
```html
ng-click="showdate();"
```

---

### 4. No DCU selection validation — silent TypeError

**Files:**
- `operationalhours-controllers.js:58, 74, 102`
- `modified_operationalhours-controllers.js:49, 89`

**Description:**
Both `showdate()` and `export_operationalhour()` access `$scope.selected_dcu.name.gateway_identifier` without checking if `$scope.selected_dcu.name` exists:
```javascript
console.log($scope.selected_dcu.name.gateway_identifier)   // line 58
$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + ...  // lines 74, 120
```

If no DCU is selected from the `ui-select` dropdown, this throws a **TypeError** (`Cannot read property 'gateway_identifier' of undefined`) and the operation silently fails with no user feedback.

**Fix:**
Add a guard clause at the top of all four functions:
```javascript
if (!$scope.selected_dcu || !$scope.selected_dcu.name) {
    return;
}
```

---

## 🟠 Major Bugs

### 5. Export response overwrites the table data display (both pages)

**Files:**
- `operationalhours-controllers.js:123-124`
- `modified_operationalhours-controllers.js:91-92`

**Description:**
Both `export_operationalhour()` functions assign the API response to `$scope.list`:
```javascript
operationalFactory.getAllExport($scope.qs_params).then(function(data) {
    $scope.list = data.data;  // overwrites table data with CSV text!
});
```

The `/io/export_operationalhour` endpoint returns **CSV binary data**. The factory method handles the actual file download in its `.success()` callback (blob creation, link click), but the controller's `.then()` still fires and assigns the raw CSV string to `$scope.list`. This **corrupts the table view** by replacing the paginated operational hours data with CSV text. Same pattern as History page bug #3.

**Fix:**
Remove the `.then()` assignment in both controllers. The export should only trigger the download, not modify scope:
```javascript
$scope.export_operationalhour = function() {
    // ... date formatting ...
    operationalFactory.getAllExport($scope.qs_params);
};
```

---

### 6. "Yesterday" date range is duplicated, "Today" preset missing

**Files:**
- `operationalhours-controllers.js:48-53`
- `modified_operationalhours-controllers.js:23-28`

**Description:**
Both controllers define identical duplicate entries for "Yesterday" and no "Today" preset:
```javascript
ranges: {
    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
    'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],  // duplicate
    'Last 7 Days': [moment().subtract(6, 'days'), moment()],
    'This Month': [moment().startOf('month'), moment().endOf('month')],
    'Last Month': [moment().subtract(29, 'days'), moment()]
}
```

The duplicate "Yesterday" key overwrites the first entry (JavaScript object dedup), so only one "Yesterday" entry appears. There is no "Today" shortcut, forcing users to manually select today's date.

**Fix:**
Replace the duplicate with a "Today" entry:
```javascript
'Today': [moment().startOf('day'), moment()],
```

---

### 7. "Last Month" is actually "Last 30 Days"

**Files:**
- `operationalhours-controllers.js:53`
- `modified_operationalhours-controllers.js:28`

**Description:**
```javascript
'Last Month': [moment().subtract(29, 'days'), moment()],
```
This is a 30-day rolling window ending today, not the previous calendar month. For example, on July 21, "Last Month" should be June 1–June 30.

**Fix:**
```javascript
'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
```

---

### 8. Export uses deprecated `.success()/.error()` API

**Files:**
- `operationalhours-factory.js:36-62`
- `modified_operationalhours-factory.js:23-48`

**Description:**
Both `getAllExport` factory methods use AngularJS's deprecated `.success()` and `.error()` promise methods (removed in AngularJS 1.6+). Should be replaced with standard `.then()` and `.catch()` for forward compatibility. Same pattern as Events/History pages.

---

### 9. No error handling on any API call

**Files:**
- `operationalhours-controllers.js:15-17, 27-29, 32-34, 77-97, 123-124, 133-152`
- `modified_operationalhours-controllers.js:32-34, 52-72, 91-92`

**Description:**
None of the factory calls (`getAll`, `getAllDcuNames`, `getAllOperationalHourByDate`, `getAllExport`, `getByMandal`, `getByGp`, `getByVillage`, `getAllById`) have `.catch()` error handlers. If the backend is unreachable or returns an error, the user sees no feedback.

---

### 10. Hardcoded DCU serial number on controller init

**File:**
- `operationalhours-controllers.js:15`

**Description:**
```javascript
operationalFactory.getAll('1905HY3P3C000904').then(function(data){
    $scope.listData = data.data;
});
```

A specific DCU serial number (`1905HY3P3C000904`) is hardcoded. This will fail for any deployment that does not have this exact DCU. Additionally, the fetched data is assigned to `$scope.listData` which is **never referenced** in the template — it is dead code.

**Fix:**
Remove this dead code block entirely.

---

### 11. Pagination functions redefined on every View click

**Files:**
- `operationalhours-controllers.js:85-96`
- `modified_operationalhours-controllers.js:60-71`

**Description:**
`figureOutTodosToDisplay()` and `pageChanged()` are defined **inside** the promise callback of `showdate()`. Every time the user clicks "View", new function objects are created and assigned to the scope. Same pattern as Events/History pages.

**Fix:**
Move these function definitions to the top level of the controller (outside any callback).

---

### 12. "Node" column always displays "R-Phase" — hardcoded

**Files:**
- `operationalhours-list.html:196`
- `modified_operationalhours-list.html:127`

**Description:**
```html
<td align="center">R-Phase</td>
```
This is hardcoded text. It should display the actual node name from the backend data, as different DCUs may have different phase configurations.

---

### 13. Cumulative ON/OFF hours stored as strings — numeric sort broken

**File:**
- `FileUtil.java:210-211`

**Description:**
```java
ui_obj.setCumulative_on_hour(tmp[6]);   // raw CSV string
ui_obj.setCumulative_off_hour(tmp[7]);  // raw CSV string
```

Both cumulative fields are raw strings from CSV. The `IOUIObject` POJO declares them as `String`. When users click the "Cumulative ON Hours" or "Cumulative OFF Hour" column headers, AngularJS's `orderBy` performs **lexicographic** sorting instead of numeric. For example, `"100"` sorts before `"9"`.

**Fix:**
Parse integers in the CSV parser:
```java
try {
    ui_obj.setCumulative_on_hour(String.valueOf(Integer.parseInt(tmp[6])));
} catch (NumberFormatException e) {
    ui_obj.setCumulative_on_hour("0");
}
```
Or change the POJO field type to `int` and handle conversion.

---

### 14. No loading indicator during API calls

**Files:**
- `operationalhours-list.html`
- `operationalhours-controllers.js`
- `modified_operationalhours-list.html`
- `modified_operationalhours-controllers.js`

**Description:**
When the user clicks "View" or "Export" for a large date range, CSV file reading can take several seconds. There is no spinner, progress bar, or any visual feedback on either page.

---

## 🟡 Minor / Code Quality Issues

### 15. `getByID()` factory method is unused

**File:**
- `operationalhours-factory.js:20-22`

```javascript
obj.getByID = function(qs_params){
    return $http.get(serviceBase + '/events/events_between_date/'+ qs_params);
};
```
This method calls `/events/events_between_date/` but is never invoked from any controller. Dead code.

---

### 16. Village filter selection does nothing

**File:**
- `operationalhours-list.html:47-50`
- `operationalhours-controllers.js:22-30`

The Village dropdown (`ng-model="filter.village"`) is rendered in the filter panel, but the `filter()` function only reads `selectedDistrict`, `selectedMandal`, and `select_gp`. The village value is never used.

---

### 17. Unclosed `<span>` tag used as closing `</label>`

**File:**
- `operationalhours-list.html:71, 87`

```html
<label class="col-md-2">Data Concentrator Unit</span></label>
<label class="col-md-1 " style="margin-top: 5px; padding-left: 10px">Date range</span>
```
Both `<label>` tags are closed with `</span>` instead of `</label>`.

---

### 18. Typo "Cumilative" in table header

**File:**
- `operationalhours-list.html:163`

The column header displays "Cumilative ON Hours" — should be "Cumulative ON Hours".

---

### 19. Typo "opration" in field names throughout Light Status page

**Files:**
- `modified_operationalhours-list.html:93, 97, 98, 102, 103, 111, 115, 116`
- `IOStatusTmpPojo_delete.java:16, 34, 38, 66, 70`

All field names use `opration_*` instead of `operation_*` — `opration_type`, `opration_value`, `opration_resone`. These are embedded in the database schema and the AngularJS sort bindings. Changing them requires coordinated changes across MongoDB collections, the POJO, and the frontend.

---

### 20. Hardcoded inline CSS from dev session

**File:**
- `operationalhours-list.html:92`

```html
<input date-range-picker ... style="top: 112px; left: 681.583px; right: auto; display: block;" />
```
Absolute positioning coordinates left over from a specific screen resolution during development. These will misposition the input on different screen sizes.

---

### 21. No unit tests

No test files exist for either `operationalhours-controllers.js`, `operationalhours-factory.js`, `modified_operationalhours-controllers.js`, or `modified_operationalhours-factory.js`, unlike other pages which have corresponding test files.

---

### 22. `$scope.listData` assigned but never used

**File:**
- `operationalhours-controllers.js:15-17`

The hardcoded DCU fetch assigns results to `$scope.listData`, but this scope variable is never referenced in the template or any other logic. Dead code (related to bug #10).

---

### 23. Light Status page heading mismatch

**File:**
- `modified_operationalhours-list.html:6-8`

The page heading displays "LIGHT STATUS" while the breadcrumb label in `app.js:426` says "MODIFIED_OPERATIONALHOUR". These are inconsistent — a user navigating from the "Light States" menu entry will see a heading that does not match the breadcrumb or the URL.

---

### 24. Cascade dropdowns not cleared on parent change

**File:**
- `operationalhours-controllers.js:132-152`

When the user changes the District selection, `getMandalOnSelect()` updates `mandal_list`, but `gp_list` and `village_list` from the previous selection remain visible. The same issue applies when changing Mandal — `village_list` is not cleared.

---

## Summary

| Severity | Count | Items |
|---|---|---|
| 🔴 Critical | 4 | Static field concurrency bug, commented-out date parsing, undefined `gateway_identifier` var, missing DCU validation |
| 🟠 Major | 10 | Export corrupts table, duplicate/absent date presets, wrong "Last Month", deprecated API, no error handling, hardcoded DCU ID, pagination redefined, hardcoded "R-Phase", string-numeric sort mismatch, no loading indicator |
| 🟡 Minor | 10 | Unused factory method, unused village filter, mismatched HTML tags, "Cumilative" typo, "opration" typo fields, hardcoded styles, no tests, dead code, heading mismatch, cascade not cleared |

### Recommended Fix Priority

1. **Critical bugs (1–4)** — Static field causes random data corruption; date parsing regression breaks `/get_io_details`; undefined variables and missing guards cause crashes.
2. **Export corruption (5)** — Clicking Export destroys the table view.
3. **Date presets (6, 7)** — Misleading/absent date range options.
4. **Hardcoded values (10, 12)** — Production deployment may lack the hardcoded DCU.
5. **Error handling (8, 9, 14)** — User feedback and forward compatibility.
6. **Data correctness (13)** — Numeric sort gives wrong column ordering.
7. **Code quality (15–24)** — Maintainability and correctness.
