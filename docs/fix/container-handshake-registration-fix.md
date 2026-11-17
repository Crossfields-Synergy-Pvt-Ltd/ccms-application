# Container Handshake Registration Fix

## Scope

This fix hardens handshake processing without changing the DCU wire protocol, packet layout, 16-byte gateway serial field, response format, ports, Docker service names, MongoDB collection, or deployment topology.

## Changes implemented

- Packet CRC and structural validation now fail closed instead of accepting malformed input after an exception.
- Handshake parsing returns no partially parsed object when field parsing fails.
- Gateway serial validation still requires exactly 16 characters, while rejecting null, blank, control-character, and padded values.
- Database failures log the full exception and gateway context; they do not silently disappear.
- Channel registration rejects null/blank identities.
- Channel replacement is synchronized and returns only the previous channel for the same gateway.
- Disconnect cleanup removes entries by channel identity rather than using a channel context as a serial-number map key.
- Old channels are closed once; Netty lifecycle callbacks are no longer invoked manually.
- Log4j2 file logging is attached to the root logger and uses `SERVER_LOG_DIR`.
- The missing `SpringNettyLog4j.properties` reference was replaced with the deployed `log4j2.properties` file.
- SLF4J is routed through the Log4j2 binding instead of the Log4j 1.2 binding.

## Tests added or updated

- Fail-closed CRC and malformed-packet tests.
- Gateway serial validation tests.
- Channel registration/removal tests.
- Reconnect tests proving an old channel cannot remove a newer channel.

## Verification

Run from the repository root:

```bash
cd SERVER/ccms
mvn test
```

The UI test suite is unchanged, but should also be run when validating the complete workspace:

```bash
cd CCMS_UI/STARTUP/ccms_ui
mvn test
```

Runtime verification should additionally send the existing handshake fixture and confirm that the response bytes remain unchanged, one `handshake_info` document is upserted, and only one active channel exists after reconnect.
