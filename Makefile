-include .env

JAR := target/trabalho-thread-java-1.0.0-SNAPSHOT.jar
DOCKER_COMPOSE := docker compose

.PHONY: help build test package run dev gui-allow clean

help:
	@printf '%s\n' \
		'Targets:' \
		'  make build              Compile the project in Docker' \
		'  make test               Recompile the project in Docker' \
		'  make package            Build the jar in Docker' \
		'  make run                Open the Swing interface in Docker' \
		'  make dev                Open the Swing interface with hot reload' \
		'  make gui-allow          Allow the local X server to accept Docker windows' \
		'  make clean              Remove target/' \
		'' \
		'Examples:' \
		'  make run' \
		'  make dev' \
		'  make gui-allow'

build:
	$(DOCKER_COMPOSE) run --rm --build dev sh -lc 'sh ./scripts/compile.sh'

test:
	$(DOCKER_COMPOSE) run --rm --build dev sh -lc 'sh ./scripts/compile.sh'

package:
	$(DOCKER_COMPOSE) run --rm --build dev sh -lc 'sh ./scripts/package.sh'

run: package
	$(DOCKER_COMPOSE) run --rm --build app

dev:
	$(DOCKER_COMPOSE) up --build dev

gui-allow:
	xhost +SI:localuser:root

clean:
	rm -rf target
