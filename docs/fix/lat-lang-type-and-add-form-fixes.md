# Lat/Lang Type Fix and Add DCU Form Bug Fixes

## Summary

Two bugs in the Add DCU workflow and a type mismatch between SERVER and MongoDB for lat/lang fields:

1. **`dcu.long` vs `dcu.lang`** — The Add form bound the longitude input to `dcu.long`, but the POJO field is `lang`. The value was silently dropped during JSON deserialization, so longitude was always saved as `null`.
2. **No error handling on Add/Update submit** — `alert()` was used for debugging with an undefined argument, the `dcuFactory.add()` promise was ignored (fire-and-forget), and `$state.reload()` was called redundantly before navigation.
3. **`float` vs `String` type mismatch** — SERVER `HandShake.java` declared lat/lang as `float`, but MongoDB stores them as `String` in all 12,898 documents that have these fields. This caused potential type instability and cast errors on round-trip serialization.

## Root Cause — dcu.long

In `dcu-add.html:323`, the input was:

```html
<input ... ng-model="dcu.long" />
```

The POJO (`ServerHandShake.java` / `DCU.java` in CCMS_UI) has:

```java
private String lang;
```

When AngularJS sends `{ "long": "84.1031" }` to the server, there is no setter `setLong()` — only `setLang()`. Jackson silently ignores the unknown property, so longitude was always `null` on save.

The Update form (`dcu-update.html:296`) already uses `dcu.lang` correctly — the Add form was inconsistent.

## Root Cause — No Error Handling

The `ok()` function in `dcu-controllers.js:259`:

```javascript
$scope.ok = function (schedules_name) {
    $scope.schedules_name = schedules_name;
    alert($scope.schedules_name)       // debug leftover, undefined
    $scope.dcu;                         // no-op expression
    dcuFactory.add($scope.dcu);         // promise ignored
    $state.reload();                    // redundant
    $state.go('dashboard.dcu');         // navigates unconditionally
};
```

- `alert()` shows the undefined `schedules_name` parameter that was never populated
- The `dcuFactory.add()` promise is not chained — if the POST fails, the user still gets navigated away without any error feedback
- `$state.reload()` followed by `$state.go()` is redundant; `$state.go()` already handles navigation

The update form's `$scope.update()` had the same pattern.

## Root Cause — float vs String

SERIAL HandShake.java:35 declared:

```java
private float lat;
private float lang;
```

But every document in MongoDB's `handshake_info` collection stores lat/lang as strings:

```
> db.handshake_info.findOne({}, {lat: 1, lang: 1})
{ "lat": "18.5163", "lang": "84.1031" }

> db.handshake_info.aggregate([
    {$match: {lat: {$exists: true}}},
    {$group: {_id: {$type: "$lat"}, count: {$sum: 1}}}
])
{ "_id": "string", "count": 12898 }
```

Zero documents have `double`, `int`, or `float` typed lat/lang.

The hex parsing in `HandShakeParser.java:102` produced an `int` (e.g., `156401`), which Spring would try to persist as `Number`. This clashed with existing string data and type inference.

## Files Modified

| # | File | Change |
|---|---|---|
| 1 | `CCMS_UI/.../dcu/dcu-add.html` | `ng-model="dcu.long"` → `ng-model="dcu.lang"` |
| 2 | `CCMS_UI/.../dcu/dcu-controllers.js` | Injected `inform`, rewrote `ok()` and `update()` with promise handling |
| 3 | `SERVER/.../netty/pojo/HandShake.java` | `float lat/lang` → `String lat/lang` |
| 4 | `SERVER/.../netty/parser/hs/HandShakeParser.java` | `Integer.parseInt()` → `String.valueOf(Integer.parseInt())` |
| 5 | `SERVER/.../netty/repos/DeviceRequestDataRepository.java` | `update.set()` → `update.setOnInsert()` for lat/lang |

### 1. dcu-add.html

```diff
- <input ... ng-model="dcu.long" />
+ <input ... ng-model="dcu.lang" />
```

Now matches the POJO field name used in `dcu-update.html:296` and in both `CCMS_UI/.../ServerHandShake.java` and `CCMS_UI/.../DCU.java`.

### 2. dcu-controllers.js — Add Controller

```diff
-dcuCntl.controller('dcuAddControllers', function(..., dcuFactory, config) {
+dcuCntl.controller('dcuAddControllers', function(..., dcuFactory, inform, config) {
```

