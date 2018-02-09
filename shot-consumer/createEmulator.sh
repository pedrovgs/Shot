#!/usr/bin/env bash
# fail if any commands fails
set -e

echo "Creating a brand new SDCard 💾!"

rm -rf sdcard.img
mksdcard -l e 512M sdcard.img

echo "SDCard created ✅"

echo "Creating a tests-emulator 📱"

echo no | $ANDROID_HOME/tools/bin/avdmanager --verbose create avd --force --name "shot-emulator" --package "system-images;android-25;google_apis;x86" --sdcard sdcard.img --device 'Nexus 5X'

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cp $DIR/config.ini ~/.android/avd/shot-emulator.avd/config.ini

echo "Emulator created ✅"