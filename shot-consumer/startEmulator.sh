#!/usr/bin/env bash

echo "Starting screenshot tests emulator 📸📱!"

$ANDROID_HOME/emulator/emulator -avd shot-test-emulator -skin 768x1280 -no-audio -no-boot-anim &

echo "Emulator ready. Happy testing 😃"
