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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.karumi.screenshot.R;
import com.karumi.screenshot.SuperHeroesApplication;
import com.karumi.screenshot.model.SuperHero;
import com.karumi.screenshot.ui.presenter.SuperHeroesPresenter;
import java.util.List;
import javax.inject.Inject;
import butterknife.Bind;

public class MainActivity extends BaseActivity implements SuperHeroesPresenter.View {

  @Inject SuperHeroesPresenter presenter;

  private SuperHeroesAdapter adapter;

  @Bind(R.id.tv_empty_case) View emptyCaseView;
  @Bind(R.id.recycler_view) RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initializeDagger();
    initializePresenter();
    initializeAdapter();
    initializeRecyclerView();
    presenter.initialize();
  }

  @Override public int getLayoutId() {
    return R.layout.main_activity;
  }

  @Override public void showSuperHeroes(List<SuperHero> superHeroes) {
    adapter.addAll(superHeroes);
    adapter.notifyDataSetChanged();
  }

  @Override public void openSuperHeroScreen(SuperHero superHero) {
    SuperHeroDetailActivity.open(this, superHero.getName());
  }

  @Override public void showEmptyCase() {
    emptyCaseView.setVisibility(View.VISIBLE);
  }

  @Override public void hideEmptyCase() {
    emptyCaseView.setVisibility(View.GONE);
  }

  private void initializeDagger() {
    SuperHeroesApplication app = (SuperHeroesApplication) getApplication();
    app.getMainComponent().inject(this);
  }

  private void initializePresenter() {
    presenter.setView(this);
  }

  private void initializeAdapter() {
    adapter = new SuperHeroesAdapter(presenter);
  }

  private void initializeRecyclerView() {
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
  }
}
