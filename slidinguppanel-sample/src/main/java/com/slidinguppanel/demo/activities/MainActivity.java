package com.slidinguppanel.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.slidinguppanel.demo.R;

/**
 * User: josomers
 * Date: 18/07/13
 * Time: 14:21
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button customButton = (Button) findViewById(R.id.custom_buttons);
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CustomPanelButtonsActivity.class));
            }
        });

        Button customTextView = (Button) findViewById(R.id.custom_textviews);
        customTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CustomPanelTextViewsActivity.class));
            }
        });

        Button fullscreen = (Button) findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FullscreenActivity.class));
            }
        });
    }

}
