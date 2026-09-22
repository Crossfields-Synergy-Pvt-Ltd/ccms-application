# Public Monitor Page Fixes

## Summary

Fixed the public monitor map, filters, dashboard statistics, installation-date filtering, and related API/UI inconsistencies.

## Implemented fixes

### Map and filters

- Consolidated initial-load and search marker rendering into one code path.
- Added support for high-voltage, high-current, offline, MCB, ON, and OFF marker categories.
- Changed each device to one marker with multiple categories, avoiding duplicate markers.
- Added coordinate validation so malformed map records are skipped safely.
- Reused the existing Google Map instance instead of recreating it for every search.
- Added request sequencing so stale search responses cannot overwrite newer results.
- Added loading, empty-result, and API-error states.
- Encoded district, mandal, GP, and cascading-filter values before sending requests.
- Reset dependent mandal and GP selections when a parent selection changes.
- Removed the undefined GP-change handler.

### Map data and security

- Extended `MapData` with device identity, display, load, light-count, and offline fields.
- Corrected map popup data after filtering/searching.
- Escaped device name and landmark values before inserting them into popup HTML.
- Replaced the hardcoded `Device Status: OKAY` text with online/offline status.

### Dashboard counts

- Counted contactor failures from `cnt_status`.
- Counted no-output events from `red_phse_no_output`.
- Added high-current count and calculated glow rate.
- Corrected public monitor labels so active energy, manual-mode devices, high-current alerts, and glow rate represent the returned fields.
- Removed non-functional Download buttons.

### Date filtering

- Corrected Today, Yesterday, Last 7 Days, This Month, and Last Month presets.
- Made the backend end date an exclusive next-day boundary so the selected end date is fully included.
- Applied the same date-boundary behavior to the related DCU list endpoints.

## Tests

Updated coverage includes:

- Public monitor marker-category regression coverage.
- URL encoding coverage for location filters.
- Map response field and offline-status coverage.
- Inclusive end-date API coverage.

Commands used:

```bash
cd CCMS_UI/STARTUP/ccms_ui
npm test -- --single-run
mvn test -Dtest=MonitorControllerTest,AuthInterceptorTest -DfailIfNoTests=false
```

