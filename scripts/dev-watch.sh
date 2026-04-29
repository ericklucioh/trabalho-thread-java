#!/bin/sh

set -eu

: "${POLL_INTERVAL:=1}"
: "${SOURCE_ROOT:=src/main/java}"

snapshot() {
  find "$SOURCE_ROOT" -type f -name '*.java' | sort | xargs sha1sum | sha1sum | awk '{ print $1 }'
}

run_app() {
  sh ./scripts/compile.sh
  java -cp target/classes thread.ui.UiApp
}

echo "Modo dev ativo"
echo "SOURCE_ROOT=$SOURCE_ROOT"
echo "Aguardando alteracoes em $SOURCE_ROOT"

last_snapshot=""

while :; do
  current_snapshot="$(snapshot)"

  if [ "$current_snapshot" != "$last_snapshot" ]; then
    clear >/dev/null 2>&1 || true
    echo "Recompilando e executando interface..."
    run_app
    last_snapshot="$current_snapshot"
  fi

  sleep "$POLL_INTERVAL"
done
