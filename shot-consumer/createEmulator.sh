#!/usr/bin/env bash

echo "Creating a brand new SDCard 💾!"

rm -rf sdcard.img
mksdcard -l e 512M sdcard.img

echo "SDCard created ✅"

echo "Creating a tests-emulator 📱"

echo no | $ANDROID_HOME/tools/bin/avdmanager --verbose create avd --force --name "shot-test-emulator" --abi default/x86_64 --package "system-images;android-23;default;x86_64" --sdcard sdcard.img --device 'Nexus 4'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cp $DIR/config.ini ~/.android/avd/shot-test-emulator.avd/config.ini

echo "Emulator created ✅"