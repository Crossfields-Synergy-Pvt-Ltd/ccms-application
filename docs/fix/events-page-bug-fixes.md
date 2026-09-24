# Events Page Bug Fixes

## Scope

This change addresses the events page issues documented in
[events-page-bugs.md](../bugs/events-page-bugs.md).

## Implemented

- Removed the unused `gateway_identifier` click arguments.
- Added DCU selection validation and user-facing error messages.
- Corrected the Today, Yesterday, Last 7 Days, and Last Month date ranges.
- Moved pagination functions outside the event-load callback.
- Added loading/export state handling and disabled duplicate actions.
- Added error handling for DCU, hierarchy, event, and export requests.
- Added village filtering to the DCU query.
- Reset dependent Mandal, GP, and Village selections when their parent changes.
- Replaced deprecated AngularJS `success()`/`error()` download handling.
- Return HTTP 204 when an event export has no rows.
- Removed the unused `/events/event_list` endpoint and factory method.

## Existing fixes confirmed

The report described two issues that were already fixed in the repository:

- File date iteration is not limited to 30 days.
- CSV paths do not contain a double slash because `BASE_PATH` has no trailing slash.

## Verification

Frontend coverage is in
`CCMS_UI/STARTUP/ccms_ui/src/test/javascript/controllers/event-controller.test.js`.

Backend coverage is in
`CCMS_UI/STARTUP/ccms_ui/src/test/java/com/vnetsoft/ccms/controller/EventsControllerTest.java`.

Run:

```bash
cd CCMS_UI/STARTUP/ccms_ui
npm test
mvn test
```
