# History Page Bug Fixes

Implemented the fixes documented in `docs/bugs/history-page-bugs.md`.

## Frontend

- Removed unused `gateway_identifier` click arguments and added DCU selection validation.
- Added loading, export, error, and notification states for history and filter requests.
- Prevented CSV export responses from replacing the displayed table data.
- Replaced deprecated AngularJS `$http.success()`/`$http.error()` usage with promise-based handling.
- Corrected sorting fields for DCU name, date, current, power factor, and consumption.
- Corrected Today, Yesterday, Last 7 Days, This Month, and Last Month date ranges.
- Moved pagination functions to controller initialization so they are not recreated per request.
- Cleared dependent GP and Village selections when parent filters change.
- Added Village to the DCU filter query and connected it to the backend.
- Removed obsolete locale date values and the unused factory `getAll()` method.

## Backend and CSV data

- `FileUtil` now reads every day in the requested inclusive date range instead of stopping after 30 days.
- Reversed ranges return no files, and CSV paths no longer contain a double slash.
- Meter CSV fields now follow the `FileLogger` layout:
  - column 0: DCU identifier
  - column 1: DCU name
  - column 8: instantaneous power (`kwh_total`)
  - column 10: cumulative consumption (`consumption`)
- History export headers now map the name and consumption fields correctly.
- Added an optional Village-aware DCU lookup through the dashboard service and DAO while retaining existing callers.

## Tests

Added or updated coverage for:

- DCU validation, history loading, pagination, and export table preservation.
- 31-day and reversed date ranges.
- CSV ID/name/power/consumption mappings.

Verification commands:

```bash
cd CCMS_UI/STARTUP/ccms_ui
npm test -- --single-run
mvn test
```

Results at implementation time:

- Frontend: 81 specs passed.
- Java: 93 tests passed.
