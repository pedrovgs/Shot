/*
 * Copyright (C) 2017 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.screenshot;

import android.app.Application;
import android.support.annotation.VisibleForTesting;
import com.karumi.screenshot.di.DaggerMainComponent;
import com.karumi.screenshot.di.MainComponent;

public class SuperHeroesApplication extends Application {

  private MainComponent mainComponent;

  @Override public void onCreate() {
    super.onCreate();
    mainComponent = DaggerMainComponent.create();
  }

  public MainComponent getMainComponent() {
    return mainComponent;
  }

  @VisibleForTesting public void setComponent(MainComponent mainComponent) {
    this.mainComponent = mainComponent;
  }
}
