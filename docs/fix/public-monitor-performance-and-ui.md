# Public monitor performance and UI fix

## Scope

The public monitor route (`/public_monitor`) now has a lighter startup path, consistent marker categories, and a responsive layout while leaving the authenticated dashboard route and deployment configuration unchanged.

## Implemented changes

- Removed automatic mandal and GP requests on initial page load. These lists are loaded only after their parent filter changes.
- Added manual-mode data to the public map response and restored manual/offline marker filters.
- Matched the logged-in dashboard map flow with dashboard-style pin icons, response-driven map creation, dataset centering, and marker population.
- Kept one marker per device with multiple status categories, coordinate validation, stale map-request protection, loading/error/empty states, and encoded query parameters.
- Added responsive public-monitor CSS for the header, map, statistics panels, and legend on narrow screens.
- Added an `ALL` district option so the default public filter is visible and selectable.

## API compatibility

The existing public endpoints remain unchanged:

- `GET /dashboard/count`
- `GET /dashboard/map_data`
- `GET /filter/get_mandal`
- `GET /filter/get_gp`

The map response now also includes `manual_mode_status`; existing clients can ignore the additional field.

## Verification

- AngularJS tests: 88 passed.
- Java controller tests: 16 passed (`MonitorControllerTest`, `AuthInterceptorTest`).
- Deployment-critical Docker, proxy, and environment files were not changed.
