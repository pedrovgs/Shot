#!/usr/bin/env bash

echo "Starting screenshot tests emulator 📸📱!"

$ANDROID_HOME/emulator/emulator -avd test -skin 768x1280 -no-audio &

echo "Emulator ready. Hapy testing 😃"