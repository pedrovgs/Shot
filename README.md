# ![Karumi logo][karumilogo]Shot [![Build Status](https://travis-ci.org/Karumi/Shot.svg?branch=master)](https://travis-ci.org/Karumi/Shot)

Shot is a [Gradle](https://gradle.org/) plugin that simplifies the execution of screenshot tests using [Screenshot Tests For Android by Facebook](http://facebook.github.io/screenshot-tests-for-android/).

## What is this?

``Shot`` is a Gradle plugin thought to run screenshot tests for Android using the [screenshot testing Facebook SDK](http://facebook.github.io/screenshot-tests-for-android/).

Record your screenshots executing ``./gradlew executeScreenshots -Precord``

![recording](./art/recording.gif)

And verify your tests executing ``./gradlew executeScreenshots``

![verifying](./art/verifying.gif)

If Shot finds any error in your tests execution the Gradle plugin will show a report as follows:

![errorReport](./art/errorReport.png)

**In a future PR we will generate a rich HTML report** 

You can find the complete Facebook SDK documentation [here](https://facebook.github.io/screenshot-tests-for-android/).

## Getting started

Setup the Gradle plugin:

```groovy
  buildscript {
    // ...
    dependencies {
      // ...
      classpath 'com.karumi:shot:0.1.0'
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
    packageTestApkTask = 'package<FlavorName><BuildTypeName>AndroidTest'
  }
```

The flavor used is the one selected to execute your screenshot tests.

An example could be:

```groovy
  shot {
    appId = 'com.my.app'
    instrumentationTestTask = 'connectedFreeAppDebugAndroidTest'
    packageTestApkTask = 'packageFreeAppAndroidTest'
  }
```


## Writing tests

This repository contains just a Gradle plugin based on the Facebook SDK already mentioned. If you need to review how to write a screenshot test we strongly recommend you to review the [official documentation](https://facebook.github.io/screenshot-tests-for-android).

## Recording tests

You can record your screenshot tests executing this command:

```
./gradlew executeScreenshotTests -Precord
```

This will execute all your integration tests and it will pull all the generated screenshots into your repository so you can easily add them to the version control system.

## Executing tests

Once you have a bunch of screenshot tests recorded you can easily verify if the behaviour of your app is the correct one executing this command:

```
./gradlew executeScreenshotTests
```

**After executing your screenshot tests using the Gradle task ``executeScreenshotTests`` a report with all your screenshots will be generated.**

![shotTasksHelp](./art/tasksDescription.png)

[karumilogo]: https://cloud.githubusercontent.com/assets/858090/11626547/e5a1dc66-9ce3-11e5-908d-537e07e82090.png
