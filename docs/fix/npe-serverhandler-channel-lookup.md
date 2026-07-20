# NPE Fix — Null Channel Lookup in ServerHandler

## Summary

All five `send*` methods in `ServerHandler.java` accessed `MainBootApp.channels_list.get(key).ctx` in a single expression. When the key was not present in the map, `.get()` returned `null`, and accessing `.ctx` on `null` threw a `NullPointerException` **before** the `if (tmp != null)` null check was reached. This made the `else` branch (which logs `"DEVICE IS OFFLINE"`) permanently unreachable.

| Method | Line | Used By |
|---|---|---|
| `sendMessageToClient` | 292 | Commented-out legacy code |
| `sendNodeConfigurationChangeRequestToClient` | 395 | `UIConfController.syncNodeConfFromUI` |
| `sendDcuConfSyncMesageToClient` | 423 | `UIConfController.syncSysConf` |
| **`sendSchedulerConfigurationChangeRequestToClient`** | **447** | **`UIConfController.syncSchedulerConfFromUI`** |
| `sendManuvalOnOffRequest` | 474 | `UIConfController.manuvalOn` / `.manuvalOff` |

## Root Cause

```java
// Before (NPE on line 447 if get() returns null):
ChannelHandlerContext tmp = MainBootApp.channels_list.get(dcu_id).ctx;
if (tmp != null) {           // ← NEVER reached when get() returns null
    // send packet
} else {
    // log "DEVICE IS OFFLINE"  ← DEAD CODE
}
```

## Fix

```java
// After (null-safe):
ChannelDetails channelDetails = MainBootApp.channels_list.get(dcu_id);
if (channelDetails != null) {
    ChannelHandlerContext tmp = channelDetails.ctx;
    // send packet
} else {
    LOG.error("DEVICE IS OFFLINE UNABLE TO SYNC : " + dcu_id);
}
```

## Impact

- **Config sync (scheduler/node/system) now properly logs `"DEVICE IS OFFLINE"`** instead of silently catching an NPE.
- **Manual ON/OFF commands** also report the correct offline status.
- **Retry-on-reconnect** (data saved to MongoDB with status `"PENDDING"` before the channel lookup) was already working and is unaffected.
- `UIConfController` still returns `"OK"` to the UI even on failure (by design — the server saves pending data for retry).

## Files Changed

| File | Change |
|---|---|
| `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/server/ServerHandler.java` | Separated `channels_list.get()` from `.ctx` in all 5 methods |
