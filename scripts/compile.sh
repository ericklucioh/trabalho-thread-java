#!/bin/sh

set -eu

SOURCE_DIR="${SOURCE_DIR:-src/main/java}"
OUTPUT_DIR="${OUTPUT_DIR:-target/classes}"

mkdir -p "$OUTPUT_DIR"

tmp_sources="$(mktemp)"
trap 'rm -f "$tmp_sources"' EXIT

find "$SOURCE_DIR" -type f -name '*.java' | sort > "$tmp_sources"

javac -d "$OUTPUT_DIR" @"$tmp_sources"
