-include .env

MVN ?= mvn
JAR := target/trabalho-thread-java-1.0.0-SNAPSHOT.jar

NAME ?= Sharon Sullivan
TYPE ?= 1
TYPES ?= 1 2 3 4 5
DATASET_G_DIR ?= ./dataset_g
DATASET_P_DIR ?= ./dataset_p
RESULT_DIR ?= ./results

.PHONY: help build test package prepare sync async benchmark clean

help:
	@printf '%s\n' \
		'Targets:' \
		'  make build              Build the project and run tests' \
		'  make test               Run tests only' \
		'  make package            Build the jar without tests' \
		'  make prepare            Create RESULT_DIR if needed' \
		'  make sync TYPE=1        Run the synchronous search for one type' \
		'  make async TYPE=1       Run the parallel search for one type' \
		'  make benchmark          Run sync and async for all TYPES' \
		'  make clean              Remove target/' \
		'' \
		'Examples:' \
		'  make sync TYPE=4 NAME="Sharon Sullivan"' \
		'  make async TYPE=2 NAME="Karen Reyes MD"' \
		'  make benchmark TYPES="1 2 3 4 5"'

build:
	$(MVN) clean verify

test:
	$(MVN) test

package:
	$(MVN) -q -DskipTests package

prepare:
	mkdir -p "$(RESULT_DIR)"

sync: prepare package
	DATASET_G_DIR="$(DATASET_G_DIR)" DATASET_P_DIR="$(DATASET_P_DIR)" RESULT_DIR="$(RESULT_DIR)" TYPE="$(TYPE)" java -jar "$(JAR)" sync "$(NAME)"

async: prepare package
	DATASET_G_DIR="$(DATASET_G_DIR)" DATASET_P_DIR="$(DATASET_P_DIR)" RESULT_DIR="$(RESULT_DIR)" TYPE="$(TYPE)" java -jar "$(JAR)" parallel "$(NAME)"

benchmark: prepare package
	@for type in $(TYPES); do \
		printf '%s\n' "== sync TYPE=$$type =="; \
		$(MAKE) --no-print-directory sync TYPE=$$type NAME="$(NAME)"; \
		printf '%s\n' "== async TYPE=$$type =="; \
		$(MAKE) --no-print-directory async TYPE=$$type NAME="$(NAME)"; \
	done

clean:
	$(MVN) clean
