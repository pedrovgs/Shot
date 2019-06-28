#!/bin/bash

set -e

YELLOW=`tput setaf 3`
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0`

echo -e "${YELLOW}🤖 Checking the correct state of our software \n${NC}"
./start_emulator.sh

echo -e "${YELLOW}🤖    Evaluating our app module build, lint and tests execution:${NC}"
./gradlew executeScreenshotTests

echo -e "${GREEN}🤖 CI tasks execution finished properly"
