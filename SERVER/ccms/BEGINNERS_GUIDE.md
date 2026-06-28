# SERVER App — Beginner's Guide

## What Is This?

The SERVER app is a **Spring Boot IoT gateway server** for smart streetlight control. It connects to physical DCU (Data Concentrator Unit) devices over TCP, processes their telemetry data, and exposes a REST API for the CCMS_UI web dashboard.

## Quick Facts

| Item | Value |
|---|---|
| Language | Java 8 |
| Framework | Spring Boot 1.5.9 |
| Build | Maven (pom.xml) |
| Database | MongoDB |
| TCP Port | 9100 (device communication) |
| REST API Port | 8102 (UI integration) |
| Output | Executable JAR |

## Architecture

```
┌──────────────┐      TCP :9100      ┌──────────────────┐      REST :8102     ┌──────────┐
│  IoT Devices ├─────────────────────►│  SERVER (Netty)  │◄────────────────────┤ CCMS_UI  │
│  (DCUs)      │                     │  + Spring Boot   │                      │ (Web UI) │
└──────────────┘                     └────────┬─────────┘                      └──────────┘
                                              │
                                              │ MongoDB :27017
                                              ▼
                                      ┌──────────────────┐
                                      │     MongoDB      │
                                      │  (ccms database) │
                                      └──────────────────┘
```

The server has **two sides**:
1. **TCP Server** (port 9100) — accepts persistent connections from streetlight DCUs. Handles handshake, event reporting, meter data, ping/keepalive, and config downloads.
2. **REST API** (port 8102, context path `/user`) — provides endpoints for the UI to push configurations, send manual light ON/OFF commands, and manage half-opened connections.

## Prerequisites

- **Java 8** (JDK) — for local development
- **Maven 3** — for building
- **MongoDB 3.4+** — running on `localhost:27017`
- **Docker & Docker Compose** — for containerized setup

## How to Run

### Option 1: Docker (Recommended)

```bash
# From the project root (/home/amogh/projects/cspl)
docker compose up server
```

This starts the server along with its MongoDB dependency. Add `-d` to run in the background.

To see logs:
```bash
docker compose logs -f server
```

### Option 2: Local (for development)

```bash
# 1. Make sure MongoDB is running on localhost:27017
# 2. Build the JAR
cd SERVER/ccms
mvn clean package

# 3. Run the JAR
java -jar target/spring-boot-mongodb-0.0.1-SNAPSHOT.jar
```

Or with Maven directly:
```bash
mvn spring-boot:run
```

## Configuration

### Application Settings (`application.properties`)

| Property | Default | Description |
|---|---|---|
| `server.port` | 8102 | REST API port |
| `server.context-path` | /user | REST API base path |
| `spring.data.mongodb.host` | localhost | MongoDB host |
| `spring.data.mongodb.port` | 27017 | MongoDB port |
| `spring.data.mongodb.database` | ccms_test | MongoDB database name |
| `spring.application.name` | BootMongo | App name |

### Netty TCP Settings (`netty-server.properties`)

| Property | Default | Description |
|---|---|---|
| `tcp.port` | 9100 | TCP server port for device connections |
| `boss.thread.count` | 1000 | Netty boss thread count |
| `worker.thread.count` | 10000 | Netty worker thread count |
| `so.keepalive` | true | TCP keepalive |
| `so.backlog` | 55000 | TCP backlog size |

### External Config Path

The app loads a Spring XML context from:
```
/home/CCMS/roadmap/conf/applicationContext.xml
```

This XML configures the `MongoTemplate` and component scanning for repository classes. In the Docker image, this is provided automatically.

## REST API Endpoints

All endpoints are under `http://localhost:8102/user/push/`.

| Endpoint | Method | Description |
|---|---|---|
| `/push/sys_conf` | GET | Push system configuration to a DCU |
| `/push/sync_node_conf` | GET | Push node configuration to a DCU |
| `/push/sync_scheduler_conf` | GET | Push scheduler configuration to a DCU |
| `/push/manuval_on` | GET | Send manual light ON command |
| `/push/manuval_off` | GET | Send manual light OFF command |
| `/push/hafe_open_connections` | GET | Clean up half-opened device connections |
| `/push/device_req_counter` | GET | Get pending device request count |

## TCP Protocol (for device communication)

Devices connect to port **9100** using a persistent TCP connection with a custom binary protocol (hex-encoded via ISO-8859-1).

Key command IDs:
| ID | Direction | Purpose |
|---|---|---|
| 9 | Device → Server | Handshake request |
| 10 | Device → Server | Ping / keepalive |
| 5 | Device → Server | Status / event data |
| 3 | Server → Device | Download init request |
| 4 | Server → Device | Download content (config) |
| 6 | Server → Device | Light ON/OFF command |

## Ports Summary

| Port | Protocol | Purpose | Who Connects |
|---|---|---|---|
| 8102 | HTTP | REST API (context: /user) | CCMS_UI (web) |
| 9100 | TCP | Device communication (Netty) | IoT DCUs |

## How to Verify It's Running

```bash
# Check the REST API health
curl http://localhost:8102/user/push/hafe_open_connections

# Check Docker logs
docker compose logs server

# Check if TCP port is listening
netstat -an | grep 9100
```

## Common Issues

| Problem | Likely Cause | Fix |
|---|---|---|
| App starts but API returns errors | MongoDB not running | `docker compose up mongodb` |
| "Failed to connect to MongoDB" | Wrong host/port | Check `spring.data.mongodb.host` in config |
| Device connection refused | TCP port not exposed | Ensure port 9100 is published in docker-compose |
| JAR build fails "source option 5 is no longer supported" | Java version mismatch | Use `openjdk:8` or Maven compiler targeting 1.8 |

## Testing

### Test Framework

| Item | Value |
|---|---|
| Framework | JUnit 4 + Mockito 1.10.19 + Spring Test 4.2.8 |
| Test location | `src/test/java/` |
| Run command | `mvn test` |

### How to Run

```bash
cd SERVER/ccms
mvn test
```

To run a single test class:

```bash
mvn test -Dtest=ManuvalCommandsTest
```

To run all project test suites (SERVER + CCMS_UI) from the project root:

```bash
cd ../..
bash scripts/run-all-tests.sh
```

### Test Results

**76/76 tests pass** across 4 test files:

| Test File | What It Tests |
|---|---|
| `ManuvalCommandsTest.java` | Device command parsing and execution (light ON/OFF, dimming) |
| `BaseUtilTest.java` | Utility functions (`byteToHex`, `hexToByte`, etc.) |
| `UIConfControllerTest.java` | REST controller endpoints (`/push/sys_conf`, `/push/manuval_*`, etc.) |
| `UtilesTest.java` | Common utility functions |

### Notes

- Pure unit tests — no MongoDB required for the SERVER backend test suite
- Mock data is embedded directly in test classes
- All tests pass on JDK 8 (matching the production runtime)

## Development Tips

- **Rebuild and restart**: `docker compose build server && docker compose up -d server`
- **View real-time logs**: `docker compose logs -f server`
- **Access MongoDB**: `docker compose exec mongodb mongo ccms`
- **Local debug**: Run `mvn spring-boot:run` with your IDE's debugger attached on port 5005
- **Logs location**: Inside the container at `/home/CCMS/roadmap/logs/ccms.log`
