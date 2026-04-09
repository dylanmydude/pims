#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
MYSQL_PID="$ROOT_DIR/vendor/mysql/run/mysql.pid"

if [ ! -f "$MYSQL_PID" ]; then
    echo "MySQL is not running."
    exit 0
fi

PID="$(cat "$MYSQL_PID")"

if kill -0 "$PID" 2>/dev/null; then
    kill "$PID"
    while kill -0 "$PID" 2>/dev/null; do
        sleep 1
    done
    echo "MySQL stopped."
else
    echo "MySQL process was not running."
fi

rm -f "$MYSQL_PID"
