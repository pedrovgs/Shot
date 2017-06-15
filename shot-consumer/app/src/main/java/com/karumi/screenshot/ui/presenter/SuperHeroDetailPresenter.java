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

package com.karumi.screenshot.ui.presenter;

import com.karumi.screenshot.model.SuperHero;
import com.karumi.screenshot.usecase.GetSuperHeroByName;
import javax.inject.Inject;

public class SuperHeroDetailPresenter extends Presenter<SuperHeroDetailPresenter.View> {

  private final GetSuperHeroByName getSuperHeroByName;

  private String name;

  @Inject public SuperHeroDetailPresenter(GetSuperHeroByName getSuperHeroByName) {
    this.getSuperHeroByName = getSuperHeroByName;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override public void initialize() {
    super.initialize();
    getSuperHeroByName.get(name, new GetSuperHeroByName.Callback() {
      @Override public void onSuperHeroLoaded(SuperHero superHero) {
        View view = getView();
        view.hideLoading();
        view.showSuperHero(superHero);
      }
    });
  }

  public interface View extends Presenter.View {

    void showSuperHero(SuperHero superHero);
  }
}
