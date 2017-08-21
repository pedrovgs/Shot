#!/usr/bin/env bash

echo "Starting screenshot tests emulator 📸📱!"

$ANDROID_HOME/emulator/emulator "-avd" "tests-emulator" "-skin" "480x800"  "-no-boot-anim" "-no-audio" &

echo "Emulator ready. Hapy testing 😃"