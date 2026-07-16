# CCMS ON/OFF — Handshake Timestamp Fix

## Problem

The **CCMS ON** and **CCMS OFF** cards on the dashboard and public monitor were incorrectly mapping to `light_status` (whether street lights are ON/OFF), instead of indicating whether the CCMS box itself is powered on and communicating.

- **CCMS ON** was counting DCUs where `light_status == 1` (Lights ON event 303)
- **CCMS OFF** was counting DCUs where `light_status != 1` (Lights OFF/DIM events 304/305)

This caused the cards to never change state when a DCU was handshaking but not sending light events.

## Fix

Changed the `ccms_on`/`ccms_off` calculation in `DashBoardDaoImpl.java` to use `hs_time_stamp` (last handshake timestamp) with a **15-minute** threshold:

| Card | Before | After |
|---|---|---|
| **CCMS ON** | `light_status == 1` | Last handshake < 15 min ago |
| **CCMS OFF** | `light_status != 1` | Last handshake >= 15 min ago or never |

The **LIGHTS ON/OFF** cards remain unchanged (still use `light_status`) — they correctly track individual light counts.

The **CCMS CONNECTED** card remains unchanged (still uses `hs_time_stamp` with 2-hour threshold) — it tracks network connectivity.

## Files Changed

| File | Change |
|---|---|
| `CCMS_UI/.../dao/DashBoardDaoImpl.java:92-107` | `ccms_on`/`ccms_off` now based on `hs_time_stamp` with 15-min threshold instead of `light_status` |
| `CCMS_UI/.../controller/MonitorControllerTest.java:55,66-67` | Added `ccms_on` and `ccms_off` assertions to test |

## Logic

```java
// In DashBoardDaoImpl.getDahsBoardCountstats()
for (HandShake tmp : list) {
    // LIGHTS ON/OFF — unchanged, tracks light count
    if (tmp.getLight_status() == 1) {
        light_on += tmp.getNo_of_lights();
    } else {
        light_off += tmp.getNo_of_lights();
    }

    // CCMS ON/OFF — fixed, tracks handshake recency
    try {
        long hs = Long.valueOf(tmp.getHs_time_stamp());
        if ((System.currentTimeMillis() - hs) > 900000) {  // 15 min
            ccms_off++;
        } else {
            ccms_on++;
        }
    } catch (Exception e) {
        ccms_off++;
    }
}
```

## Thresholds

| Card | Field | Threshold | Meaning |
|---|---|---|---|
| **CCMS CONNECTED** | `hs_time_stamp` | 2 hours (7200000ms) | Network connectivity |
| **CCMS ON** | `hs_time_stamp` | **15 minutes** (900000ms) | Box powered on |
| **CCMS OFF** | `hs_time_stamp` | 15 minutes | Box offline |
| **LIGHTS ON** | `light_status` | — | Lights lit |
| **LIGHTS OFF** | `light_status` | — | Lights off |

## Real-Time Calculation

Every page load/refresh calls `GET /dashboard/count`, which recalculates all counts in real-time from MongoDB. The 15-minute threshold is evaluated server-side at request time.

## Verification

All 75 UI tests pass:
```
Tests run: 75, Failures: 0, Errors: 0, Skipped: 0
```
