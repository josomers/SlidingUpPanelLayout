package com.slidinguppanel.demo.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.slidinguppanel.SlidingUpPanelLayout;
import com.slidinguppanel.demo.R;

public class CustomPanelButtonsActivity extends Activity {

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private final float mDefaultRatio = 2f / 3; // two-thirds of the screen height - ratio.

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_panel_buttons);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final LinearLayout panel = (LinearLayout) findViewById(R.id.panel);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidinguppanel);
        mSlidingUpPanelLayout.setInterceptingPanelEvents(true);
        mSlidingUpPanelLayout.setDraggerView(panel, getResources().getDimensionPixelSize(R.dimen.panel_height_custom));
        mSlidingUpPanelLayout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        mSlidingUpPanelLayout.setMaxContentHeightRatio(mDefaultRatio);

        final Button btnPanel1 = (Button) findViewById(R.id.expand);
        btnPanel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.expand();
            }
        });

        final Button btnPanel2 = (Button) findViewById(R.id.collapse);
        btnPanel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.collapse();
            }
        });

        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setMaxContentHeightRatio(1.0f);
                mSlidingUpPanelLayout.expand();
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(200);
            }
        });

        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(700);
            }
        });

        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setMaxContentHeightRatio(mDefaultRatio);
                mSlidingUpPanelLayout.expand();
            }
        });

        final Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.resetContentHeight();
                mSlidingUpPanelLayout.expand();
            }
        });
    }

    private void buttonPressed(int height) {
        mSlidingUpPanelLayout.setContentHeight(height);
        mSlidingUpPanelLayout.expand();
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