#!/usr/bin/env bash

echo "Creating a brand new SDCard 💾!"

rm -rf sdcard.img
mksdcard -l e 512M sdcard.img

echo "SDCard created ✅"

echo "Creating a tests-emulator 📱"

echo no | $ANDROID_HOME/tools/bin/avdmanager --verbose create avd --force --name shot-test-emulator --package 'system-images;android-23;default;armeabi-v7a' --abi armeabi-v7a --device 'Nexus 4' --sdcard sdcard.img

echo "Emulator created ✅"