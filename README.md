# ![Karumi logo][karumilogo]Shot [![Build Status](https://app.bitrise.io/app/c7c358c5be663f7c/status.svg?token=SOM_wLR4WZScWxIYKeMV3A&branch=master)](https://app.bitrise.io/app/c7c358c5be663f7c)

Shot is a [Gradle](https://gradle.org/) plugin that simplifies the execution of screenshot tests using [Screenshot Tests For Android by Facebook](http://facebook.github.io/screenshot-tests-for-android/).

## What is this?

``Shot`` is a Gradle plugin thought to run screenshot tests for Android using the [screenshot testing Facebook SDK](http://facebook.github.io/screenshot-tests-for-android/).

**Since Shot 0.3.0 a simple but powerful HTML report is generated after every verification or screenshots recording execution. Here you have an example of the [recording](./art/recordReport.png) and [verification](./art/verificationReport.png) report generated.** 

![smallVerificationReport1](./art/smallVerificationReport1.png)
![smallVerificationReport2](./art/smallVerificationReport2.png)

Record your screenshots executing ``./gradlew executeScreenshotTests -Precord``

![recording](./art/recording.gif)

And verify your tests executing ``./gradlew executeScreenshotTests``

![verifying](./art/verifying.gif)

If Shot finds any error in your tests execution the Gradle plugin will show a report as follows:

![errorReport](./art/errorReport.png)

You can find the complete Facebook SDK documentation [here](https://facebook.github.io/screenshot-tests-for-android/).

## Getting started

Setup the Gradle plugin:

```groovy
  buildscript {
    // ...
    dependencies {
      // ...
      classpath 'com.karumi:shot:3.0.1'
    }
  }
  apply plugin: 'shot'

  shot {
    appId = 'YOUR_APPLICATION_ID'
  }
```

This plugin sets up a few convenience commands you can list executing ``./gradlew tasks`` and reviewing the ``Shot`` associated tasks:

**If you are using flavors update your shot configuration inside the ``build.gradle`` file as follows:**

```groovy
  shot {
    appId = 'YOUR_APPLICATION_ID'
    instrumentationTestTask = 'connected<FlavorName><BuildTypeName>AndroidTest'
  }
```

If for some reason you are running your tests on a different machine and you want to skip the instrumentation tests execution and just compare the sources remember you can use the following shot configuration:

```groovy
  shot {
    runInstrumentation = false
  }
```

The flavor used is the one selected to execute your screenshot tests.

An example could be:

```groovy
  shot {
    appId = 'com.my.app'
    instrumentationTestTask = 'connectedFreeAppDebugAndroidTest'
  }
```

The screenshots library needs the ``WRITE_EXTERNAL_STORAGE`` permission. When testing a library, add this permission to the manifest of the instrumentation apk. If you are testing an application, add this permission to the app under test. To grant this permission you can create an ``AndroidManifest.xml`` file inside the ``androidTest`` folder. Here is an example:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="<YOUR_APP_ID>.tests"
    android:sharedUserId="<YOUR_APP_ID>.uid">

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

</manifest>
```

Remember to configure the instrumentation test runner in your ``build.gradle`` as follows:

```groovy
android {
    // ...
    defaultConfig {
        // ...
        testInstrumentationRunner "com.myapp.ScreenshotTestRunner"
    }
    // ...
```

In order to do this, you'll have to create a class named ``ScreenshotTestRunner``, like the following one, inside your instrumentation tests source folder:

```java
public class ScreenshotTestRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle args) {
        super.onCreate(args);
        ScreenshotRunner.onCreate(this, args);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        ScreenshotRunner.onDestroy();
        super.finish(resultCode, results);
    }
}
```

Now you are ready to use the ``Screenshot`` API from your tests:

```java
@Test
public void theActivityIsShownProperly() {
        Activity mainActivity = startMainActivity();
       /*
         * Take the actual screenshot. At the end of this call, the screenshot
         * is stored on the device and the gradle plugin takes care of
         * pulling it and displaying it to you in nice ways.
         */
        Screenshot.snapActivity(activity).record();
}
```

***You can find a complete example in this repository under the folder named ``shot-consumer`` or review [this kata](https://github.com/Karumi/KataScreenshotAndroid/).***

The [official documentation](https://facebook.github.io/screenshot-tests-for-android).

Now you are ready to record and verify your screenshot tests! 

## Recording tests

You can record your screenshot tests executing this command:

```shell
./gradlew executeScreenshotTests -Precord
```

This will execute all your integration tests and it will pull all the generated screenshots into your repository so you can easily add them to the version control system.

## Executing tests

Once you have a bunch of screenshot tests recorded you can easily verify if the behaviour of your app is the correct one executing this command:

```shell
./gradlew executeScreenshotTests
```

**After executing your screenshot tests using the Gradle task ``executeScreenshotTests`` a report with all your screenshots will be generated.**

![shotTasksHelp](./art/tasksDescription.png)

[karumilogo]: https://cloud.githubusercontent.com/assets/858090/11626547/e5a1dc66-9ce3-11e5-908d-537e07e82090.png

## Executing tests in multiple devices

If after some time writing screenshot tests your build takes too long to run our recommendation is to run your tests in multiple devices. **Sharding your tests execution will split your test suite into different devices so your tests execution time will be reduced. This feature is not designed to test the UI across different platforms or screen resolutions, to do that we'd recommend you to configure the size of the screenshot taken by modifing the view height and width.** To run your tests in multiple devices you can use [Composer](https://github.com/gojuno/composer) and the official [Gradle Plugin they provide](https://github.com/trevjonez/composer-gradle-plugin). Composer will take all your tests and will split the test suite execution between all the connected devices. **Remember, if you are going to use more than one device all the devices should use the same Android OS and the same screen resolution and density!** Keep also in mind composer needs Gradle 5.4.1 to be able to run your tests using multiple devices.

Once you've configured composer to run your tests you only need to update Shot to use the composer task as the instrumentation test task as follows:

```groovy
shot {
  appId = 'YOUR_APPLICATION_ID'
  instrumentationTestTask = "testDebugComposer"
}
```

Take into account the ``instrumentationTestTask`` could be different if you use different flavors or build types. Remember also you should use Shot > 3.0.0 because this feature was introduced in this release!
 
## CI Reporting

Shot generates an HTML report you can review at the end of the recording or verification build. However, if you are running Shot in a CI environment which does not support saving the reporting files generated by the build you can verify your tests using this command ``./gradlew executeScreenshotTests -PprintBase64``. This will change how Shot show comparision errors displaying a command you can copy and paste on your local terminal for every screenshot test failed.

## Running only some tests

You can run a single test or test class, just add the `android.testInstrumentationRunnerArguments.class` parameter within your gradle call. This option works for both modes, verification and recording, just remember to add the `-Precord` if you want to do the latter.

**Running all tests in a class:**

```shell
./gradlew executeScreenshotTests -Pandroid.testInstrumentationRunnerArguments.class=com.your.package.YourClassTest
```

**Running a single test:**

```shell
./gradlew executeScreenshotTests -Pandroid.testInstrumentationRunnerArguments.class=com.your.package.YourClassTest#yourTest
```

## Custom dependencies

If you have included in your project a dependency to related to the dexmaker and you are facing this exception: ``com.android.dx.util.DexException: Multiple dex files define``, you can customize how the facebook SDK is added to your project and exclude the dexmaker library as follows:

 ```groovy
   androidTestCompile ('com.facebook.testing.screenshot:core:0.8.0') {
     exclude group: 'com.crittercism.dexmaker', module: 'dexmaker'
     exclude group: 'com.crittercism.dexmaker', module: 'dexmaker-dx'
   }
 ```
 
The Shot plugin automatically detects if you are including a compatible version of the screenshot facebook library in your project and, if it's present, it will not include it again.
 
**Disclaimer**: The only compatible version of the facebook library is 0.8.0 right now, so if you are using any other version we highly encourage to match it with the one Shot is using to avoid problems.

## iOS support

If you want to apply the same testing technique on iOS you can use [Snap.swift](https://github.com/skyweb07/Snap.swift)

License
-------

    Copyright 2018 Karumi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
