# ![Karumi logo][karumilogo]Shot [![Build Status](https://travis-ci.org/Karumi/shot.svg?branch=master)](https://travis-ci.org/Karumi/Shot)

Shot is a [Gradle](https://gradle.org/) plugin that simplifies the execution of screenshot tests using [Screenshot Tests For Android by Facebook](http://facebook.github.io/screenshot-tests-for-android/).

## What is this?

``Shot`` is a Gradle plugin thought to run screenshot tests for Android using the [screenshot testing Facebook SDK](http://facebook.github.io/screenshot-tests-for-android/).

## Getting started

Setup the Gradle plugin:

```groovy
  buildscript {
    // ...
    dependencies {
      // ...
      classpath 'com.karumi:shot:0.0.1'
    }
  }
  apply plugin: 'shot'
```

This plugin sets up a few convenience commands you can review executing ``./gradlew tasks`` and reviews the ``Shot`` associated tasks:

## Writting tests

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