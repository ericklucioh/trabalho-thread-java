#!/bin/sh

set -eu

JAR_FILE="${JAR_FILE:-target/trabalho-thread-java-1.0.0-SNAPSHOT.jar}"

sh ./scripts/compile.sh
mkdir -p "$(dirname "$JAR_FILE")"
jar --create --file "$JAR_FILE" --main-class thread.ui.UiApp -C target/classes .
