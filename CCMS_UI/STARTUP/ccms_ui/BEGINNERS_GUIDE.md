# CCMS_UI App — Beginner's Guide

## What Is This?

CCMS_UI is a **Centralized Control & Monitoring System** web application for managing smart streetlight networks. It provides a dashboard to monitor, configure, and control DCU (Data Concentrator Unit) devices and their connected streetlights.

## Quick Facts

| Item | Value |
|---|---|
| Backend | Java 7, Spring MVC 4.2.8, Hibernate 4.3.5 |
| Frontend | AngularJS 1.4.8 (SPA) |
| Build | Maven (WAR) |
| Container | Apache Tomcat 7+ |
| Databases | MySQL 5.x, MongoDB, Redis |
| Port | 8080 (Tomcat) |
| Context Path | `/CCMS` |

## Architecture

```
┌────────────────────────────────────────────────────────┐
│                   CCMS_UI (Tomcat)                      │
│                                                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │           AngularJS 1.x SPA (Frontend)            │   │
│  │  UI Router: login, dashboard, dcu, node, meter,  │   │
│  │  event, map, schedules, history, filter, etc.     │   │
│  └──────────────────────┬───────────────────────────┘   │
│                          │ HTTP REST calls               │
│  ┌──────────────────────▼───────────────────────────┐   │
│  │        Spring MVC Controllers (Backend)           │   │
│  │  DCU, Node, User, Event, Meter, Station, etc.     │   │
│  └──────┬──────────────┬──────────────┬─────────────┘   │
│         │              │              │                  │
│    ┌────▼────┐   ┌────▼────┐   ┌────▼────┐              │
│    │  MySQL  │   │ MongoDB │   │  Redis  │              │
│    │employee │   │  ccms   │   │ (cache) │              │
│    │  _db    │   │         │   │         │              │
│    └─────────┘   └─────────┘   └─────────┘              │
└────────────────────────────────────────────────────────┘
                          │
                          │ REST :8102
                    ┌─────▼──────┐
                    │   SERVER    │
                    │ (IoT gw)   │
                    └────────────┘
```

The app is a **monolithic Java WAR** that serves both the backend API and the AngularJS frontend from the same Tomcat instance. It talks to:
- **MySQL** (`employee_db`) — relational data (users, DCUs, events, schedules)
- **MongoDB** (`ccms`) — device telemetry and configuration data
- **SERVER** app — to push configurations and commands to physical devices

## Prerequisites

- **Java 7** (JDK) — for local development
- **Maven 3** — for building
- **Tomcat 7+** — for deployment
- **MySQL 5.7+** — database `employee_db`
- **MongoDB 3.4+** — database `ccms`
- **Docker & Docker Compose** — for containerized setup

## How to Run

### Option 1: Docker (Recommended)

```bash
# From the project root (/home/amogh/projects/cspl)
docker compose up ccms_ui
```

This starts CCMS_UI along with MySQL and MongoDB. Add `-d` to run in background.

To view logs:
```bash
docker compose logs -f ccms_ui
```

Once running, open: **http://localhost:8080/CCMS/**

### Option 2: Local (for development)

```bash
# 1. Start MySQL and create the database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS employee_db;"

# 2. Make sure MongoDB is running on localhost:27017

# 3. Build the WAR
cd CCMS_UI/STARTUP/ccms_ui
mvn clean package

# 4. Deploy to Tomcat
#    Copy target/CCMS.war to Tomcat's webapps/ directory
cp target/CCMS.war /path/to/tomcat/webapps/

# 5. Start Tomcat
/path/to/tomcat/bin/startup.sh

# Access at: http://localhost:8080/CCMS/
```

## Configuration

### Spring Config (`spring-config.xml`)

| Setting | Value | Purpose |
|---|---|---|
| MySQL URL | `jdbc:mysql://localhost:3306/employee_db` | Relational DB |
| MySQL User | root | DB credentials |
| MySQL Pass | root | DB credentials |
| MongoDB Host | 127.0.0.1:27017 | Document DB |
| MongoDB DB | ccms | Document database |

> **In Docker**, all `localhost` references are replaced with Docker service names (`mongodb`, `mysql`). The Docker build uses `spring-config-docker.xml`.

### Logging (`log4j.properties`)

- Log level: `DEBUG`
- Output file: `E:/CCMS_DATA/wms.log` (on Windows)
- Console output enabled

## UI Navigation

The app opens to the **Map View** (default route). After logging in, you reach the dashboard with left navigation:

| Section | Purpose |
|---|---|
| **Dashboard** | Overview maps and charts of the streetlight network |
| **DCU** | Add/list/modify Data Concentrator Units (gateways) |
| **Node** | Add/list/modify individual streetlight nodes |
| **Monitor & Control** | Real-time device monitoring and manual override commands |
| **Meter** | View and manage meter data |
| **Events** | View device events and alerts |
| **Schedules** | Configure auto on/off schedules |
| **History** | Event and data history |
| **Filter** | Data filtering rules |
| **Switch Point** | Switch point configuration |
| **Operational Hours** | Operational hour tracking |
| **User** | User administration |
| **Default Config** | Default device configuration templates |

