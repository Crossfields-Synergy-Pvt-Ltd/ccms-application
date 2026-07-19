# Manual ON/OFF — Wrong Gateway ID in CI=6 Packet Fix

## Summary

The light ON/OFF button on the Monitor & Control page sent the Netty CI=6 command packet with `00000000` as the gateway ID instead of the DCU's actual gateway identifier (e.g., `2043`). The DCU received the command but ignored it because the gateway ID in the packet header didn't match its own ID.

## Root Cause

The UI sent `dcu_details.serial_number` as `device_identifier`, but this field is **never populated from the DCU protocol** — it always defaults to `"00000000"` in the `HandShake` POJO and is never persisted to MongoDB. The `ManuvalCommands.java` builder used this value verbatim as the gateway ID in the CI=6 packet header.

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

### File: `DeviceConfigurationController.java`

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

## Files Changed

| File | Change |
|---|---|
| `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/controller/DeviceConfigurationController.java` | `turnOnLights()` and `turnOffLights()` now fetch `gateway_identifier` from DB instead of using UI-provided `device_identifier` |
| `CCMS_UI/STARTUP/ccms_ui/src/test/java/com/vnetsoft/ccms/controller/DeviceConfigurationControllerTest.java` | Updated existing tests with proper `getHandShakeByID` mocks, added 2 new tests for handshake-not-found paths |

## Tests Added/Updated

| Test | What It Verifies |
|---|---|
| `testTurnOnLights_ValidParams_ReturnsStatus200` | Mocked HandShake with gateway_identifier=2043 → URL uses correct ID |
| `testTurnOnLights_HandShakeNotFound_ReturnsStatus200` | Null HandShake → graceful 200 error handling |
| `testTurnOffLights_ValidParams_ReturnsStatus200` | Mocked HandShake with gateway_identifier=2043 → URL uses correct ID |
| `testTurnOffLights_HandShakeNotFound_ReturnsStatus200` | Null HandShake → graceful 200 error handling |

## Verification

- Unit tests pass (13/13): `mvn test -Dtest=DeviceConfigurationControllerTest`
- Build succeeds: `mvn clean package -DskipTests`
- Container rebuild: `docker compose build ccms_ui && docker compose up -d ccms_ui`
- Manual: Click light ON/OFF on Monitor & Control page → DCU receives CI=6 packet with correct gateway ID → light toggles
