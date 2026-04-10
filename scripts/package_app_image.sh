#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"
BUILD_DIR="$ROOT_DIR/build"
INPUT_DIR="$BUILD_DIR/jpackage-input"
DIST_DIR="$ROOT_DIR/dist"
APP_NAME="PIMS"
MAIN_CLASS="ui.Main"
MAIN_JAR="pims.jar"
JDBC_JAR="$ROOT_DIR/lib/mysql-connector-j.jar"

if [ ! -f "$JDBC_JAR" ]; then
    echo "Missing JDBC driver: $JDBC_JAR"
    exit 1
fi

rm -rf "$OUT_DIR" "$INPUT_DIR" "$DIST_DIR/$APP_NAME"
mkdir -p "$OUT_DIR" "$INPUT_DIR" "$DIST_DIR"

mapfile -t SOURCES < <(find "$ROOT_DIR/src" -name "*.java" | sort)

if [ "${#SOURCES[@]}" -eq 0 ]; then
    echo "No Java sources found under $ROOT_DIR/src"
    exit 1
fi

echo "Compiling application..."
javac -cp "$JDBC_JAR" -d "$OUT_DIR" "${SOURCES[@]}"

echo "Creating runnable jar..."
jar --create --file "$INPUT_DIR/$MAIN_JAR" --main-class "$MAIN_CLASS" -C "$OUT_DIR" .
cp "$JDBC_JAR" "$INPUT_DIR/"

echo "Packaging native app image..."
jpackage \
    --type app-image \
    --name "$APP_NAME" \
    --input "$INPUT_DIR" \
    --main-jar "$MAIN_JAR" \
    --main-class "$MAIN_CLASS" \
    --dest "$DIST_DIR"

echo "App image created at: $DIST_DIR/$APP_NAME"
