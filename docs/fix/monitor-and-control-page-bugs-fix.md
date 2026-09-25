# Monitor and Control Page Bug Fixes

## Scope

This change addresses the monitor-and-control page defects documented in `docs/bugs/monitor-and-control-page-bugs.md`.

## Implemented fixes

- Corrected the No Output checkbox to filter on `red_phse_no_output`.
- Fixed switchpoint navigation to pass the DCU gateway serial identifier expected by the switchpoint page.
- Changed Refresh to reload the current monitor query instead of calling the undefined `login()` function.
- Renamed the light action to `toggle_light`, added defensive checks, confirmed status updates, and visible success/error feedback.
- Removed the unused undefined-meter request, dead DCU dropdown/filter code, dead `show_more` state, and undefined `userFactory` delete action.
- Corrected Today, Yesterday, Last 7 Days, and Last Month date-range presets.
- Fixed the final-page check so pagination does not request an unnecessary empty page.
- Cleared dependent mandal, GP, and village selections when a parent hierarchy selection changes.
- Added a village selector and passed village filtering through the monitor count and device-list APIs.
- Moved device-list pagination into MongoDB with `skip` and `limit`; the Java controller no longer loads the full matching collection before slicing it.
- Added explicit loading, empty-result, and API-error states so a failed or empty monitor request is no longer rendered as a blank page.
- Made device-command forwarding use the existing `backend.http.port` property instead of hardcoded port `8102`.
- Restored the saved authentication token on API requests so the monitor data request is authorized after navigation or reload.
- Added visible loading failures for unauthorized/forbidden requests, backend failures, and empty result sets instead of silently leaving the page blank.
- Changed monitor data backend failures from a silent `200`/null response to an error response that the UI can handle.
- Made light commands return `400` for invalid parameters, `404` for unknown DCUs, `502` for downstream command failures, and `200` only after the command is delivered.
- Updated the UI to change the displayed light state only after a successful command response and to prevent duplicate commands while one is pending.

## API changes

`GET /dashboard/count` and `POST /dashboard/instant_data_filter` now accept:

- `village`, defaulting to `ALL`.
- `page` and `size` for `instant_data_filter` as before, now applied in the MongoDB query.

Existing callers that omit `village` continue to use `ALL`.

## Blank-page handling

The Monitor & Control page now shows a clear error when the device-list or count request fails, including authorization failures. A successful empty response shows a no-data message, while successful DCU responses remain rendered in the existing card list. Backend device-list failures now return an HTTP 500 response instead of a null successful response, allowing the UI to distinguish failure from an empty result.


Light command endpoints now expose the command outcome through the HTTP status and response body. The monitor page displays the corresponding success or failure message and preserves the previous state when the command is not accepted.

## Tests

Verified with:

```bash
cd CCMS_UI/STARTUP/ccms_ui
mvn test -q
npm test

cd ../../../SERVER/ccms
mvn test -q
```

Results:

- Maven UI tests: monitor endpoint regression coverage updated; 91 tests passed.
- Karma frontend tests: monitor error-state and rendering coverage added; 86 specs passed.
- JavaScript syntax checks passed for the monitor controller and factory.
- Maven server tests: 81 tests passed.
- JavaScript syntax checks passed for the monitor controller, factory, and application interceptor.
