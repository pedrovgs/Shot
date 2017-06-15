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

package com.karumi.screenshot.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.karumi.screenshot.R;
import com.karumi.screenshot.ui.presenter.Presenter;
import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements Presenter.View {

  @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
  @Nullable @Bind(R.id.progress_bar) View loadingView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    initializeButterKnife();
    initializeToolbar();
  }

  public abstract int getLayoutId();

  @Override public void showLoading() {
    if (loadingView != null) {
      loadingView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideLoading() {
    if (loadingView != null) {
      loadingView.setVisibility(View.GONE);
    }
  }

  private void initializeButterKnife() {
    ButterKnife.bind(this);
  }

  protected void initializeToolbar() {
    if (toolbar != null) {
      setSupportActionBar(toolbar);
    }
  }
}
