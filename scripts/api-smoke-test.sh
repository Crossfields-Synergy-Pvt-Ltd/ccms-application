#!/bin/bash
# =============================================================
# CSPL/CCMS API Smoke Test
# Run against a running Docker stack to verify all endpoints
# Usage: bash scripts/api-smoke-test.sh [base_url]
# Default base_url: http://localhost:8080/CCMS
#
# Loads .env from the project root for credentials. Override
# SMOKE_ADMIN_EMAIL / SMOKE_ADMIN_PASSWORD in the environment
# to test with a different account.
# =============================================================

# Load .env from project root if present
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
if [ -f "$PROJECT_ROOT/.env" ]; then
    set -a
    # shellcheck disable=SC1091
    . "$PROJECT_ROOT/.env"
    set +a
fi

: "${SMOKE_ADMIN_EMAIL:=admin@example.com}"
: "${SMOKE_ADMIN_PASSWORD:=changeme}"
: "${SERVER_PORT:=8102}"

BASE="${1:-http://localhost:8080/CCMS}"
SERVER="http://localhost:${SERVER_PORT}"
PASS=0
FAIL=0
TOTAL=0
LOGFILE="/tmp/ccms-smoke-test-$(date +%Y%m%d-%H%M%S).log"

echo "==================================================" | tee "$LOGFILE"
echo " CSPL/CCMS - Smoke Test Report" | tee -a "$LOGFILE"
echo " Date: $(date)" | tee -a "$LOGFILE"
echo " Base URL: $BASE" | tee -a "$LOGFILE"
echo " Server URL: $SERVER" | tee -a "$LOGFILE"
echo "==================================================" | tee -a "$LOGFILE"
echo "" | tee -a "$LOGFILE"

test_endpoint() {
    local desc="$1" method="$2" url="$3" expect_code="$4" expect_body="$5"
    TOTAL=$((TOTAL + 1))
    local resp_file="/tmp/ccms_test_resp_$$.txt"
    local http_code

    if [ "$method" = "POST" ]; then
        local data="$6"
        http_code=$(curl -s -o "$resp_file" -w "%{http_code}" -X POST \
            -H "Content-Type: application/json" \
            -d "$data" "$url" --max-time 15 2>/dev/null)
    else
        http_code=$(curl -s -o "$resp_file" -w "%{http_code}" "$url" --max-time 15 2>/dev/null)
    fi

    local body
    body=$(head -c 500 "$resp_file" 2>/dev/null)
    local result="PASS"
    local reason=""

    if [ "$http_code" = "000" ]; then
        result="FAIL"
        reason="connection_error/timeout"
    elif [ "$http_code" != "$expect_code" ]; then
        result="FAIL"
        reason="expected_$expect_code_got_$http_code"
    elif [ -n "$expect_body" ] && ! echo "$body" | grep -q "$expect_body"; then
        result="FAIL"
        reason="body_missing_[$expect_body]"
    fi

    if [ "$result" = "PASS" ]; then
        PASS=$((PASS + 1))
        echo "  PASS: $desc [$http_code]" | tee -a "$LOGFILE"
    else
        FAIL=$((FAIL + 1))
        echo "  FAIL: $desc [$reason]" | tee -a "$LOGFILE"
        echo "    Body: $body" | tee -a "$LOGFILE"
    fi

    rm -f "$resp_file"
}

LOGIN_QS="?name=${SMOKE_ADMIN_EMAIL}&password=${SMOKE_ADMIN_PASSWORD}"

echo "--- AUTH / LOGIN (4 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Login valid credentials" GET "${BASE}/superadmin/user/login${LOGIN_QS}" 200 '"status":"100"'
test_endpoint "Login bad password" GET "${BASE}/superadmin/user/login?name=${SMOKE_ADMIN_EMAIL}&password=wrong" 200 '"status":"00"'
test_endpoint "Login missing name" GET "${BASE}/superadmin/user/login?password=${SMOKE_ADMIN_PASSWORD}" 400 ""
test_endpoint "Login missing password" GET "${BASE}/superadmin/user/login?name=${SMOKE_ADMIN_EMAIL}" 400 ""

echo "" | tee -a "$LOGFILE"
echo "--- DASHBOARD (5 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Dashboard counts" GET \
    "$BASE/dashboard/count?distrtict=ALL&mandal=ALL&gp=ALL" 200 ""
test_endpoint "Map data" GET \
    "$BASE/dashboard/map_data?distrtict=ALL&mandal=ALL&gp=ALL" 200 ""
test_endpoint "Map data empty filter" GET \
    "$BASE/dashboard/map_data?distrtict=&mandal=&gp=" 200 ""
test_endpoint "Instant data by ID" GET \
    "$BASE/dashboard/instant_data_id/0" 200 ""
test_endpoint "Instant data nonexistent" GET \
    "$BASE/dashboard/instant_data_id/NONEXISTENT" 200 ""

