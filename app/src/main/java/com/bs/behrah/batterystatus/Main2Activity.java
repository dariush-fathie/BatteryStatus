package com.bs.behrah.batterystatus;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class Main2Activity extends AppCompatActivity {

    SharedPre shp;
    Button stop_tv;
    me.itangqi.waveloadingview.WaveLoadingView waveLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        this.setFinishOnTouchOutside(false);
        this.setTitle(null);
        shp = new SharedPre(getApplicationContext());
        waveLoadingView = findViewById(R.id.bat_progress_wv);

        stop_tv = findViewById(R.id.stop_tv);

        stop_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main2Activity.this, ServiceClass.class);
                stopService(i);
                shp.setContinue_(0);
                finish();
            }
        });
        waveLoadingView.setProgressValue(shp.getLevel());
        waveLoadingView.setCenterTitle(shp.getLevel() + " %");
        WaveLoadingViewStatus();

    }



    private void WaveLoadingViewStatus() {

        if (shp.getLevel() < 16) {

            waveLoadingView.setWaveColor(ContextCompat.getColor(getApplicationContext(), R.color.lowLev));
        } else if (shp.getLevel() < 31) {
            waveLoadingView.setWaveColor(ContextCompat.getColor(getApplicationContext(), R.color.midLev));

        } else {
            waveLoadingView.setWaveColor(ContextCompat.getColor(getApplicationContext(), R.color.highLev));
        }

        if (shp.isDarHalSharj()) {
            waveLoadingView.setAmplitudeRatio(50);
        } else {
            waveLoadingView.setAmplitudeRatio(5);
        }

    }

    @Override
    protected void onPause() {

        Intent i = new Intent(Main2Activity.this, ServiceClass.class);
        startService(i);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

    }


}
