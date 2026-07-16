# Backend Search for Monitor & Control and DCU Configuration

## Summary

Two search-related bugs that prevented operators from locating specific DCUs across 17,451 devices:

1. **Monitor & Control search was frontend-only** — The search box only filtered the 50 items already loaded in the browser (`$scope.handshake_Data`). With 17,451 total DCUs spread across ~350 pages, a DCU like `CSA03923` could never be found unless it happened to appear on the currently loaded page.
2. **DCU Configuration search used a broken regex on a single field** — `getHandShakeByIDWithFilter()` only matched against the `name` field and used a malformed regex (`name+ '*'`) that produced incorrect MongoDB query patterns. Searching by gateway serial number (e.g., `1903SO1P1C014099`) always failed.

## Root Cause — Monitor & Control Frontend-Only Search

In `monitorandcontrol-controllers.js:97-103`:

```javascript
if (searchText) {
    var textMatch = false;
    if (item.device_name && item.device_name.toLowerCase().indexOf(searchText) !== -1) textMatch = true;
    if (item.id && item.id.toLowerCase().indexOf(searchText) !== -1) textMatch = true;
    if (item.dcu_details && item.dcu_details.name && item.dcu_details.name.toLowerCase().indexOf(searchText) !== -1) textMatch = true;
    if (!textMatch) continue;
}
```

This loop runs against `$scope.handshake_Data`, which only contains the **most recently loaded page** of 50 DCUs. The `search()` function rebuilt the district/mandal/gp query and reloaded page 0 — but never sent the search text to the backend.

The count endpoint (`/dashboard/count`) also returned the **total** for all 17,451 devices regardless of any search, making it impossible for users to know whether their search matched anything.

## Root Cause — DCU Config Malformed Regex

In `DashBoardDaoImpl.java:551`:

```java
query.addCriteria(Criteria.where("name").regex(dcu_name+ '*'));
```

This produced a MongoDB query like `{ name: { $regex: "1903SO1P1C014099*" } }`. In regex syntax, `*` means "zero or more of the preceding character," not "match anything starting with this string." For a 16-character serial number, this regex was effectively meaningless and would match very few (if any) documents.

Additionally, the search was limited to the `name` field only. DCUs are often referred to by their gateway serial number (`1903SO1P1C014099`) or MongoDB `_id`, neither of which was searched.

## Files Modified

| # | File | Change |
|---|---|---|
| 1 | `CCMS_UI/.../dao/DashBoardDao.java` | Added `String search` parameter to `getDahsBoardCountstats()` and `getAllHandShakeData()` |
| 2 | `CCMS_UI/.../dao/DashBoardDaoImpl.java` | Implemented `$or` regex search on `name`, `gateway_serial_number`, and `_id` in both count and list queries; fixed `getHandShakeByIDWithFilter()` |
| 3 | `CCMS_UI/.../services/DashBoardServices.java` | Updated interface signatures with `search` param |
| 4 | `CCMS_UI/.../services/DashBoardServicesImpl.java` | Passed `search` through to DAO |
| 5 | `CCMS_UI/.../controller/MonitorController.java` | Accepted `search` query param in `/count` and `/instant_data_filter`; forwarded to service |
| 6 | `CCMS_UI/.../app/monitorandcontrol/monitorandcontrol-factory.js` | Added `search` argument to `getAllCount()` and `getAllHandShake()`; appended `&search=` to URLs |
| 7 | `CCMS_UI/.../app/monitorandcontrol/monitorandcontrol-controllers.js` | Passed `searchFish` (>= 3 chars) to factory; frontend-only filtering kept for < 3 char queries |
| 8 | `CCMS_UI/.../controller/MonitorControllerTest.java` | Updated `getDahsBoardCountstats` mock calls from 5 to 6 params to match new `search` parameter |

### 1. DashBoardDao.java

```diff
- public MonitorControlCount getDahsBoardCountstats(String district, String mandal, String gp, Date startDate, Date endDate) throws Exception;
+ public MonitorControlCount getDahsBoardCountstats(String district, String mandal, String gp, Date startDate, Date endDate, String search) throws Exception;

- public List<HandShake> getAllHandShakeData(String district, String mandal, String gp, Date startDate, Date endDate)  throws Exception;
+ public List<HandShake> getAllHandShakeData(String district, String mandal, String gp, Date startDate, Date endDate, String search)  throws Exception;
```

### 2. DashBoardDaoImpl.java — Count Query

```diff
  if (startDate != null && endDate != null) {
      query.addCriteria(Criteria.where("installation_date").gte(startDate).lte(endDate));
  }
+ if (search != null && !search.isEmpty()) {
+     query.addCriteria(new Criteria().orOperator(
+         Criteria.where("name").regex(search, "i"),
+         Criteria.where("gateway_serial_number").regex(search, "i"),
+         Criteria.where("_id").regex(search, "i")
+     ));
+ }
  List<HandShake> list = mongoTemplate.find(query, HandShake.class);
```

Same block added to `getAllHandShakeData()`.

### 3. DashBoardDaoImpl.java — DCU Config Search Fix

```diff
- query.addCriteria(Criteria.where("name").regex(dcu_name+ '*'));
+ query.addCriteria(new Criteria().orOperator(
+     Criteria.where("name").regex(dcu_name, "i"),
+     Criteria.where("gateway_serial_number").regex(dcu_name, "i"),
+     Criteria.where("_id").regex(dcu_name, "i")
+ ));
```

This changes the DCU Configuration search from a single-field malformed regex to a proper three-field case-insensitive contains search.

### 4. MonitorController.java — /count

