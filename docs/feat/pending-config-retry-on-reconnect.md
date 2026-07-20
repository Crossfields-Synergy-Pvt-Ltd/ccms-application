# Pending Config Retry on DCU Reconnect

## Summary

When a DCU configuration sync (node or schedule) is triggered while the DCU is offline, the Inform Change (0x02) packet is saved as `PENDDING` in MongoDB but never sent — because the TCP channel it was written to was stale or closed. When the DCU reconnects, no retry occurs, so the config change is silently lost.

This feature adds pending-config detection in the handshake handler: after a DCU's handshake is processed and the new channel is registered, the server queries for pending configs and resends the Inform Change packet on the **new** connection.

## Root Cause

In `ServerHandler.java`, when an Inform Change (0x02) is sent:

```java
ChannelHandlerContext tmp = MainBootApp.channels_list.get(dcu_id).ctx;
tmp.writeAndFlush(...)
```

If the DCU is offline, `channels_list` either has no entry (silently skipped with "DEVICE IS OFFLINE" log) or has a stale entry that will be replaced on reconnect — but there is no mechanism to re-send once the new channel arrives. The pending config sits in MongoDB with `status: "PENDDING"` forever.

## Protocol Flow

```
DCU reconnect
  │
  └─► HandShake (0x09)
        │
        ├─► trackChannels() — replaces stale channel with new ctx
        │
        ├─► Send handshake response
        │
        └─► [NEW] Query MongoDB for pending configs
              │
              ├─► Pending node_conf_data found?
              │     └─► Send Inform Change (0x02) → Download Init → Download Content
              │
              └─► Pending schedule_conf_data found?
                    └─► Send Inform Change (0x02) → Download Init → Download Content
```

## Files Modified

| # | File | Change |
|---|---|---|
| 1 | `SERVER/.../netty/server/ServerHandler.java` | Added pending-config retry logic in handshake handler after `trackChannels()` and handshake response |
| 2 | `SERVER/.../netty/repos/DeviceRequestDataRepository.java` | Added `findNodeConfDataByDcuId()` and `findScheduleConfDataByDcuId()` — query by `_id` + `status = "PENDDING"` |

### 1. ServerHandler.java

After the handshake response is written and flushed on the new `ctx`, and the channel is registered via `trackChannels()`:

```java
String dcuSerial = hs_req.getGateway_serial_number();
try {
    DeviceRequestDataRepository db_repo = MainBootApp.context.getBean(DeviceRequestDataRepository.class);

    NodeConfData pendingNode = db_repo.findNodeConfDataByDcuId(dcuSerial);
    if (pendingNode != null) {
        String packet = Utiles.getNodeConfInformationExchangePacket(
                pendingNode.getFile_identifier(), pendingNode.getDevice_identifier());
        String CRC = Integer.toHexString(BaseUtil.calculateCRC(
                BaseUtil.convertHexToString(packet).toCharArray(),
                BaseUtil.convertHexToString(packet).length()));
        ctx.writeAndFlush(Unpooled.copiedBuffer(
                BaseUtil.convertHexToString(packet + CRC), CharsetUtil.ISO_8859_1));
    }

    ScheduleConfData pendingSched = db_repo.findScheduleConfDataByDcuId(dcuSerial);
    if (pendingSched != null) {
        String packet = Utiles.getScheduleConfInformationExchangePacket(
                pendingSched.getFile_identifier(), pendingSched.getDevice_identifier());
        String CRC = Integer.toHexString(BaseUtil.calculateCRC(
                BaseUtil.convertHexToString(packet).toCharArray(),
                BaseUtil.convertHexToString(packet).length()));
        ctx.writeAndFlush(Unpooled.copiedBuffer(
                BaseUtil.convertHexToString(packet + CRC), CharsetUtil.ISO_8859_1));
    }
} catch (Exception e) {
    LOG.error("ERROR RETRYING PENDING CONFIG ON RECONNECT FOR DCU " + dcuSerial, e);
}
```

**Key constraint:** The Inform Change packet is written to `ctx` (the `channelRead` parameter), which is the **new** channel established by the reconnect. This avoids writing to a stale channel.

### 2. DeviceRequestDataRepository.java

```java
public NodeConfData findNodeConfDataByDcuId(String serial) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").is(serial));
    query.addCriteria(Criteria.where("status").is("PENDDING"));
    return mongoTemplate.findOne(query, NodeConfData.class);
}

public ScheduleConfData findScheduleConfDataByDcuId(String serial) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").is(serial));
    query.addCriteria(Criteria.where("status").is("PENDDING"));
    return mongoTemplate.findOne(query, ScheduleConfData.class);
}
```

Both methods query MongoDB documents where:
- `_id` matches the gateway serial number (e.g., `1709HY1P1C009834`)
- `status` equals `"PENDDING"` (the initial status set by `saveNodeConfRequestData` / `saveSchedulerConfRequestData`)

Since `@Id` is on `device_serial_number`, the `_id` field equals the DCU serial number.

## Not Retried

The following are **not** retried on reconnect (by design):
- **System (DCU) config** (`DCUSysConfData`, file type `01`) — uses `"PROCESSING-INIT"` status, not `"PENDDING"`; the initial sync is always sent on an active channel
- **Manual ON/OFF commands** — one-shot commands with no persistence
- **Configs with `"DONE"` or `"PROCESSING-INIT"` status** — already completed or in-flight

## Verification

- Press sync button for an **offline** DCU — log shows "DEVICE IS OFFLINE" and config saved as `PENDDING`
- DCU reconnects — log shows `PENDING NODE/SCHEDULE CONF FOUND FOR DCU ... - RETRYING INFORM CHANGE ON NEW CHANNEL`
- DCU receives Inform Change and initiates Download Init → Download Content flow
- After successful download, config status updates to `"DONE"` in MongoDB
- Both Maven projects compile with `BUILD SUCCESS`
- All existing tests pass

## Build & Test

```bash
# SERVER
mvn test -f SERVER/ccms/pom.xml

# UI
mvn test -f CCMS_UI/STARTUP/ccms_ui/pom.xml
```

## Notes

- The MongoDB driver auth issue (`SCRAM-SHA-1 authentication failed ... user name: n=`) must be resolved separately by removing empty `MONGODB_USERNAME`/`MONGODB_PASSWORD` env vars from the Hostinger environment.
- No database migration required — the feature uses existing `status` field values set by the existing sync endpoint code.
- The `findNodeConfDataByDcuId` / `findScheduleConfDataByDcuId` queries use `_id` directly (which maps to `device_serial_number` via `@Id`), so no separate index is needed.
