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

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class ScreenshotTest {

  protected void compareScreenshot(Activity activity) {
    Screenshot.snapActivity(activity).record();
  }

  protected void compareScreenshot(RecyclerView.ViewHolder holder, int height) {
    compareScreenshot(holder.itemView, height);
  }

  protected void compareScreenshot(View view, int height) {
    Context context = getInstrumentation().getTargetContext();
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(metrics);
    ViewHelpers.setupView(view)
        .setExactHeightPx(context.getResources().getDimensionPixelSize(height))
        .setExactWidthPx(metrics.widthPixels)
        .layout();
    Screenshot.snap(view).record();
  }
}
