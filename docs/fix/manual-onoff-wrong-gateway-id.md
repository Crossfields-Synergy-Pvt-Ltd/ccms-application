# Manual ON/OFF — Wrong Gateway ID in CI=6 Packet Fix

## Summary

The light ON/OFF button on the Monitor & Control page sent the Netty CI=6 command packet with `00000000` as the gateway ID instead of the DCU's actual gateway identifier (e.g., `2043`). The DCU received the command but ignored it because the gateway ID in the packet header didn't match its own ID.

## Root Causes

Two independent issues combined to produce the bug:

### 1. CCMS_UI forwarded wrong field (PRIMARY — user-facing)

The UI sent `dcu_details.serial_number` as `device_identifier`, but this field is **never populated from the DCU protocol** — it always defaults to `"00000000"` in the `HandShake` POJO.

### 2. SERVER never persisted gateway_identifier to MongoDB (SECONDARY — database)

The SERVER's `DeviceRequestDataRepository.addOrUpdateHandShake()` had `gateway_identifier` commented out on line 54. Even though the `HandShakeParser` correctly parsed the gateway ID from the DCU's packet header, the value was never written to MongoDB. Both SERVER and CCMS_UI share the same MongoDB (`cspl-mongodb`), so when CCMS_UI read the document, `gateway_identifier` was always `0`.

| Field | Value Sent | Actual Value Needed |
|---|---|---|
| `serial_number` (from UI) | `"00000000"` | N/A — always default |
| `gateway_identifier` (from DB) | `2043` (example) | This is what the DCU expects |

## Data Flow (Before Fix)

```
UI: obj.dcu_details.serial_number = "00000000"
  │
  ▼
DeviceConfigurationController: forwards as dcu_identifier=00000000
  │
  ▼
UIConfController: Integer.parseInt("00000000") → 0
  │
  ▼
ManuvalCommands: uses 0 as gateway ID → packet header = "00000000"
  │
  ▼
DCU receives packet, gateway ID doesn't match → command ignored
```

## What Changed

### Fix 1: SERVER — `DeviceRequestDataRepository.java`

**Line 54:** Uncommented the persistence of `gateway_identifier` so it is stored to MongoDB when a DCU handshakes.

**Before:**
```java
// update.set("gateway_identifier", user.getGateway_identifier());
```

**After:**
```java
update.set("gateway_identifier", user.getGateway_identifier());
```

Without this, the value was parsed from the DCU's protocol header but never persisted. Any subsequent read would return `0` (Java int default).

### Fix 2: CCMS_UI — `DeviceConfigurationController.java`

In both `turnOnLights()` and `turnOffLights()`, the `device_identifier` param from the UI is no longer forwarded to the SERVER. Instead, the correct `gateway_identifier` is fetched from MongoDB using the `device_serial_number` (= `gateway_serial_number`).

**Before:**
```java
String uri = "http://" + serverHost + ":8102/user/push/manuval_on?dcu_serial_number="
    + device_serial_number + "&dcu_identifier=" + device_identifier;
```

**After:**
```java
HandShake hand_shake = userServices.getHandShakeByID(device_serial_number);
int gatewayId = hand_shake.getGateway_identifier();

String uri = "http://" + serverHost + ":8102/user/push/manuval_on?dcu_serial_number="
    + device_serial_number + "&dcu_identifier=" + gatewayId;
```

This matches the pattern already used by `sync_dcu_configuration` (line 71-72), `sync_node_conf` (line 122), and `sync_schduler_conf` (line 163).

### Data Flow (After Fix)

```
DCU handshakes → SERVER parses gateway_identifier from protocol header
                   └─► addOrUpdateHandShake() now persists it   ✅ Fix 1
                         └─► MongoDB: handshake_info.gateway_identifier = 2043

User clicks ON/OFF → CCMS_UI reads HandShake from MongoDB
                       └─► getGateway_identifier() returns 2043     ✅ Fix 2
                             └─► URL: dcu_identifier=2043
                                   └─► SERVER builds CI=6 packet with gateway ID = 000007FB
                                         └─► DCU accepts and executes command
```

## Files Changed

| File | Change |
|---|---|
| `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/repos/DeviceRequestDataRepository.java` | Uncommented `update.set("gateway_identifier", ...)` at line 54 |
| `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vetsoft/ccms/controller/DeviceConfigurationController.java` | `turnOnLights()` and `turnOffLights()` now fetch `gateway_identifier` from DB instead of using UI-provided `device_identifier` |
| `CCMS_UI/STARTUP/ccms_ui/src/test/java/com/vetsoft/ccms/controller/DeviceConfigurationControllerTest.java` | Updated existing tests with proper `getHandShakeByID` mocks, added 2 new tests for handshake-not-found paths |

## Containers to Rebuild

| Container | Reason |
|---|---|
| `cspl-server` | Fix 1 — uncommented `gateway_identifier` persistence |
| `cspl-ccms-ui` | Fix 2 — DB lookup of `gateway_identifier` |

## Deploy Steps

```bash
# 1. Rebuild both containers
docker compose build server ccms_ui

# 2. Restart (forces DCUs to reconnect and re-handshake)
docker compose up -d server ccms_ui

# 3. For existing DCUs that already have gateway_identifier = 0 in MongoDB:
#    Restarting the server drops TCP connections, DCUs reconnect and
#    send handshake → gateway_identifier gets persisted.
docker compose restart server
```

## Tests Added/Updated

| Test | What It Verifies |
|---|---|
| `testTurnOnLights_ValidParams_ReturnsStatus200` | Mocked HandShake with gateway_identifier=2043 → URL uses correct ID |
| `testTurnOnLights_HandShakeNotFound_ReturnsStatus200` | Null HandShake → graceful 200 error handling |
| `testTurnOffLights_ValidParams_ReturnsStatus200` | Mocked HandShake with gateway_identifier=2043 → URL uses correct ID |
| `testTurnOffLights_HandShakeNotFound_ReturnsStatus200` | Null HandShake → graceful 200 error handling |

## Verification

- CCMS_UI unit tests pass (13/13): `mvn test -Dtest=DeviceConfigurationControllerTest`
- CCMS_UI build succeeds: `mvn clean package -DskipTests`
- After deploy + DCU re-handshake: `tcpdump -nn -i any 'tcp port 9100'` shows CI=6 packet with correct gateway ID
- Manual: Click light ON/OFF on Monitor & Control page → DCU light toggles
