# DCU addition and update workflow fixes

## Scope

This change rectifies the active DCU add, update, map, action-button, and delete flows. Existing REST endpoints, HTTP methods, route paths, and request compatibility were retained.

## Changes implemented

1. DCU identity is normalised on create/update. The existing `serial_number` payload remains accepted, while `gateway_serial_number` and the Mongo document id are populated when needed.
2. The UI add form now binds the required identity field correctly and prevents duplicate submissions.
3. Add and update handlers inspect the returned `Status.code`; HTTP 200 responses containing a business failure no longer show a false success or redirect.
4. Update submission now uses the real update handler, removes the static disabled state from the Save button, and preserves form validation.
5. Update state parameters are handled consistently for both object and serial-number inputs.
6. The map code now initialises immediately, uses valid fallback coordinates, and updates the Angular latitude/longitude model when the marker is dragged.
7. The invalid `latlong` element and undefined `infoWindow` references were removed.
8. Delete waits for the API result, reports failures, and reloads only after a successful delete.
9. Delete lookup supports both the Mongo `_id` and `gateway_serial_number`, covering records created by either flow.
10. UI edits now use Mongo updates for editable fields instead of replacing the complete handshake document. Netty/protocol fields are therefore preserved during DCU updates.
11. Removed the stale `MakePristineTrue()` binding from the active Add form. The function was not defined by `dcuAddControllers`, causing an AngularJS error while entering the serial number and preventing the Add flow from completing reliably.

## Compatibility

The following communication contracts were not changed:

- `/dcu/create`, `/dcu/delet_dcu_id/{id}`, and all existing DCU action endpoints.
- Existing HTTP methods and response body shape (`Status.code` and `Status.message`).
- Legacy clients that submit `serial_number` instead of `gateway_serial_number`.
- Existing handshake records and fields written by the server/Netty communication flow.

## Verification

- `mvn -q -Dtest=DCUControllerTest test` — passed, 16 tests.
- JavaScript syntax check for `dcu-controllers.js` — passed.
- Added AngularJS regression coverage for canonical add payloads and business-failure handling in `src/test/javascript/controllers/dcu-controller.test.js`.
- `npm test -- --single-run` — passed, 91/91 tests. The Karma configuration excludes the legacy duplicate DCU controller so the active implementation is tested.
- `node --check app/dcu/dcu-controllers.js` — passed after removing the undefined Add-form change handler.
- `git diff --check` — passed.
