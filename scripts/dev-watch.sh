#!/bin/sh

set -eu

: "${MODE:?MODE is required}"
: "${TARGET:?TARGET is required}"
: "${POLL_INTERVAL:?POLL_INTERVAL is required}"
: "${SOURCE_ROOT:?SOURCE_ROOT is required}"

snapshot() {
  {
    if [ -d "$SOURCE_ROOT" ]; then
      find "$SOURCE_ROOT" -type f -name '*.java' | sort
    fi

    if [ -f pom.xml ]; then
      printf '%s\n' pom.xml
    fi
  } | while IFS= read -r file; do
    [ -f "$file" ] && sha1sum "$file"
  done | sha1sum | awk '{ print $1 }'
}

run_app() {
  mvn -q -DskipTests compile
  java -cp target/classes thread.App "$MODE" "$TARGET"
}

echo "Modo dev ativo"
echo "MODE=$MODE"
echo "TARGET=$TARGET"
echo "Aguardando alteracoes em $SOURCE_ROOT e pom.xml"

last_snapshot=""

while :; do
  current_snapshot="$(snapshot)"

  if [ "$current_snapshot" != "$last_snapshot" ]; then
    clear >/dev/null 2>&1 || true
    echo "Recompilando e executando..."
    run_app
    last_snapshot="$current_snapshot"
  fi

  sleep "$POLL_INTERVAL"
done
