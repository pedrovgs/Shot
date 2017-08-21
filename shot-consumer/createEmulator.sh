#!/usr/bin/env bash

echo "Creating a brand new SDCard 💾!"

rm -rf sdcard.img
mksdcard -l e 512M sdcard.img

echo "SDCard created ✅"

echo "Creating a tests-emulator 📱"

echo no | $ANDROID_HOME/tools/bin/avdmanager --verbose create avd --force --name "tests-emulator" --abi default/armeabi-v7a --package "system-images;android-22;default;armeabi-v7a" --sdcard sdcard.img

echo "Emulator created ✅"