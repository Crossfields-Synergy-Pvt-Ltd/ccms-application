# DCU/RTU Configuration Page Fixes

Date: 2026-09-24

## Implemented

- Fixed DCU configuration Apply so it keeps the AngularJS scope and navigates only after the save Promise completes.
- Delayed default configuration reload until the async request completes.
- Added success/failure notifications for configuration and sync operations.
- Added confirmation before bulk node and scheduler synchronization.
- Bulk sync now reports failed DCUs instead of always returning success.
- Normalized configuration-sync detail responses to an empty list when the API returns null or a non-array.
- Routed the direct delete helper through the confirmation flow.
- Corrected serial-number sorting and the configuration tooltip text.
- Removed generated AngularJS validation classes from the modified configuration template.
- Added a heading to the Default DCU Configuration page.
- Moved default configuration storage into the separate `dcu_default_system_conf_details` collection.
- Kept legacy ID `100` as a read fallback and use `default` in the new collection.
- Kept the existing `sync_schduler_conf` endpoint spelling for compatibility.
- Did not change the configurable backend HTTP port; it already uses `backend.http.port` instead of a hardcoded `8102`.

## Communication impact

Sync error propagation is a REST orchestration change between the UI backend and server push endpoints. It does not change the DCU binary protocol, packet format, or Netty device handlers.

The default configuration change is MongoDB persistence/data isolation, not a communication-protocol change.

## Migration note

Existing deployments can continue reading legacy default data under ID `100`. Saving the Default DCU Configuration writes to the isolated collection. Verify and save the default configuration once after deployment before retiring any legacy document.

## Verification

- Node syntax checks pass for the edited AngularJS controllers.
- AngularJS Karma suite: 84 tests passed.
- CCMS UI Maven suite: 90 tests passed.
