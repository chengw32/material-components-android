/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.material.catalog.feature;

import io.material.catalog.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import androidx.annotation.Nullable;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;

/** Utils for feature demos. */
public abstract class FeatureDemoUtils {

  static final String ARG_TRANSITION_NAME = "ARG_TRANSITION_NAME";

  private static final int MAIN_ACTIVITY_FRAGMENT_CONTAINER_ID = R.id.container;
  private static final String DEFAULT_CATALOG_DEMO = "default_catalog_demo";

  public static void startFragment(FragmentActivity activity, Fragment fragment, String tag) {
    startFragmentInternal(activity, fragment, tag, null, null, null);
  }

  public static void startFragment(
      FragmentActivity activity,
      Fragment fragment,
      String tag,
      View sharedElement,
      String sharedElementName) {
    startFragmentInternal(activity, fragment, tag, sharedElement, sharedElementName, null);
  }

  public static void startFragment(
      FragmentActivity activity,
      Fragment fragment,
      String tag,
      View sharedElement,
      String sharedElementName,
      ContainerTransformConfiguration containerTransformConfiguration) {
    startFragmentInternal(
        activity, fragment, tag, sharedElement, sharedElementName, containerTransformConfiguration);
  }

  public static void startFragmentInternal(
      FragmentActivity activity,
      Fragment fragment,
      String tag,
      @Nullable View sharedElement,
      @Nullable String sharedElementName,
      @Nullable ContainerTransformConfiguration containerTransformConfiguration) {
    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP
        && sharedElement != null
        && sharedElementName != null) {
      Fragment currentFragment = getCurrentFragment(activity);
      Hold hold = new Hold();
      // Add root view as target for the Hold so that the entire view hierarchy is held in place as
      // one instead of each child view individually. Helps keep shadows during the transition.
      hold.addTarget(currentFragment.getView());
      currentFragment.setExitTransition(hold);

      MaterialContainerTransform transform = new MaterialContainerTransform();
      if (containerTransformConfiguration != null
          && containerTransformConfiguration.isArcMotionEnabled()) {
        transform.setPathMotion(new MaterialArcMotion());
      }
      transform.setContainerColor(MaterialColors.getColor(sharedElement, R.attr.colorSurface));
      transform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
      fragment.setSharedElementEnterTransition(transform);
      transaction.addSharedElement(sharedElement, sharedElementName);

      if (fragment.getArguments() == null) {
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, sharedElementName);
        fragment.setArguments(args);
      } else {
        fragment.getArguments().putString(ARG_TRANSITION_NAME, sharedElementName);
      }
    } else {
      transaction.setCustomAnimations(
          R.anim.abc_grow_fade_in_from_bottom,
          R.anim.abc_fade_out,
          R.anim.abc_fade_in,
          R.anim.abc_shrink_fade_out_from_bottom);
    }

    transaction
        .replace(MAIN_ACTIVITY_FRAGMENT_CONTAINER_ID, fragment, tag)
        .addToBackStack(null /* name */)
        .commit();
  }

  public static Fragment getCurrentFragment(FragmentActivity activity) {
    return activity
        .getSupportFragmentManager()
        .findFragmentById(MAIN_ACTIVITY_FRAGMENT_CONTAINER_ID);
  }

  public static String getDefaultDemo(Context context) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    return preferences.getString(DEFAULT_CATALOG_DEMO, "");
  }

  public static void saveDefaultDemo(Context context, String val) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(DEFAULT_CATALOG_DEMO, val);
    editor.apply();
  }
}
