package com.slidinguppanel.demo.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.slidinguppanel.SlidingUpPanelLayout;
import com.slidinguppanel.demo.R;

/**
 * User: josomers
 * Date: 18/07/13
 * Time: 14:10
 */
public class FullscreenActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_fullscreen);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidinguppanel);
        slidingUpPanelLayout.setInterceptingPanelEvents(true);
        slidingUpPanelLayout.setDraggerHeight(getResources().getDimensionPixelSize(R.dimen.panel_height_full));
        slidingUpPanelLayout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        slidingUpPanelLayout.setMaxContentHeightRatio(1.0f);

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float slideOffset) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    if (slideOffset < 0.2) {
                        if (getActionBar().isShowing()) {
                            getActionBar().hide();
                        }
                    } else {
                        if (!getActionBar().isShowing()) {
                            getActionBar().show();
                        }
                    }
                }
            }

            @Override
            public void onPanelCollapsed(View view) {
            }

            @Override
            public void onPanelExpanded(View view) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
