# Super-admin database clear

## Summary

The dashboard now provides a `Clear Database` action for users whose role is
`ADMIN` or `SUPER ADMIN`. It clears application data from MongoDB so the dashboard starts
fresh, while preserving users and saved schedules.

## Behavior

The action calls `POST /dashboard/clear_database` after two client-side
confirmations:

1. The administrator confirms the destructive operation.
2. The administrator types `CLEAR`.

The backend performs the authorization check independently of the UI. It reads
the authenticated email from the verified token, loads that user from MongoDB,
and requires the exact role `ADMIN` or `SUPER ADMIN`. Other requests receive
HTTP 403.

The backend enumerates MongoDB collections and deletes documents from every
application collection except:

- `ccms_user_details` — user accounts and login credentials
- `scheduler_details` — saved schedules
- `schedule_conf_data` — schedule configuration data

MongoDB-managed `system.*` collections are also left untouched. This means
`dcu_details`, handshake data, meter data, events, IO data, hierarchy data,
device configuration, and system configuration are cleared.

After a successful clear, the dashboard reloads its current filters. It will
remain empty until DCUs reconnect and begin sending fresh data. Active DCU
connections may repopulate data immediately, which is expected.

## Deployment note

The service bean is registered in both the development Spring configuration and
the Docker-specific `conf/spring-config-docker.xml`. The Docker image must be
rebuilt and redeployed for the endpoint to be available in production.

The seed service can restore `mongo.archive` when it is explicitly run with
`SEED_DATA=true`; that operation is separate from the dashboard button. Avoid
rerunning the seed service after a clear unless restoring the archive is
intended.

## Verification

- Java tests: `mvn test -q` from `CCMS_UI/STARTUP/ccms_ui`
- AngularJS tests: `npm test -- --single-run` from `CCMS_UI/STARTUP/ccms_ui`
- Confirm that a super admin can clear data while users and schedules remain.
- Confirm that a non-super-admin receives HTTP 403.
