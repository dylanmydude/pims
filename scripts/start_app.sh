#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"
JDBC_JAR="$ROOT_DIR/lib/mysql-connector-j.jar"

if [ ! -f "$JDBC_JAR" ]; then
    echo "Missing JDBC driver: $JDBC_JAR"
    exit 1
fi

mkdir -p "$OUT_DIR"

mapfile -t SOURCES < <(find "$ROOT_DIR/src" -name "*.java" | sort)

if [ "${#SOURCES[@]}" -eq 0 ]; then
    echo "No Java sources found under $ROOT_DIR/src"
    exit 1
fi

echo "Compiling application..."
javac -cp "$JDBC_JAR" -d "$OUT_DIR" "${SOURCES[@]}"

echo "Starting application..."
exec java -cp "$OUT_DIR:$JDBC_JAR" ui.Main
