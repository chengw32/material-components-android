/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.material.catalog.fab;

import io.material.catalog.R;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import io.material.catalog.feature.DemoFragment;
import io.material.catalog.feature.DemoUtils;
import java.util.List;

/** A fragment that displays the main FAB demos for the Catalog app. */
public class FabMainDemoFragment extends DemoFragment {

  private boolean fabsShown = true;
  private AccessibilityManager manager;

  @Override
  public void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);
    manager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
  }

  @Override
  public View onCreateDemoView(
      LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
    View view =
        layoutInflater.inflate(R.layout.cat_fab_fragment, viewGroup, false /* attachToRoot */);

    ViewGroup content = view.findViewById(R.id.content);
    View.inflate(getContext(), getFabsContent(), content);

    List<FloatingActionButton> fabs = DemoUtils.findViewsWithType(view, FloatingActionButton.class);

    for (FloatingActionButton fab : fabs) {
      fab.setOnClickListener(
          v -> {
            Snackbar.make(v, R.string.cat_fab_clicked, BaseTransientBottomBar.LENGTH_SHORT).show();
          });
    }

    Button showHideFabs = view.findViewById(R.id.show_hide_fabs);
    showHideFabs.setOnClickListener(
        v -> {
          for (FloatingActionButton fab : fabs) {
            if (fabsShown) {
              fab.hide();
              showHideFabs.setText(R.string.show_fabs_label);
            } else {
              fab.show();
              showHideFabs.setText(R.string.hide_fabs_label);
            }
          }
          fabsShown = !fabsShown;
          sendAccesibilityEvent(showHideFabs, fabsShown);
        });

    Button spinFabs = view.findViewById(R.id.rotate_fabs);
    spinFabs.setOnClickListener(
        v -> {
          if (!fabsShown) {
            return;
          }

          for (FloatingActionButton fab : fabs) {
            fab.setRotation(0);
            ViewCompat.animate(fab)
                .rotation(360)
                .withLayer()
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
          }
        });

    return view;
  }

  private void sendAccesibilityEvent(View source, boolean fabsShown) {
    if (!manager.isEnabled()) {
      return;
    }

    AccessibilityEvent event = AccessibilityEvent.obtain();
    event.setEventType(AccessibilityEventCompat.TYPE_ANNOUNCEMENT);
    event.setClassName(getClass().getName());
    int eventStringResId = fabsShown ? R.string.show_fabs_event : R.string.hide_fabs_event;
    event.getText().add(getContext().getString(eventStringResId));
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      event.setSource(source);
    }

    manager.sendAccessibilityEvent(event);
  }

  @LayoutRes
  protected int getFabsContent() {
    return R.layout.mtrl_fabs;
  }
}
