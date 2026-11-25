# Handshake Response Gateway Identifier Fix

## Summary

DCU `1903SO1P1C013917` was repeatedly reconnecting and could not be controlled.
The handshake record was written to MongoDB, but the live Netty channel was not
registered because handshake response generation attempted to parse the
alphanumeric UI `serial_number` as an integer.

## Root cause

`HandShakeParser.getHandshakeResponseData()` used:

```java
Integer.parseInt(hs_req.getSerial_number())
```

`serial_number` is a UI-owned field and can contain values such as
`1903SO1P1C013917`. This caused `NumberFormatException` before
`ServerHandler` could call `MainBootApp.trackChannels()`.

The protocol gateway identifier is already parsed from the handshake header and
stored in `gateway_identifier`. That is the field required in the handshake
response.

## What changed

Handshake response generation now uses:

```java
Integer.toHexString(hs_req.getGateway_identifier())
```

The response continues to use the same 8-character, zero-padded protocol field.
No ports, routes, database collections, or DCU serial values were changed.

## Effect

- Alphanumeric DCU serials no longer cause handshake response generation to fail.
- The handshake can complete and the live channel can be registered.
- Existing DCUs continue to receive their own parsed gateway identifier.
- Manual ON/OFF control can find the registered channel after reconnect.

## Tests

Added a regression test that uses gateway identifier `2043` with the affected
alphanumeric serial format and verifies the response contains `000007fb`.

## Verification

Run from `SERVER/ccms`:

```bash
mvn -Dtest=HandShakeParserTest test
mvn test
```

After deployment, verify SERVER logs no longer contain:

```text
For input string: "1903SO1P1C013917"
```

The affected DCU should remain connected after handshake and control should log
an online channel for its serial.