```diff
- $scope.ok = function (schedules_name) {
-     $scope.schedules_name = schedules_name;
-     alert($scope.schedules_name)
-     $scope.dcu;
-     dcuFactory.add($scope.dcu);
-     $state.reload();
-     $state.go('dashboard.dcu');
+ $scope.ok = function () {
+     dcuFactory.add($scope.dcu).then(function (response) {
+         inform.add("DCU saved successfully", {ttl: 3000, type: "success"});
+         $state.go('dashboard.dcu');
+     }, function (error) {
+         inform.add("Failed to save DCU: " + (error.data || error.statusText), {ttl: 5000, type: "danger"});
+     });
 };
```

Changes:
- `inform` injected for toast notifications
- Removed unused `schedules_name` parameter
- Removed `alert()` — `inform.add()` shows a success toast instead
- Removed `$scope.dcu;` no-op statement
- `dcuFactory.add()` promise is now chained: navigates on success, shows error on failure
- Removed redundant `$state.reload()` — `$state.go('dashboard.dcu')` is sufficient

### 2. dcu-controllers.js — Update Controller

Same pattern applied to `dcuUpdateControllers`:

```diff
-dcuCntl.controller('dcuUpdateControllers', function(..., dcuFactory, config) {
+dcuCntl.controller('dcuUpdateControllers', function(..., dcuFactory, inform, config) {
```

```diff
- $scope.update = function () {
-     $scope.dcu;
-     dcuFactory.add($scope.dcu);
-     $state.reload();
-     $state.go('dashboard.dcu');
+ $scope.update = function () {
+     dcuFactory.add($scope.dcu).then(function (response) {
+         inform.add("DCU updated successfully", {ttl: 3000, type: "success"});
+         $state.go('dashboard.dcu');
+     }, function (error) {
+         inform.add("Failed to update DCU: " + (error.data || error.statusText), {ttl: 5000, type: "danger"});
+     });
 };
```

### 3. HandShake.java

```diff
- private float lat;
- private float lang;
+ private String lat;
+ private String lang;
```

Getters/setters updated to match:

```diff
- public float getLat() { return lat; }
- public void setLat(float lat) { this.lat = lat; }
+ public String getLat() { return lat; }
+ public void setLat(String lat) { this.lat = lat; }
```

The `toString()` method still works since string concatenation handles `String` values naturally.

### 4. HandShakeParser.java

```diff
- hand_shake_request.setLat(Integer.parseInt(
-     buffer.substring(index, (index + 8)), 16));
+ hand_shake_request.setLat(String.valueOf(Integer.parseInt(
+     buffer.substring(index, (index + 8)), 16)));
```

- The hex bytes are still parsed as an integer (to validate the hex and handle future scaling)
- The result is converted to `String` to match the MongoDB field type
- Lat and long are both changed identically

### 5. DeviceRequestDataRepository.java

```diff
+ update.setOnInsert("lat", user.getLat());
+ update.setOnInsert("lang", user.getLang());
  update.set("crc", user.getCrc());
```

Added `$setOnInsert` before the existing `$set` calls. This means:

- **First handshake (insert):** Lat/lang from handshake hex is stored as a raw integer string (e.g., `"156401"`)
- **User edits via UI:** `mongoTemplate.save()` in `DCUDaoImpl.addHandShake()` overwrites with the user-entered decimal string (e.g., `"15.6401"`)
- **Subsequent handshakes (upsert):** `$setOnInsert` sees the field already exists and does NOT overwrite it — user values are preserved

This is the same `$setOnInsert` pattern already used for `installation_date` on line 78.

## DB Verification

Before the change:

```
handshake_info count: 17,450
  - lat/lang = String:   12,898 docs
  - lat/lang = missing:   4,552 docs
  - lat/lang = float:          0 docs
  - lat/lang = double:         0 docs
  - lat/lang = int:            0 docs
```

No data migration is needed since all existing documents already have `String`-typed lat/lang. The type change only affects future handshake upserts.

## Test Results

All 217 tests pass:

| Suite | Tests | Status |
|---|---|---|
| SERVER (Maven) | 76 | PASS |
| CCMS_UI (Maven) | 75 | PASS |
| CCMS_UI (Karma JS) | 66 | PASS |

## Deployment

Rebuild and restart both SERVER and CCMS_UI:

```bash
docker compose build server ccms_ui
docker compose up -d server ccms_ui
```

Or rebuild individual images:

```bash
docker compose build server
docker compose up -d server

docker compose build ccms_ui
docker compose up -d ccms_ui
```

No database migration step required — all existing data is already `String`-typed.
