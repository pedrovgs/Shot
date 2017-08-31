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

import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;
import com.facebook.testing.screenshot.ScreenshotRunner;

public class ScreenshotTestRunner extends AndroidJUnitRunner {

  @Override public void onCreate(Bundle args) {
    super.onCreate(args);
    ScreenshotRunner.onCreate(this, args);
  }

  @Override public void finish(int resultCode, Bundle results) {
    ScreenshotRunner.onDestroy();
    super.finish(resultCode, results);
  }
}
