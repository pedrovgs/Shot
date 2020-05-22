#!/bin/bash

if $ANDROID_HOME/tools/android list avd | grep -q 2_Nexus_5X_API_26; then
    echo "There is an existing an emulator to run screenshot tests"
    exit 0;
fi

echo "Creating a brand new SDCard..."
rm -rf sdcard_2.img
$ANDROID_HOME/emulator/mksdcard -l e 1G sdcard_2.img
echo "SDCard created!"

echo "Downloading the image to create the emulator..."
echo no | $ANDROID_HOME/tools/bin/sdkmanager "system-images;android-26;google_apis;x86"
echo "Image downloaded!"

echo "Creating the emulator to run screenshot tests..."
echo no | $ANDROID_HOME/tools/bin/avdmanager create avd -n 2_Nexus_5X_API_26 -k "system-images;android-26;google_apis;x86" --force --sdcard sdcard_2.img
echo "Emulator created!"

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cp $DIR/config.ini ~/.android/avd/2_Nexus_5X_API_26.avd/config.ini
cp sdcard_2.img ~/.android/avd/2_Nexus_5X_API_26.avd/sdcard_2.img
cp sdcard_2.img.qcow2 ~/.android/avd/2_Nexus_5X_API_26.avd/sdcard_2.img.qcow2
