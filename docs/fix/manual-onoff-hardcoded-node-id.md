# Manual ON/OFF — Hardcoded Node ID in CI=6 Packet Fix

## Summary

The CI=6 Node Operation Command packet had the node ID hardcoded to `00000001`. If the DCU's light was on a different node (e.g., node 5), the DCU silently ignored the command because no node with ID 1 existed.

## Root Cause

`ManuvalCommands.java` built the CI=6 packet payload with a hardcoded `00000001` as the node ID (the target of the ON/OFF operation):

```java
.append("00000001") // node id — hardcoded
```

The protocol spec says node IDs range from `0x00000001` to `0xFFFFFFFF`, and the DCU assigns a unique node ID to each light/meter/sensor. If the hardcoded ID didn't match the actual light's node, the DCU would receive the command, see no matching node, and silently drop it.

## Data Flow (Before Fix)

```
ManuvalCommands.turnOnLight(serial, gatewayId)
  │
  └─► builds CI=6 packet:
        op_type=01, op_value=0001, node_count=01, node_id=00000001
                                                     ▲ HARDCODED
  │
  └─► sent via Netty to DCU
  │
  └─► DCU: "I have no node 1" → silently ignored
```

## What Changed

### 1. `ManuvalCommands.java` (SERVER)
- Added 3-arg overloads `turnOnLight(serial, id, nodeId)` and `turnOffLight(serial, id, nodeId)`
- The 2-arg versions now delegate to 3-arg with `nodeId=1` (backward compatible)
- Node ID is formatted as 8-char hex via `StringUtils.leftPad`

**Before:**
```java
.append("00000001") // node id
```

**After:**
```java
.append(StringUtils.leftPad("" + Integer.toHexString(node_id), 8, "0")) // node id
```

### 2. `UIConfController.java` (SERVER)
- `/manuval_on` and `/manuval_off` now accept `@RequestParam("node_id") int node_id` (default `1`)
- Passed through to `ManuvalCommands.turnOnLight/turnOffLight`

### 3. `DeviceRequestDataRepository.java` (SERVER)
- When light events (303=ON, 304=OFF, 305=DIM) are processed, the `node_identifier` from the event is now persisted to the HandShake document as `light_node_id`
- This means once a manual toggle occurs at the DCU, the correct node ID is stored for future commands

### 4. `HandShake.java` (SERVER + CCMS_UI)
- Added `private int light_node_id = 1;` field with getter/setter

### 5. `DeviceConfigurationController.java` (CCMS_UI)
- `turnOnLights()` and `turnOffLights()` now read `hand_shake.getLight_node_id()` and pass it as `&node_id=` to the SERVER URL

## Auto-Population of `light_node_id`

When someone manually toggles the light at the DCU (physical switch), the DCU sends a CI=5 event upload. The SERVER processes this event and:

1. Extracts the `node_identifier` from the event data
2. Generates an event with ID 303 (ON) or 304 (OFF)
3. Calls `updateDeviceEventsStatusOnHandshakeCollection()`
4. **Now also persists** `update.set("light_node_id", user.getNode_identifier())` to the HandShake document

Subsequent CI=6 commands from the UI will use this stored node ID.

## Files Changed

| File | Change |
|---|---|
| `SERVER/.../controller/utils/ManuvalCommands.java` | Added 3-arg overloads `turnOnLight`/`turnOffLight` with `node_id` parameter |
| `SERVER/.../controller/UIConfController.java` | `manuvalOn`/`manuvalOff` accept `node_id` request param |
| `SERVER/.../netty/repos/DeviceRequestDataRepository.java` | Persist `light_node_id` from light events (303/304/305) |
| `SERVER/.../netty/pojo/HandShake.java` | Added `light_node_id` field |
| `CCMS_UI/.../controller/DeviceConfigurationController.java` | `turnOnLights`/`turnOffLights` read `light_node_id` from HandShake |
| `CCMS_UI/.../pojo/HandShake.java` | Added `light_node_id` field |

## Tests Added/Updated

| Test | File | What It Verifies |
|---|---|---|
| `testTurnOnLight_WithCustomNodeId` | `ManuvalCommandsTest.java` | Custom node ID 5 appears in packet as `00000005` |
| `testTurnOffLight_WithCustomNodeId` | `ManuvalCommandsTest.java` | Custom node ID 3 appears in packet as `00000003` |
| `testTurnOnLight_CustomNodeIdProducedDifferentPacket` | `ManuvalCommandsTest.java` | Packets with node ID 1 vs 2 differ |
| `testManuvalOn_WithCustomNodeId` | `UIConfControllerTest.java` | `manuvalOn` accepts and forwards node_id |
| `testManuvalOff_WithCustomNodeId` | `UIConfControllerTest.java` | `manuvalOff` accepts and forwards node_id |
| `testTurnOnLights/OffLights_ValidParams` | `DeviceConfigurationControllerTest.java` | Mock HandShake includes `light_node_id` |

## Verification

- SERVER unit tests pass: **81 tests, 0 failures**
- CCMS_UI unit tests pass: **76 tests, 0 failures**
- Build succeeds: `mvn clean package -DskipTests`
- Container rebuild: `docker compose build ccms_ui server && docker compose up -d`
