package com.bs.behrah.batterystatus;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import ru.bullyboo.view.CircleSeekBar;


@TargetApi(Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    MediaPlayer mp = new MediaPlayer();
    AudioManager audio;


    RelativeLayout tone_rl;
    TextView volt_tv, health_tv, temperature_tv, tone_tv, tv_battery_percent, more_tv;
    SwitchCompat alarmSwitch;
    BroadcastReceiver updateReceiver = null;
    SharedPre shp;
    me.itangqi.waveloadingview.WaveLoadingView waveLoadingView;
    ru.bullyboo.view.CircleSeekBar csb1, csb2;
    boolean ft = true;
    boolean START_SEEK_BAR = false;

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waveview);
        ABC();
        shp.setTimeToFullCharge("");
        init();
        Intent i = new Intent(MainActivity.this, ServiceClass.class);
        if (!isMyServiceRunning(ServiceClass.class)) {
            startService(i);
            Log.e("service" , "notRunning new instance");
        } else {
            Intent intent = new Intent();
            intent.setAction(StaticValues.updateAction);
            sendBroadcast(intent);
            Log.e("service" , "isRunning");
        }

        Log.e("flow", "1");
        startReceiveUpdate();
    }

    private void init() {
        alarmSwitch = findViewById(R.id.sc_enableAlarm);
        alarmSwitch.setOnCheckedChangeListener(this);
        alarmSwitch.setChecked(shp.getAlarmEnabled());
        volt_tv = findViewById(R.id.volt_tv);
        tv_battery_percent = findViewById(R.id.tv_battery_charge_percent);
        health_tv = findViewById(R.id.health_tv);
        temperature_tv = findViewById(R.id.temperature_tv);
        tone_tv = findViewById(R.id.tone_tv);
        tone_rl = findViewById(R.id.tone_rl);
        waveLoadingView = findViewById(R.id.waveLoadingView);

        csb1 = findViewById(R.id.csb1);
        csb2 = findViewById(R.id.csb2);
        more_tv = findViewById(R.id.more_tv);


        tone_rl.setOnClickListener(this);
        waveLoadingView.setOnClickListener(this);
        more_tv.setOnClickListener(this);

        tv_battery_percent.setText(String.valueOf(shp.getBat_Lev_val() + " %"));
        csb2.setValue(shp.getBat_Lev_val());
        csb1.setCallback(new CircleSeekBar.Callback() {
            @Override
            public void onStartScrolling(int startValue) {

                START_SEEK_BAR = true;
            }

            @Override
            public void onEndScrolling(int endValue) {
                START_SEEK_BAR = false;

                if (getMp().isPlaying()) {
                    getMp().stop();
                }
            }
        });

        csb1.setOnValueChangedListener(new CircleSeekBar.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {

                shp.setVol_Lev_val(value);

                if (START_SEEK_BAR) {
                    if (!getMp().isPlaying()) {
                        play();
                    }
                    setVolume(value);
                }
            }
        });

        csb2.setOnValueChangedListener(new CircleSeekBar.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                shp.setBat_Lev_val(csb2.getValue());
                tv_battery_percent.setText(String.valueOf("سطح هشدار :" + " % " + value));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateReceiver, new IntentFilter("com.bs.behrah.batterystatus.update"));
    }


    private void startReceiveUpdate() {
        if (updateReceiver == null) {
            updateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("flow", "3");
                    if (intent.getAction().equals("com.bs.behrah.batterystatus.update")) {
                        if (ft) {
                            ft = false;
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    waveLoadingView.setProgressValue(shp.getLevel());
                                    waveLoadingView.setCenterTitle(shp.getLevel() + " %");
                                }
                            }, 500);
                        } else {
                            waveLoadingView.setProgressValue(shp.getLevel());
                            waveLoadingView.setCenterTitle(shp.getLevel() + " %");
                        }
                        volt_tv.setText(String.valueOf(shp.getVoltage() + " v"));
                        health_tv.setText(getHealth(shp.getHealth()));
                        temperature_tv.setText(shp.getTemperature());
                        WaveLoadingViewStatus();
                    }
                }
            };
        }
    }

    private void ABC() {
        audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        shp = new SharedPre(getApplicationContext());
        shp.setContinue_(1);
    }

    private String getHealth(int i) {
        switch (i) {
            case 1:
                return "سرد";
            case 2:
                return "ضعیف";
            case 3:
                return "نرمال";
            case 4:
                return "ولتاژ بالا";
            case 5:
                return "خیلی داغ";
            case 6:
                return "معیوب";
            case 7:
                return "نامشخص";
        }
        return "";
    }

    private void ABC2() {
        csb1.setMaxValue(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 4);

        csb2.setValue(shp.getBat_Lev_val());
        csb1.setValue(shp.getVol_Lev_val());


        if (!shp.getRingtone().equals("پیش فرض")) {
            tone_tv.setText(shp.getRingtone().substring(0, shp.getRingtone().length() - 4));
        } else {
            tone_tv.setText(shp.getRingtone());
        }

    }

    @Override
    protected void onResume() {
        ABC2();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(updateReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tone_rl:
                startActivity(new Intent(MainActivity.this, RingtoneActivity.class));
                overridePendingTransition(R.anim.in, R.anim.out);
                break;
            case R.id.more_tv:

                showPopUp();
                break;
            case R.id.waveLoadingView:
                waveLoadingView.setProgressValue(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int l = shp.getLevel();
                        waveLoadingView.setProgressValue(l);
                    }
                }, 1000);
                break;
        }
    }

    private void showPopUp() {
        View view = getLayoutInflater().inflate(R.layout.more_layout, null);
        TextView tvCold, tvWeak, tvGood, tvOverVoltage, tvOverHeat, tvFailure, tvUnknown, tvCapacity, tvTech;
        tvCold = view.findViewById(R.id.tv_cold);
        tvWeak = view.findViewById(R.id.tv_weak);
        tvGood = view.findViewById(R.id.tv_good);
        tvOverVoltage = view.findViewById(R.id.volt_tv);
        tvOverHeat = view.findViewById(R.id.tv_over_heat);
        tvFailure = view.findViewById(R.id.tv_failure);
        tvUnknown = view.findViewById(R.id.tv_unknown);
        tvCapacity = view.findViewById(R.id.tv_capacity);
        tvTech = view.findViewById(R.id.tv_tech);


        switch (shp.getHealth()) {
            case 1:
                tvCold.setBackground(ContextCompat.getDrawable(this, R.drawable.health_cold));
                tvCold.setTextColor(Color.WHITE);
                break;
            case 2:
                tvWeak.setBackground(ContextCompat.getDrawable(this, R.drawable.health_weak));
                tvWeak.setTextColor(Color.WHITE);
                break;
            case 3:
                tvGood.setBackground(ContextCompat.getDrawable(this, R.drawable.health_normal));
                tvGood.setTextColor(Color.WHITE);
                break;
            case 4:
                tvOverVoltage.setBackground(ContextCompat.getDrawable(this, R.drawable.health_over_voltage));
                tvOverVoltage.setTextColor(Color.WHITE);
                break;
            case 5:
                tvOverHeat.setBackground(ContextCompat.getDrawable(this, R.drawable.health_over_heat));
                tvOverHeat.setTextColor(Color.WHITE);
                break;
            case 6:
                tvFailure.setBackground(ContextCompat.getDrawable(this, R.drawable.health_failure));
                tvFailure.setTextColor(Color.WHITE);
                break;
            case 7:
                tvUnknown.setBackground(ContextCompat.getDrawable(this, R.drawable.health_unknow));
                tvUnknown.setTextColor(Color.WHITE);
        }


        int capacity = (int) shp.getCapacity();
        int currentLevel = shp.getLevel();
        int occupiedCapacity = (currentLevel * capacity) / 100;
        tvCapacity.setText(String.valueOf(occupiedCapacity + " mAh " + " \\ " + capacity + " mAh "));

        tvTech.setText(shp.getTechnology());

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        builder.setTitle(null);
        builder.create();
        builder.show();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void WaveLoadingViewStatus() {

        if (shp.getLevel() < 16) {
            waveLoadingView.setWaveColor(getColor(R.color.lowLev));
        } else if (shp.getLevel() < 31) {
            waveLoadingView.setWaveColor(getColor(R.color.midLev));

        } else {
            waveLoadingView.setWaveColor(getColor(R.color.highLev));
        }

        if (shp.isDarHalSharj()) {
            waveLoadingView.setAmplitudeRatio(70);
        } else {
            waveLoadingView.setAmplitudeRatio(5);
        }


        if (shp.isDarHalSharj()) {
            if (shp.getLevel() != 100) {
                String t = shp.getTimeToFullCharge();
                if (t != "") {
                    waveLoadingView.setTopTitle(t);
                }
                if (t == "-") {
                    waveLoadingView.setTopTitle("در حال کاهش شارژ");
                }

            } else {
                waveLoadingView.setTopTitle("");
            }

        } else {
            waveLoadingView.setTopTitle("");
        }


        if (shp.isDarHalSharj()) {
            if (shp.getLevel() == 100) {
                waveLoadingView.setBottomTitle("شارژر را جدا کن");
            } else {
                waveLoadingView.setBottomTitle("در حال شارژ");
            }
        } else {
            if (shp.getLevel() < 16) {
                waveLoadingView.setBottomTitle("شارژر را متصل کن");
            } else {
                waveLoadingView.setBottomTitle("سطح باتری");
            }
        }

    }

    private void play() {

        if (getMp() != null) {
            getMp().release();
            setMp(null);
        }
        if (shp.getRingtone().equals("پیش فرض")) {
            setMp(MediaPlayer.create(getApplicationContext(), R.raw.sharj));
        } else {
            Uri uri = Uri.parse("/system/media/audio/ringtones/" + shp.getRingtone());
            setMp(MediaPlayer.create(getApplicationContext(), uri));
        }
        getMp().isLooping();

        getMp().start();
    }

    void setVolume(int volume) {


        if (volume > 0 && volume <= 4) {
            volume = 1;
        } else {
            volume = (int) volume / 4;
        }

        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.e("CS", b + "fgfdfgdg ");
        shp.setAlarmEnabled(b);
    }
}
