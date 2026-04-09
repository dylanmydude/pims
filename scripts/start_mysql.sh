#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
MYSQL_ROOT="$ROOT_DIR/vendor/mysql/root/usr"
MYSQL_DATA="$ROOT_DIR/vendor/mysql/data"
MYSQL_RUN="$ROOT_DIR/vendor/mysql/run"
MYSQL_LOG="$ROOT_DIR/vendor/mysql/log"
MYSQL_FILES="$ROOT_DIR/vendor/mysql/files"
MYSQL_PID="$MYSQL_RUN/mysql.pid"
MYSQL_SOCKET="$MYSQL_RUN/mysql.sock"
MYSQLD="$MYSQL_ROOT/sbin/mysqld"
LD_PATH="$ROOT_DIR/vendor/mysql/root/usr/lib/x86_64-linux-gnu"

mkdir -p "$MYSQL_DATA" "$MYSQL_RUN" "$MYSQL_LOG" "$MYSQL_FILES"

if [ ! -d "$MYSQL_DATA/mysql" ]; then
    LD_LIBRARY_PATH="$LD_PATH" "$MYSQLD" \
        --no-defaults \
        --initialize-insecure \
        --basedir="$MYSQL_ROOT" \
        --datadir="$MYSQL_DATA" \
        --plugin-dir="$MYSQL_ROOT/lib/mysql/plugin" \
        --lc-messages-dir="$MYSQL_ROOT/share/mysql"
fi

if [ -f "$MYSQL_PID" ] && kill -0 "$(cat "$MYSQL_PID")" 2>/dev/null; then
    echo "MySQL is already running."
    exit 0
fi

LD_LIBRARY_PATH="$LD_PATH" "$MYSQLD" \
    --no-defaults \
    --daemonize \
    --basedir="$MYSQL_ROOT" \
    --datadir="$MYSQL_DATA" \
    --socket="$MYSQL_SOCKET" \
    --pid-file="$MYSQL_PID" \
    --port=3306 \
    --bind-address=127.0.0.1 \
    --plugin-dir="$MYSQL_ROOT/lib/mysql/plugin" \
    --lc-messages-dir="$MYSQL_ROOT/share/mysql" \
    --log-error="$MYSQL_LOG/error.log" \
    --secure-file-priv="$MYSQL_FILES" \
    --mysqlx=0

echo "MySQL started."
echo "Socket: $MYSQL_SOCKET"
echo "PID: $(cat "$MYSQL_PID")"
