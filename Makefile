-include .env

MVN ?= mvn
JAR := target/trabalho-thread-java-1.0.0-SNAPSHOT.jar

NAME ?= Sharon Sullivan
TYPE ?= 1
DATASET_G_DIR ?= ./dataset_g
DATASET_P_DIR ?= ./dataset_p
RESULT_DIR ?= ./results

export DATASET_G_DIR
export DATASET_P_DIR
export RESULT_DIR
export NAME
export TYPE

FIRST_GOAL := $(firstword $(MAKECMDGOALS))
SECOND_GOAL := $(word 2,$(MAKECMDGOALS))

ifneq ($(filter $(FIRST_GOAL),sync async parallel),)
ifneq ($(SECOND_GOAL),)
TYPE := $(SECOND_GOAL)
$(eval $(SECOND_GOAL): ; @:)
endif
endif

.PHONY: help build test package prepare sync async parallel clean

help:
	@printf '%s\n' \
		'Targets:' \
		'  make build        Build the project and run tests' \
		'  make test         Run tests only' \
		'  make package      Build the jar without tests' \
		'  make sync [TYPE]  Run the synchronous search' \
		'  make async [TYPE] Run the asynchronous search' \
		'  make clean        Remove target/' \
		'' \
		'Examples:' \
		'  make sync' \
		'  make sync 4' \
		'  make async 2'

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

parallel: async

clean:
	$(MVN) clean
