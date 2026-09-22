# Switch Point Information Page Fixes

## Scope

Fixed the bugs documented in `docs/bugs/switchpoint-page-bugs.md`.

## Backend changes

- `MonitorController` now includes the selected `HandShake` as `dcu_details` in `/dashboard/instant_data_id/{id}` responses.
- `DCUInstantData` now exposes the matching `DCUConfiguration` as `dcu_configurations`.
- The response also includes the DCU's `SchedulerConfiguration` as `schedule_configuration`, resolved through the handshake's schedule name.
- A missing DCU now returns an empty response safely instead of dereferencing a null handshake.

These changes are in the CCMS UI Spring application. No changes were made to the Netty/Spring Boot `SERVER/ccms` module or deployment configuration.

## Frontend changes

- State navigation now consumes `gateway_serial_number`, selects the matching dropdown entry, and loads the page automatically.
- Light status can be toggled using the existing light-control endpoints.
- The edit action now passes the gateway serial number instead of the full handshake object.
- Phase power uses the instantaneous `active_power` field; voltage remains bound to `r_phase_voltage`.
- Configuration and schedule values are loaded from the backend rather than hardcoded.
- Connected load, light count, overload, voltage, supply, RTU, and connectivity indicators use device data.
- Loading, error, empty-state, and stale-data handling were added.

## Tests

Added coverage for:

- Instant-data response fields in `MonitorControllerTest`.
- State-parameter navigation and automatic loading.
- Error handling and stale-data clearing.
- Edit navigation serial-number handling.
- Switchpoint light-control factory methods.

The backend test passes with 12 tests and 0 failures:

```text
mvn test -Dtest=MonitorControllerTest
```

The JavaScript files pass `node --check`. The full Karma suite passes with 72 tests and 0 failures when run with the local Chrome test server enabled.
