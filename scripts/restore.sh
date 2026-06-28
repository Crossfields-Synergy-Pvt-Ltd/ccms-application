#!/usr/bin/env bash
set -euo pipefail

# Load .env from project root if present
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
if [ -f "$PROJECT_ROOT/.env" ]; then
    set -a
    # shellcheck disable=SC1091
    . "$PROJECT_ROOT/.env"
    set +a
fi

: "${MYSQL_ROOT_PASSWORD:=root}"
: "${MYSQL_DATABASE:=employee_db}"

if [ $# -lt 1 ]; then
    echo "Usage: $0 <backup-directory>"
    echo ""
    echo "Examples:"
    echo "  $0 backups/2026-06-27_1230"
    echo "  $0 /absolute/path/to/backup"
    exit 1
fi

BACKUP_DIR="$1"

# Resolve relative paths
if [[ "$BACKUP_DIR" != /* ]]; then
    BACKUP_DIR="$PROJECT_ROOT/$BACKUP_DIR"
fi

if [ ! -d "$BACKUP_DIR" ]; then
    echo "Error: Backup directory not found: $BACKUP_DIR"
    exit 1
fi

echo "============================================"
echo "  CCMS Restore - $(date)"
echo "  Source: $BACKUP_DIR"
echo "============================================"
echo ""

# --- Count what's available ---
MONGODB_ARCHIVE="$BACKUP_DIR/mongodb-ccms.archive"
MYSQL_DUMP="$BACKUP_DIR/mysql-${MYSQL_DATABASE}.sql"
CSV_TAR="$BACKUP_DIR/historical-data.tar.gz"

# --- MongoDB ---
if [ -f "$MONGODB_ARCHIVE" ]; then
    echo "[1/3] Restoring MongoDB (ccms database) ..."
    docker exec -i cspl-mongodb mongorestore --db ccms --drop --archive < "$MONGODB_ARCHIVE"
    echo "      -> Done"
else
    echo "[1/3] MongoDB backup not found, skipping"
fi

# --- MySQL ---
if [ -f "$MYSQL_DUMP" ]; then
    echo "[2/3] Restoring MySQL ($MYSQL_DATABASE database) ..."
    docker exec -i cspl-mysql mysql -u root "-p${MYSQL_ROOT_PASSWORD}" "$MYSQL_DATABASE" < "$MYSQL_DUMP"
    echo "      -> Done"
else
    echo "[2/3] MySQL backup not found, skipping"
fi

# --- Historical CSV data ---
if [ -f "$CSV_TAR" ]; then
    echo "[3/3] Restoring historical CSV data ..."
    mkdir -p "$PROJECT_ROOT/data"
    tar -xzf "$CSV_TAR" -C "$PROJECT_ROOT/data/"
    echo "      -> Done"
else
    echo "[3/3] Historical data backup not found, skipping"
fi

echo ""
echo "============================================"
echo "  Restore complete"
echo "============================================"
echo ""
echo "Restart CCMS_UI to pick up restored CSV data:"
echo "  docker compose restart ccms_ui"
