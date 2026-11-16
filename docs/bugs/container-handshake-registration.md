# Container Handshake Registration and Logging Bug

## Summary

Device handshakes reach the Netty server, but a successful network handshake does not reliably result in a registered device or a useful diagnostic log. The Docker service names and MongoDB database/collection names are aligned; the primary defects are in handshake validation, MongoDB error handling, channel tracking, and logging configuration.

## Expected flow

1. A DCU connects to the server on TCP port `9100`.
2. `ServerHandler` validates the packet and identifies a handshake command.
3. `HandShakeParser` parses the gateway serial number and handshake fields.
4. `DeviceRequestDataRepository.addOrUpdateHandShake()` upserts the record into MongoDB database `ccms`, collection `handshake_info`.
5. The channel is registered in `MainBootApp.channels_list` using the gateway serial number.
6. The UI reads the same `handshake_info` collection.

## Findings

### 1. Handshake persistence is gated by an exact serial length

The MongoDB write only runs when the parsed gateway serial number has exactly 16 characters:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/parser/hs/HandShakeParser.java:129-136`

If parsing produces padding, null characters, malformed data, or another valid protocol length, the server can send a response without registering the handshake.

### 2. MongoDB persistence errors are hidden

The upsert is performed by:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/repos/DeviceRequestDataRepository.java:44-87`

The caller catches failures with only `e.getMessage()`:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/parser/hs/HandShakeParser.java:123-155`

The exception stack trace is not passed to the logger. A connection refusal, timeout, serialization error, or failed upsert can therefore look like a missing handshake rather than an explicit database failure.

### 3. Channel removal uses the wrong map key

`MainBootApp.channels_list` is declared as a map keyed by gateway serial number:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/MainBootApp.java:27`

The disconnect handlers remove entries using `ChannelHandlerContext` instead of the gateway serial number:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/server/ServerHandler.java:319-321`
- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/server/ServerHandler.java:335-351`

Those removals do not match the map key and leave stale channel records behind.

### 4. Reconnect handling can close the wrong channel or classify a valid connection as stale

When a gateway serial already exists in the registry, `trackChannels()` handles same-IP and different-IP connections separately:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/MainBootApp.java:51-106`

The different-IP path stores the previous channel in `hafe_opened_channels_list` and aggressively closes it using multiple close, deregister, disconnect, flush, and pipeline operations. Because stale entries are not removed correctly, subsequent reconnects can repeatedly enter this path.

### 5. The persistence and channel-registration operations are coupled in the handshake read path

`ServerHandler` parses and persists the handshake before it tracks the channel:

- `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/server/ServerHandler.java:87-120`

If MongoDB is unavailable or the persistence code throws, the handshake processing path can fail before the connection is registered in memory.

### 6. File logging configuration does not consistently match the runtime logging stack

The server Docker image copies `log4j2.properties` into the runtime configuration directory:

- `SERVER/ccms/Dockerfile:12-16`

The Maven configuration also includes the SLF4J Log4j 1.2 binding:

- `SERVER/ccms/pom.xml:89-103`

Additionally, the Log4j2 file appender is declared but the root logger is configured with the console appender only:

- `SERVER/ccms/conf/log4j2.properties:10-24`

This means handshake and MongoDB diagnostics may be visible only through container stdout and may not be written to `SERVER_LOG_DIR` as deployment documentation suggests.

## Configuration consistency check

The server and UI are configured to use the same MongoDB service, database, and handshake collection:

- Docker Compose defaults MongoDB to service hostname `mongodb` and database `ccms`: `docker-compose.yml:88-92`, `docker-compose.yml:116-121`
- The server model uses collection `handshake_info`: `SERVER/ccms/src/main/java/com/vetsoft/ccms/netty/pojo/HandShake.java:9`
- The UI also reads `HandShake` documents from MongoDB: `CCMS_UI/STARTUP/ccms_ui/src/main/java/com/vnetsoft/ccms/dao/DCUDaoImpl.java:72-75`

Therefore, the evidence does not indicate a server/UI collection mismatch.

## Secondary deployment issue

The main Compose file publishes MongoDB and MySQL ports to the host:

- `docker-compose.yml:27-28` publishes `27017`
- `docker-compose.yml:41-42` publishes `3306`

This conflicts with the deployment requirement that these databases remain internal. It is a security and isolation issue, but it is not the most likely cause of missing handshake records.

## Recommended corrective actions

1. Track channels using a stable gateway identifier and remove entries by that identifier, not by `ChannelHandlerContext`.
2. Make channel replacement atomic and close only the previous channel associated with the same gateway.
3. Replace the exact 16-character persistence gate with protocol-aware validation and explicit rejection logging for invalid serials.
4. Log MongoDB failures with the exception object/stack trace and include the gateway serial number.
5. Separate handshake persistence from channel registration, or define explicit behavior when MongoDB is unavailable.
6. Standardize on one logging implementation and attach the file appender to the root logger if persistent file logs are required.
7. Verify the deployed containers with MongoDB and MySQL port publishing disabled.

## Verification performed

- SERVER Maven build completed successfully with `mvn test -DskipTests`.
- UI Maven build completed successfully with `mvn test -DskipTests`.
- Static inspection confirmed the handshake parser, MongoDB upsert path, channel registry, Docker configuration, and logging configuration described above.
- Docker runtime checks were not possible in the inspection environment because the Docker CLI was unavailable in WSL.
