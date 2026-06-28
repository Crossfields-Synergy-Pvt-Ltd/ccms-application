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

BACKUP_DIR="backups/$(date +%Y-%m-%d_%H%M)"
mkdir -p "$PROJECT_ROOT/$BACKUP_DIR"

echo "============================================"
echo "  CCMS Backup - $(date)"
echo "============================================"
echo ""

# --- MongoDB ---
echo "[1/3] Backing up MongoDB (ccms database) ..."
docker exec cspl-mongodb mongodump --db ccms --archive > "$PROJECT_ROOT/$BACKUP_DIR/mongodb-ccms.archive"
echo "      -> $BACKUP_DIR/mongodb-ccms.archive ($(du -h "$PROJECT_ROOT/$BACKUP_DIR/mongodb-ccms.archive" | cut -f1))"

# --- MySQL ---
echo "[2/3] Backing up MySQL ($MYSQL_DATABASE database) ..."
docker exec cspl-mysql mysqldump -u root "-p${MYSQL_ROOT_PASSWORD}" "$MYSQL_DATABASE" > "$PROJECT_ROOT/$BACKUP_DIR/mysql-${MYSQL_DATABASE}.sql"
echo "      -> $BACKUP_DIR/mysql-${MYSQL_DATABASE}.sql ($(du -h "$PROJECT_ROOT/$BACKUP_DIR/mysql-${MYSQL_DATABASE}.sql" | cut -f1))"

# --- Historical CSV data ---
echo "[3/3] Backing up historical CSV data ..."
if [ -d "$PROJECT_ROOT/data/dontdelete" ] && [ "$(ls -A "$PROJECT_ROOT/data/dontdelete" 2>/dev/null)" ]; then
    tar -czf "$PROJECT_ROOT/$BACKUP_DIR/historical-data.tar.gz" -C "$PROJECT_ROOT/data" dontdelete/
    echo "      -> $BACKUP_DIR/historical-data.tar.gz ($(du -h "$PROJECT_ROOT/$BACKUP_DIR/historical-data.tar.gz" | cut -f1))"
else
    echo "      -> (no historical data found, skipping)"
fi

echo ""
echo "============================================"
echo "  Backup complete: $PROJECT_ROOT/$BACKUP_DIR"
echo "============================================"
