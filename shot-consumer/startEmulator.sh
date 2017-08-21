#!/usr/bin/env bash

echo "Starting screenshot tests emulator 📸📱!"

$ANDROID_HOME/emulator/emulator "-avd" "tests-emulator" "-no-boot-anim" &

echo "Emulator ready. Hapy testing 😃"