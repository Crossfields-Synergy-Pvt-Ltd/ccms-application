# MongoDB Authentication and Panel ID Type Mismatch

## Scope

This document covers two related production issues observed in `cspl-server`:

1. A false MongoDB authentication failure during SERVER startup.
2. A MongoDB deserialization failure when an alphanumeric panel ID such as `CSA9388` is read as an integer.

No deployment or data changes should be made from this document without backup and verification.

## Issue 1: MongoDB authentication failure

### Observed error

```text
MongoCommandException: Authentication failed.
server mongodb:27017
code: 18
codeName: AuthenticationFailed
```

The same startup sequence later reports a successful MongoDB connection and completes the handshake-field migration. This indicates that the failure is caused by inconsistent MongoDB client configuration rather than MongoDB network unavailability.

### Root cause

The SERVER loads two Spring contexts:

- Spring Boot auto-configuration.
- A legacy XML context created by `MainBootApp`.

The Compose file always passes these variables to the SERVER, including when they resolve to empty strings:

```yaml
SPRING_DATA_MONGODB_USERNAME: ${MONGODB_USERNAME:-}
SPRING_DATA_MONGODB_PASSWORD: ${MONGODB_PASSWORD:-}
```

Spring Boot interprets the empty values as an authentication configuration and attempts to authenticate against the default MongoDB deployment, which has authentication disabled. The XML context creates a host/database Mongo client without credentials and can still connect successfully.

Relevant files:

- `docker-compose.yml`
- `SERVER/ccms/docker-entrypoint.sh`
- `SERVER/ccms/conf/applicationContext-docker.xml`
- `SERVER/ccms/src/main/java/com/vetsoft/ccms/MainBootApp.java`

The current deployment documentation confirms that MongoDB authentication is disabled by default and that empty authentication variables should not be supplied.

### Correction plan

For the current no-auth MongoDB deployment:

1. Stop injecting empty `SPRING_DATA_MONGODB_USERNAME` and `SPRING_DATA_MONGODB_PASSWORD` variables into the SERVER service.
2. Remove blank `MONGODB_USERNAME` and `MONGODB_PASSWORD` values from Hostinger environment variables; leave them unset.
3. Keep the existing MongoDB host, port, and database values:

   ```text
   mongodb
   27017
   ccms
   ```

4. Do not enable MongoDB authentication as part of this correction.
5. If authentication is required in the future, configure credentials consistently in every Mongo client, including the XML-defined clients and authentication database.

The Compose file is infra-critical and requires owner/PR review before modification.

### Validation

After deployment:

- No `Authentication failed` or MongoDB error code `18` appears in SERVER logs.
- `HandshakeFieldMigration` completes successfully.
- SERVER starts HTTP on port `8102` and Netty on port `9100`.
- SERVER and UI can read and write MongoDB data.
- The application healthcheck passes.

## Issue 2: Alphanumeric panel ID mapped to integer

### Observed error

```text
ConversionFailedException: Failed to convert from type [java.lang.String]
to type [int] for value 'CSA9388'

Caused by: NumberFormatException: For input string: "CSA9388"
```

The error occurs while processing gateway `1903SO1P1C014877`, after the handshake is persisted and the SERVER reads the document back from MongoDB.

### Root cause

The MongoDB document contains an alphanumeric business identifier:

```text
panel_unique_id: "CSA9388"
```

The model types are inconsistent:

| Component | `panel_unique_id` type |
|---|---|
| SERVER Netty `HandShake` | `int` |
| CCMS UI `HandShake` | `String` |
| SERVER `DCU` | `int` |
| CCMS UI `DCU` | `int` |
| UI form | Text/alphanumeric input |

The immediate failing declaration is:

```text
SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/pojo/HandShake.java
```

The UI handshake model already declares the value as `String`.

The failing persistence flow is:

1. `HandShakeParser` parses a valid gateway handshake.
2. `DeviceRequestDataRepository.addOrUpdateHandShake()` performs a MongoDB upsert.
3. The repository calls `mongoTemplate.findOne(..., HandShake.class)`.
4. Spring Data maps the complete MongoDB document to the SERVER `HandShake` model.
5. Mapping fails when `"CSA9388"` is converted to `int`.

The Netty handshake update intentionally does not overwrite the UI-owned `panel_unique_id`, so the existing alphanumeric value remains in MongoDB and exposes the model mismatch.

Relevant files:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/pojo/HandShake.java`
- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/pojo/DCU.java`
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/HandShake.java`
- `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/pojo/DCU.java`
- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/repos/DeviceRequestDataRepository.java`

### Correction plan

1. Change SERVER Netty `HandShake.panel_unique_id` from `int` to `String`.
2. Update the getter and setter signatures.
3. Align the SERVER and UI `DCU` models to use `String` for the same field.
4. Review all callers for numeric arithmetic or comparisons. The field is a business/display identifier and is not used as a protocol numeric value.
5. Preserve the existing repository ownership boundary: Netty should not overwrite the UI-owned panel ID during handshake updates.
6. Keep the read-after-upsert initially and verify that documents containing both string and numeric legacy values can be read successfully.

### MongoDB data inspection

Before any data migration, back up the `ccms` database and inspect the field types:

```javascript
db.handshake_info.count({ panel_unique_id: { $type: 2 } })
db.handshake_info.count({ panel_unique_id: { $type: 16 } })
db.handshake_info.count({ panel_unique_id: { $type: 18 } })

db.handshake_info.find(
  { panel_unique_id: { $type: 2 } },
  { _id: 1, gateway_serial_number: 1, panel_unique_id: 1 }
)
```

Do not convert values such as `CSA9388` to `0` or delete them.

Changing the model to `String` should preserve existing string values without a migration. If numeric and string values cannot be read consistently after the model change, perform a controlled MongoDB 3.4-compatible normalization migration:

- Run only after backup.
- Preserve each original value as text.
- Log the number of changed records.
- Make the migration idempotent.
- Do not modify `_id`, gateway serial numbers, schedules, protocol fields, or installation dates.

The existing `HandshakeFieldMigration` classes currently handle field-name migrations only and should not be expanded until live type counts are known.

## Tests and verification

Add focused SERVER coverage for:

- Deserializing a handshake with `panel_unique_id: "CSA9388"`.
- Deserializing a handshake with a numeric legacy panel ID.
- Running `addOrUpdateHandShake()` with an existing alphanumeric panel ID.
- Confirming the upsert and read-after-upsert complete without `ConversionFailedException`.
- Confirming the Netty update does not erase the UI-owned panel ID.

Run the repository test entry point:

```bash
bash scripts/run-all-tests.sh
```

Deployment verification should confirm:

- No MongoDB authentication error code `18`.
- No `ConversionFailedException` or `NumberFormatException` for `panel_unique_id`.
- Successful handshake processing for gateway `1903SO1P1C014877`.
- Successful UI display and editing of an alphanumeric panel ID.
- Successful DCU schedule and configuration operations after reconnect.

## Rollback

- Restore the MongoDB backup if a data migration changes values incorrectly.
- Roll back SERVER/UI images if the model change causes API incompatibility.
- Revert deployment configuration only after confirming whether the live MongoDB instance uses authentication.

## Recommended order

1. Back up MongoDB.
2. Remove the false empty-credential authentication path.
3. Align `panel_unique_id` to `String` across the models.
4. Run mapping and repository tests.
5. Inspect live MongoDB field types.
6. Add a data normalization migration only if required by the test results.
7. Deploy to a test environment.
8. Verify startup, handshake persistence, UI panel IDs, and DCU synchronization.
9. Deploy to production and monitor the relevant error patterns.
