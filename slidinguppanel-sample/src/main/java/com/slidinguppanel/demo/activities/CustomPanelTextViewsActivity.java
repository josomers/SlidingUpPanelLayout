package com.slidinguppanel.demo.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slidinguppanel.SlidingUpPanelLayout;
import com.slidinguppanel.demo.R;

public class CustomPanelTextViewsActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_panel_textviews);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final View panel = findViewById(R.id.panel);

        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidinguppanel);
        slidingUpPanelLayout.setInterceptingPanelEvents(false);
        slidingUpPanelLayout.setDraggerView(panel, getResources().getDimensionPixelSize(R.dimen.panel_height_custom));
        slidingUpPanelLayout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow), 5);
        slidingUpPanelLayout.setMaxContentHeightRatio(1.0f);

        final TextView btnPanel1 = (TextView) findViewById(R.id.expand);
        btnPanel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.expand();
            }
        });

        final TextView btnPanel2 = (TextView) findViewById(R.id.collapse);
        btnPanel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.collapse();
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