-include .env

JAR := target/trabalho-thread-java-1.0.0-SNAPSHOT.jar
DOCKER_COMPOSE := docker compose

.PHONY: help build test package prepare run sync async benchmark clean

help:
	@printf '%s\n' \
		'Targets:' \
		'  make build              Build the project and run tests in Docker' \
		'  make test               Run tests only in Docker' \
		'  make package            Build the jar without tests in Docker' \
		'  make prepare            Create RESULT_DIR if needed' \
		'  make run                Run the configured MODE/TARGET in Docker' \
		'  make sync TYPE=<type> TARGET=<target>             Run the synchronous search for one type' \
		'  make async TYPE=<type> TARGET=<target>            Run the parallel search for one type' \
		'  make benchmark          Run sync and async for all TYPES' \
		'  make clean              Remove target/' \
		'' \
		'Examples:' \
		'  make sync TYPE=<type> TARGET=<target>' \
		'  make async TYPE=<type> TARGET=<target>' \
		'  make benchmark TYPES="<types>" TARGET=<target>'

build:
	$(DOCKER_COMPOSE) run --rm --build dev mvn clean verify

test:
	$(DOCKER_COMPOSE) run --rm --build dev mvn test

package:
	$(DOCKER_COMPOSE) run --rm --build dev mvn -q -DskipTests package

prepare:
	mkdir -p "$(RESULT_DIR)"

run: prepare package
	$(DOCKER_COMPOSE) run --rm --build app "$(MODE)" "$(TARGET)"

sync: prepare package
	$(DOCKER_COMPOSE) run --rm --build app sync "$(TARGET)"

async: prepare package
	$(DOCKER_COMPOSE) run --rm --build app parallel "$(TARGET)"

benchmark: prepare package
	@for type in $(TYPES); do \
		printf '%s\n' "== sync TYPE=$$type =="; \
		$(MAKE) --no-print-directory sync TYPE=$$type TARGET="$(TARGET)"; \
		printf '%s\n' "== async TYPE=$$type =="; \
		$(MAKE) --no-print-directory async TYPE=$$type TARGET="$(TARGET)"; \
	done

clean:
	rm -rf target