echo "" | tee -a "$LOGFILE"
echo "--- DCU (5 tests) ---" | tee -a "$LOGFILE"
test_endpoint "DCU list" GET "$BASE/dcu/list" 200 ""
test_endpoint "DCU handshake list" GET \
    "$BASE/dcu/handshake_list?distrtict=ALL&mandal=ALL&gp=ALL" 200 ""
test_endpoint "DCU name list" GET \
    "$BASE/dcu/dcu_name_list?distrtict=ALL&mandal=ALL&gp=ALL" 200 ""
test_endpoint "DCU IO list" GET "$BASE/dcu/io_list" 200 ""
test_endpoint "DCU handshake info by id" GET \
    "$BASE/dcu/handshake_info_by_id?distrtict=ALL&mandal=ALL&gp=ALL&name=DCU001" 200 ""

echo "" | tee -a "$LOGFILE"
echo "--- EVENTS (4 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Event counts" GET "$BASE/events/event_counts" 200 ""
test_endpoint "Events between dates" GET \
    "$BASE/events/events_between_date?id=ALL&start_date=2024-01-01&end_date=2024-12-31" 200 ""
test_endpoint "Events invalid date range" GET \
    "$BASE/events/events_between_date?id=ALL&start_date=not-a-date&end_date=not-a-date" 200 ""
test_endpoint "Events missing params" GET \
    "$BASE/events/events_between_date" 400 ""

echo "" | tee -a "$LOGFILE"
echo "--- FILTER (4 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Filter list" GET "$BASE/filter/list" 200 ""
test_endpoint "Get mandal ALL" GET "$BASE/filter/get_mandal?distrtict=ALL" 200 ""
test_endpoint "Get gp ALL" GET "$BASE/filter/get_gp?mandal=ALL" 200 ""
test_endpoint "Get village" GET "$BASE/filter/get_vilage?gp=ALL" 200 ""

echo "" | tee -a "$LOGFILE"
echo "--- DEVICE CONFIG (3 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Lights on" GET \
    "$BASE/device_conf/lights_on?device_serial_number=1905HY1P1C009534&device_identifier=2043" 200 ""
test_endpoint "Lights off" GET \
    "$BASE/device_conf/lights_off?device_serial_number=1905HY1P1C009534&device_identifier=2043" 200 ""
test_endpoint "Lights on empty params" GET \
    "$BASE/device_conf/lights_on?device_serial_number=&device_identifier=" 200 ""

echo "" | tee -a "$LOGFILE"
echo "--- USERS (2 tests) ---" | tee -a "$LOGFILE"
test_endpoint "User list (requires auth)" GET "$BASE/superadmin/user/list" 400 ""
test_endpoint "User create empty" POST \
    "$BASE/superadmin/user/create" 200 "" '{}'

echo "" | tee -a "$LOGFILE"
echo "--- SCHEDULER (1 test) ---" | tee -a "$LOGFILE"
test_endpoint "Scheduler list" GET "$BASE/scheduler/list" 200 ""

echo "" | tee -a "$LOGFILE"
echo "--- NODE (1 test) ---" | tee -a "$LOGFILE"
test_endpoint "Node list (requires auth)" GET "$BASE/node/list" 400 ""

echo "" | tee -a "$LOGFILE"
echo "--- MISC (5 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Default DCU config" POST "$BASE/conf/get_dcu_defult_conf" 200 ""
test_endpoint "System config list" GET "$BASE/cloudsms/sym_list" 200 ""
test_endpoint "CCMS index page" GET "http://localhost:8080/CCMS/" 200 ""
test_endpoint "IO by ID" GET "$BASE/io/get_io_details/0" 200 ""
test_endpoint "IO data between dates" GET \
    "$BASE/io/io_data_between_date?id=0&start_date=2024-01-01&end_date=2024-12-31" 200 ""

echo "" | tee -a "$LOGFILE"
echo "--- SERVER/ccms (5 tests) ---" | tee -a "$LOGFILE"
test_endpoint "Server push manuval on" GET \
    "$SERVER/user/push/manuval_on?dcu_serial_number=1905HY1P1C009534&dcu_identifier=2043" 200 "DONE"
test_endpoint "Server push manuval off" GET \
    "$SERVER/user/push/manuval_off?dcu_serial_number=1905HY1P1C009534&dcu_identifier=2043" 200 "DONE"
test_endpoint "Server hafe open connections" GET \
    "$SERVER/user/push/hafe_open_connections" 200 "DONE"
test_endpoint "Server sync sys conf" GET \
    "$SERVER/user/push/sys_conf?dcu_id=TEST&buffer=55AA&dcu_identifier=0&node_data=&file_idetifier=TEST" 200 "OK"
test_endpoint "Server ping" GET "$SERVER/user/push/hafe_open_connections" 200 "DONE"

echo "" | tee -a "$LOGFILE"
echo "==================================================" | tee -a "$LOGFILE"
echo " TOTAL: $TOTAL  |  PASS: $PASS  |  FAIL: $FAIL" | tee -a "$LOGFILE"
echo "==================================================" | tee -a "$LOGFILE"

# Exit with failure if any test failed
[ $FAIL -eq 0 ]
exit $?
