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
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import com.karumi.screenshot.di.MainComponent;
import com.karumi.screenshot.di.MainModule;
import com.karumi.screenshot.model.SuperHero;
import com.karumi.screenshot.model.SuperHeroesRepository;
import com.karumi.screenshot.ui.view.MainActivity;
import com.karumi.screenshot.ui.view.SuperHeroDetailActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.when;

public class MainActivityTest extends ScreenshotTest {

  private static final int ANY_NUMBER_OF_SUPER_HEROES = 10;

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    Activity activity = startActivity();

    compareScreenshot(activity);
  }

  @Test public void showsJustOneSuperHero() {
    givenThereAreSomeSuperHeroes(1);

    Activity activity = startActivity();

    compareScreenshot(activity);
  }

  @Test public void showsSuperHeroesIfThereAreSomeSuperHeroes() {
    givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES);

    Activity activity = startActivity();

    compareScreenshot(activity);
  }

  @Test public void showsAvengersBadgeIfASuperHeroIsPartOfTheAvengersTeam() {
    givenThereAreSomeAvengers(ANY_NUMBER_OF_SUPER_HEROES);

    Activity activity = startActivity();

    compareScreenshot(activity);
  }

  @Test public void doesNotShowAvengersBadgeIfASuperHeroIsNotPartOfTheAvengersTeam() {
    givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES, false);

    Activity activity = startActivity();

    compareScreenshot(activity);
  }

  @Test public void opensSuperHeroDetailActivityOnRecyclerViewItemTapped() {
    List<SuperHero> superHeroes = givenThereAreSomeSuperHeroes();
    int superHeroIndex = 0;
    startActivity();

    onView(withId(R.id.recycler_view)).
        perform(RecyclerViewActions.actionOnItemAtPosition(superHeroIndex, click()));

    SuperHero superHeroSelected = superHeroes.get(superHeroIndex);
    intended(hasComponent(SuperHeroDetailActivity.class.getCanonicalName()));
    intended(hasExtra("super_hero_name_key", superHeroSelected.getName()));
  }

  private List<SuperHero> givenThereAreSomeAvengers(int numberOfAvengers) {
    return givenThereAreSomeSuperHeroes(numberOfAvengers, true);
  }

  private List<SuperHero> givenThereAreSomeSuperHeroes() {
    return givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES);
  }

  private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes) {
    return givenThereAreSomeSuperHeroes(numberOfSuperHeroes, false);
  }

  private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes, boolean avengers) {
    List<SuperHero> superHeroes = new LinkedList<>();
    for (int i = 0; i < numberOfSuperHeroes; i++) {
      String superHeroName = "SuperHero - " + i;
      String superHeroDescription = "Description Super Hero - " + i;
      SuperHero superHero =
          new SuperHero(superHeroName, null, avengers,
              superHeroDescription);
      superHeroes.add(superHero);
      when(repository.getByName(superHeroName)).thenReturn(superHero);
    }
    when(repository.getAll()).thenReturn(superHeroes);
    return superHeroes;
  }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}