# Map performance, typed DCU lookup, and user validation

## Scope

This change addresses:

- Faster dashboard/public map startup and marker rendering.
- Faster dashboard count calculation by replacing one meter query per DCU with one batch query.
- Typed DCU input on Events and History instead of loading every DCU into a selector, with client and server validation for invalid or unknown gateway IDs.
- Required-field indicators and browser validation for Add User.

The DCU switch ON/OFF feedback issue is intentionally not included.

## Changes

### Map and dashboard performance

- Dashboard no longer requests the unused full DCU-name list during initialization.
- Dashboard active-load data is fetched in one MongoDB query for all matching DCUs.
- Public map markers use native Google Maps colored symbols for reliable status rendering and lower marker overhead.
- Existing map filters and API response formats remain unchanged.

### Events and History

Users enter the DCU number directly. The typed value is sent as the existing `id` parameter to the event/history endpoints, so the file-backed data lookup remains compatible.

The full `/dcu/dcu_name_list` request is no longer made when either page opens. The input accepts an exact gateway serial number, trims surrounding whitespace, and allows only letters, numbers, `.`, `_`, and `-`. Empty input and unsafe values are rejected before any request is sent. The Events and History filter controls remain wired to their current filter models.

### Typed DCU response handling

- View and export requests return `400` for invalid IDs, `404` for IDs not present in the DCU handshake data, `200` for valid IDs (including an empty result), and `500` for unexpected read failures.
- The UI reports these cases separately, so an unknown typed value is not presented as a successful empty search.
- File-backed lookup rejects unsafe path components as a second protection layer.

### Add User

First name, last name, email, password, and role now display a required `*`. Email is also marked required in the browser form, matching the server-side validation.

## Verification

- AngularJS tests: `84` tests passed.
- CCMS UI Java tests: `91` tests passed.
- Existing switch command behavior was not changed.

## Follow-up opportunities

For further map scaling, add MongoDB indexes for the dashboard filter fields and introduce marker clustering or viewport-based loading. Those changes require production-data benchmarking before implementation.
