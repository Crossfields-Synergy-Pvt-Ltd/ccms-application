#!/usr/bin/env bash

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERVER_DIR="$PROJECT_ROOT/SERVER/ccms"
CCMS_UI_DIR="$PROJECT_ROOT/CCMS_UI/STARTUP/ccms_ui"

PASS=0
FAIL=0

run_suite() {
    local name=$1
    local dir=$2
    local cmd=$3
    echo "=== [$name] $cmd ==="
    cd "$dir" && eval "$cmd" && { echo ">>> $name: PASS"; PASS=$((PASS+1)); } \
        || { echo ">>> $name: FAIL"; FAIL=$((FAIL+1)); }
}

run_suite "SERVER" "$SERVER_DIR" "mvn test"
run_suite "CCMS_UI Java" "$CCMS_UI_DIR" "mvn test"
run_suite "CCMS_UI AngularJS" "$CCMS_UI_DIR" "npm test"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="
exit $FAIL
