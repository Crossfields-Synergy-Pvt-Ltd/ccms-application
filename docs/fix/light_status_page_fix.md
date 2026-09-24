# Light Status Page Fix

## Scope

The Light Status page now has a working backend and a consistent frontend API contract.

## Backend

- Added `ModifiedIOController` under `/modified_io`.
- Added DAO and service layers for querying MongoDB `io_data` records by DCU and `DD/MM/YYYY` date range.
- Added a corrected `ModifiedIOPojo` response model with `operation_*` fields.
- Added `/modified_io/export` CSV download support.
- Enabled the Modified IO beans in both development and Docker Spring configurations.

The response node value is the stored `node_id`, because the existing node model does not contain a separate display-name field.

## Frontend

- Updated the Light Status factory to call the date-filtered list and export endpoints.
- Removed unused list API code.
- Renamed the export action to `exportLightStatus`.
- Updated the page heading, breadcrumb, and navigation label to `LIGHT STATUS`.
- Corrected date and operation field bindings to match the backend response.
- Preserved DCU validation, loading/error feedback, stable pagination, and non-destructive export behavior.

## Verification

- AngularJS/Karma: 81 specs passed.
- Maven: 90 tests passed.