### Key Pages

- **Login** → `http://localhost:8080/CCMS/#/login`
- **Map View** → `http://localhost:8080/CCMS/#/map` (default landing page)
- **Dashboard** → `http://localhost:8080/CCMS/#/dashboard/home`
- **DCU List** → `http://localhost:8080/CCMS/#/dashboard/dcu`

## Ports Summary

| Port | Service | Purpose |
|---|---|---|
| 8080 | Tomcat HTTP | CCMS_UI web app (context: /CCMS) |
| 3306 | MySQL | Relational database |
| 27017 | MongoDB | Document database |

## How to Verify It's Running

```bash
# Check if the login page loads
curl -s http://localhost:8080/CCMS/ | head -5

# Check Docker logs
docker compose logs ccms_ui

# Check Tomcat manager (if enabled)
curl http://localhost:8080/manager/text/list
```

Open **http://localhost:8080/CCMS/** in a browser. You should see the login page.

## Common Issues

| Problem | Likely Cause | Fix |
|---|---|---|
| 404 on `/CCMS/` | App not deployed / context path mismatch | Confirm `CCMS.war` is in Tomcat's `webapps/` |
| "Cannot connect to MySQL" | MySQL not running or wrong credentials | `docker compose up mysql` |
| "MongoDB connection refused" | MongoDB not running | `docker compose up mongodb` |
| White page / JS not loading | Browser caching | Clear cache or open in incognito |
| Login not working | MySQL `employee_db.users` table empty | Seed the users table (check UserDao for schema) |
| UI routes not working | AngularJS hashbang routing | Use `/#/` in URL, e.g. `/#/dashboard/home` |

## Testing

CCMS_UI has two test suites: **Java backend tests** and **AngularJS frontend tests**.

### Backend Tests (Java)

| Item | Value |
|---|---|
| Framework | JUnit 4 + Mockito 1.10.19 + Spring Test 4.2.8 + json-path 2.2.0 |
| Test location | `src/test/java/com/vnetsoft/ccms/controller/` |
| Run command | `mvn test` |

**75/75 tests pass** across 6 controller test files:

| Test File | What It Tests |
|---|---|
| `AbstractControllerTest.java` | Base test class with Spring/Mockito setup |
| `DCUControllerTest.java` | DCU CRUD endpoints |
| `DeviceConfigurationControllerTest.java` | Light ON/OFF, configuration push to SERVER |
| `EventControllerTest.java` | Event query and reporting |
| `UserControllerTest.java` | User authentication, login, profile |
| `NodeControllerTest.java` | Node CRUD endpoints |

### Frontend Tests (AngularJS)

| Item | Value |
|---|---|
| Framework | Karma + Jasmine 2.x |
| Test locations | `src/test/javascript/controllers/`, `src/test/javascript/factories/` |
| Run command | `npm test` |
| Browser | ChromeHeadless (via puppeteer) |

**66/66 tests pass** across 10 test files (6 controller tests, 4 factory tests).

### How to Run

```bash
# Run only Java backend tests
cd CCMS_UI/STARTUP/ccms_ui
mvn test

# Run only AngularJS frontend tests
npm test

# Run everything (Java + AngularJS)
mvn test && npm test

# Or run all project test suites (SERVER + CCMS_UI) from the project root
cd ../../../
bash scripts/run-all-tests.sh
```

### Prerequisites

- **Java tests:** Maven 3, JDK 7+ (tests target Java 1.8)
- **AngularJS tests:** Node.js + npm. Install dependencies first:

```bash
cd CCMS_UI/STARTUP/ccms_ui
npm install
```

`CHROME_BIN` is automatically set to puppeteer's bundled Chromium via the `scripts/get-chrome-path.js` helper — no native Chrome install or env var required.

### Notes

- All Java controller tests use **pure Mockito mocks** — no embedded MongoDB or MySQL required
- AngularJS tests use **stub files** for Bower dependencies (`inform`, `ui.select`, `angucomplete`, `highcharts-ng`, Google Maps, etc.) located in `src/test/javascript/mocks/`
- Hard refresh (`Ctrl+Shift+R`) required after each deployment rebuild to clear cached HTML templates
- Total: **141 tests pass** across the two CCMS_UI test suites

## Development Tips

- **Rebuild and restart**: `docker compose build ccms_ui && docker compose up -d ccms_ui`
- **View real-time logs**: `docker compose logs -f ccms_ui`
- **Access MySQL**: `docker compose exec mysql mysql -u root -p employee_db`
- **Access MongoDB**: `docker compose exec mongodb mongo ccms`
- **Quick WAR rebuild locally**: `mvn clean package -DskipTests` then copy `target/CCMS.war`
- **Toggle debug logs**: Edit `src/main/resources/log4j.properties`