```diff
  @RequestMapping(value = "/count", method = RequestMethod.GET)
  public @ResponseBody MonitorControlCount getDashBoardCounts(
      ...
+     @RequestParam(value = "search", required = false) String search
  ) {
      ...
-     return dashBpardService.getDahsBoardCountstats(district, mandal, gp, startDate, endDate);
+     return dashBpardService.getDahsBoardCountstats(district, mandal, gp, startDate, endDate, search);
  }
```

### 5. MonitorController.java — /instant_data_filter

```diff
  @RequestMapping(value = "/instant_data_filter", method = RequestMethod.POST)
  public @ResponseBody List<DCUInstantData> getAllDevicesInstantDataByFilter(
      ...
+     @RequestParam(value = "search", required = false) String search,
      ...
  ) {
      ...
-     List<HandShake> dcu_list = dashBpardService.getAllHandShakeData(district, mandal, gp, startDate, endDate);
+     List<HandShake> dcu_list = dashBpardService.getAllHandShakeData(district, mandal, gp, startDate, endDate, search);
  }
```

### 6. monitorandcontrol-factory.js

```diff
- obj.getAllCount = function(qs_params){
-     return $http.get(serviceBase + '/dashboard/count'+ qs_params);
+ obj.getAllCount = function(qs_params, search){
+     var url = serviceBase + '/dashboard/count'+ qs_params;
+     if (search && search.length > 0) url += '&search=' + encodeURIComponent(search);
+     return $http.get(url);
  }

- obj.getAllHandShake = function(qs_params, page, size){
-     return $http.post(serviceBase + '/dashboard/instant_data_filter'+ qs_params + '&page=' + page + '&size=' + size);
+ obj.getAllHandShake = function(qs_params, page, size, search){
+     var url = serviceBase + '/dashboard/instant_data_filter'+ qs_params + '&page=' + page + '&size=' + size;
+     if (search && search.length > 0) url += '&search=' + encodeURIComponent(search);
+     return $http.post(url);
  }
```

### 7. monitorandcontrol-controllers.js

**Load page with search:**

```diff
  $scope.loadPage = function(page) {
      ...
+     var searchParam = ($scope.searchFish && $scope.searchFish.length >= 3) ? $scope.searchFish : null;
-     monitorandcontrolFactory.getAllHandShake($scope.qs_params, page, $scope.pageSize).then(...)
+     monitorandcontrolFactory.getAllHandShake($scope.qs_params, page, $scope.pageSize, searchParam).then(...)
  };
```

**Search button click:**

```diff
  $scope.search = function() {
      ...
+     var searchParam = ($scope.searchFish && $scope.searchFish.length >= 3) ? $scope.searchFish : null;
-     monitorandcontrolFactory.getAllCount($scope.qs_params).then(...)
+     monitorandcontrolFactory.getAllCount($scope.qs_params, searchParam).then(...)
  };
```

**Frontend-only filtering adjustment:**

```diff
- if (searchText) {
+ if (searchText && searchText.length < 3) {
```

Queries of 3+ characters are now handled by the backend. Queries of 1-2 characters still use the existing frontend-only filter on the loaded page (to avoid heavy backend regex queries on extremely short strings).

### 8. MonitorControllerTest.java — Test Signature Fix

When `getDahsBoardCountstats()` gained the 6th `search` parameter, the unit test `MonitorControllerTest.java` was not updated, causing **test compilation failure** (even with `-DskipTests`, Maven still compiles test sources):

```
[ERROR] method getDahsBoardCountstats in interface DashBoardServices cannot be applied to given types;
  required: java.lang.String,java.lang.String,java.lang.String,java.util.Date,java.util.Date,java.lang.String
  found: java.lang.String,java.lang.String,java.lang.String,<nulltype>,<nulltype>
```

**Fix:** Added a 6th `null` argument for `search` to all 5 mock calls in `MonitorControllerTest.java` (lines 55, 69, 77, 88, 122).

```diff
- when(dashBpardService.getDahsBoardCountstats("ALL", "ALL", "ALL", null, null))
+ when(dashBpardService.getDahsBoardCountstats("ALL", "ALL", "ALL", null, null, null))
```

## Search Behavior

| Query Length | Backend Search | Frontend Filter | Count Updated |
|---|---|---|---|
| Empty / cleared | No | No | Total for selected filters |
| 1-2 chars | No | Yes (loaded page only) | Total for selected filters |
| 3+ chars | Yes (all 17,451 DCUs) | Yes (on returned results) | Matching count only |

**Fields searched:** `name`, `gateway_serial_number`, `_id` — all case-insensitive contains.

## Verification

- Search `CSA03923` on Monitor & Control page — should return matching DCU immediately without paging
- Search `1903SO1P1C014099` on DCU Configuration page — should return matching DCU
- Clear search box and click SEARCH — should reset to all DCUs for selected district/mandal/gp
- Count banner (`MONITOR AND CONTROL (N)`) should show matching count when search is active
- All existing checkbox filters (MCB Trip, ON, OFF, etc.) continue to work on top of search results
- Maven compile passes with `BUILD SUCCESS`

## Deployment

Rebuild and restart the `ccms_ui` container:

```bash
docker compose build ccms_ui
docker compose up -d ccms_ui
```

No database migration required — the search uses existing indexed fields (`name`, `gateway_serial_number`, `_id`).

## Notes

- No HTML changes were needed — the existing search input, SEARCH button, and pagination controls in `monitorandcontrol-list.html` already had the correct wiring.
- The `$or` + `$regex` query pattern works on MongoDB 3.4 (the version used in this deployment).
- `_id` is included in the search because MongoDB document IDs in this collection are the same as `gateway_serial_number` for most DCUs, providing a fallback match.
