# MongoDB Authentication and Panel ID Type Mismatch Fix

## Summary

This fix addresses two production failures:

1. The SERVER attempted a MongoDB authentication flow using empty credentials even though the deployment uses a no-authenticated MongoDB instance.
2. Alphanumeric panel IDs such as `CSA9388` were read into Java `int` fields and failed during Spring Data MongoDB conversion.

## Changes made

### MongoDB configuration

The SERVER service no longer receives empty `SPRING_DATA_MONGODB_USERNAME` or
`SPRING_DATA_MONGODB_PASSWORD` environment variables from `docker-compose.yml`.

For the current deployment, `MONGODB_USERNAME` and `MONGODB_PASSWORD` should remain
unset. MongoDB host, port, and database configuration are unchanged.

MongoDB authentication was not enabled or changed by this fix.

### Panel ID model

`panel_unique_id` is now a `String` in:

- SERVER Netty `HandShake`
- SERVER Netty `DCU`
- CCMS UI `DCU`

The CCMS UI `HandShake` model was already a `String`.

Panel IDs are business identifiers, not protocol numbers. Numeric-looking IDs such
as `9388` and alphanumeric IDs such as `CSA9388` are therefore preserved as text.

The existing ownership boundary remains unchanged: the UI writes `panel_unique_id`,
while Netty updates protocol/status fields and does not overwrite the UI-owned panel
ID during handshake updates.

## Data migration

No migration or normalization is included. Existing string values remain unchanged,
and new UI writes use the string model. Existing numeric BSON values should be
verified for compatibility in the target Spring Data version before production
rollout. A migration is only required if that compatibility check fails or if the
team later elects to enforce a uniform BSON type for historical records.

Do not convert alphanumeric values to zero or delete them.

## Tests added

- SERVER model coverage for alphanumeric and numeric-looking panel IDs.
- SERVER repository coverage confirming an alphanumeric ID survives handshake
  processing and is not overwritten by the Netty update.
- CCMS UI model coverage for alphanumeric and numeric-looking panel IDs.

Run the full verification suite with:

```bash
bash scripts/run-all-tests.sh
```

## Deployment verification

After deployment, verify that:

- SERVER logs contain no MongoDB authentication error code 18.
- No `ConversionFailedException` or `NumberFormatException` occurs for
  `panel_unique_id`.
- Gateway `1903SO1P1C014877` processes handshakes successfully.
- `CSA9388` displays and edits correctly in the UI.
- Reconnects do not erase the panel ID.
- Schedule and configuration operations continue to work.

The MongoDB backup should be retained until these checks complete.
