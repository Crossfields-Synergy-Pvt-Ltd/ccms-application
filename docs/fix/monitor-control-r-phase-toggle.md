# Monitor & Control — R-Phases Toggle Fix

## Summary

The "R-Phases" toggle button on the Monitor & Control page appeared unresponsive after using the SEARCH feature. Clicking the light ON/OFF image produced no visible feedback, making it seem broken.

## Root Causes

Four independent issues contributed to the problem:

| # | Issue | File | Line |
|---|---|---|---|
| 1 | `search()` called `loadPage(0)` without resetting `$scope.loading`, causing the guard `if ($scope.loading) return;` to silently skip the data reload | `monitorandcontrol-controllers.js` | 163 |
| 2 | `turn_on_light()` never updated `light_status` on the scope after the API succeeded — the user saw no visual change | `monitorandcontrol-controllers.js` | 207–213 |
| 3 | No error handling — failed API calls were swallowed silently | `monitorandcontrol-controllers.js` | 207–213 |
| 4 | `turn_on_light()` reused `$scope.qs_params` (shared with search/filter state), creating a fragile implicit coupling | `monitorandcontrol-controllers.js` | 204 |

## What Changed

### 1. Controller — `search()` resets `$scope.loading`

**Before:**
```javascript
$scope.loadPage(0);
```

**After:**
```javascript
$scope.loading = false;
$scope.loadPage(0);
```

This ensures that `loadPage(0)` is not silently dropped by the `if ($scope.loading) return;` guard when a prior page load is still in-flight.

### 2. Controller — `turn_on_light()` updates `light_status` on success

**Before:**
```javascript
monitorandcontrolFactory.turnOffLights($scope.qs_params).then(function(data){});
// ...
monitorandcontrolFactory.turnOnLights($scope.qs_params).then(function(data){});
```

**After:**
```javascript
monitorandcontrolFactory.turnOffLights(params).then(function(data){
    $scope.obj.dcu_details.light_status = 0;
}).catch(function(error){
    console.error('Failed to turn off light:', error);
});
// ...
monitorandcontrolFactory.turnOnLights(params).then(function(data){
    $scope.obj.dcu_details.light_status = 1;
}).catch(function(error){
    console.error('Failed to turn on light:', error);
});
```

On API success, `light_status` is flipped locally so the light ON/OFF image updates immediately. On failure, the error is logged and `light_status` stays unchanged.

### 3. Controller — Local variable replaces shared `$scope.qs_params`

**Before:**
```javascript
$scope.qs_params = '?device_serial_number=...';
monitorandcontrolFactory.turnOffLights($scope.qs_params)...
```

**After:**
```javascript
var params = '?device_serial_number=...';
monitorandcontrolFactory.turnOffLights(params)...
```

Prevents accidental corruption of the search/filter query string.

## Files Changed

| File | Change |
|---|---|
| `CCMS_UI/STARTUP/ccms_ui/src/main/webapp/app/monitorandcontrol/monitorandcontrol-controllers.js` | 3 edits (~12 lines changed) |
| `CCMS_UI/STARTUP/ccms_ui/src/test/javascript/controllers/monitorandcontrol-controller.test.js` | Updated mocks, added 3 new tests (~30 lines changed) |

## Tests Added/Updated

| Test | What It Verifies |
|---|---|
| `should call turnOnLights when light_status is 0 and update status to 1 on success` | After API success, `light_status` becomes 1 |
| `should call turnOffLights when light_status is 1 and update status to 0 on success` | After API success, `light_status` becomes 0 |
| `should not update light_status on API failure` | After API failure, `light_status` stays unchanged |
| `should reset loading flag before loading page` | `$scope.loading` is reset before `loadPage(0)` in `search()` |

## Verification

- Unit tests pass: `karma start CCMS_UI/STARTUP/ccms_ui/karma.conf.js`
- Manual: Search a DCU, click the R-Phases light image → the image toggles ON/OFF instantly
- Manual: With server unreachable, click the R-Phases light image → no state change, error logged to console
