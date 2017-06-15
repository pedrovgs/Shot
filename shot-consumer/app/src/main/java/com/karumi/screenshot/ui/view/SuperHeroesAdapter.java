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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.karumi.screenshot.R;
import com.karumi.screenshot.model.SuperHero;
import com.karumi.screenshot.ui.presenter.SuperHeroesPresenter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SuperHeroesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final SuperHeroesPresenter presenter;
  private final List<SuperHero> superHeroes;

  public SuperHeroesAdapter(SuperHeroesPresenter presenter) {
    this.presenter = presenter;
    this.superHeroes = new ArrayList<>();
  }

  void addAll(Collection<SuperHero> collection) {
    superHeroes.addAll(collection);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.super_hero_row, parent, false);
    return new SuperHeroViewHolder(view, presenter);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    SuperHeroViewHolder superHeroViewHolder = (SuperHeroViewHolder) holder;
    SuperHero superHero = superHeroes.get(position);
    superHeroViewHolder.render(superHero);
  }

  @Override public int getItemCount() {
    return superHeroes.size();
  }
}
