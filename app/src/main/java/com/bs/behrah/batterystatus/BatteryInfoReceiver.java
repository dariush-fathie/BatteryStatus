package com.bs.behrah.batterystatus;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;


public class BatteryInfoReceiver extends BroadcastReceiver {

    SharedPre shp;
    MediaPlayer mp = new MediaPlayer();
    PendingIntent pendingIntent;
    String contentText = "";
    Context context;
    boolean usbCharge, acCharge;

    int level = -1;
    Notification n;
    NotificationManager notificationManager;
    long time = 0;
    int l = 0;

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        shp = new SharedPre(context);
        int percent = shp.getBat_Lev_val();
        batStatus(intent);


        if (shp.isDarHalSharj() && shp.getAlarmEnabled()) {

            timeCalculate();

            Intent i = new Intent(context, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            n = null;
            n = new Notification.Builder(context)
                    .setContentTitle("وضعیت باتری")
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true).build();


            notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(15223, n);

            if (shp.getContinue_() == 1) {

                if ((level == 100 || level == percent) && !getMp().isPlaying()) {
                    play();
                    alert();
                }

            }
        } else {
            l = 0;
            time = 0 ;
            shp.setContinue_(1);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
    }

    private void timeCalculate() {
        long temp = System.currentTimeMillis();
        Log.e("firsttime", temp + "");
        if (l == 0) { // first time
            l = shp.getLevel();
            time = temp;
            Log.e("firsttime2", l + "");
            calculateFullChargeTime(false, true);
        } else if (shp.getLevel() - l > 0) { // charge increased
            long diff = temp - time;
            Log.e("diff ", diff + "");
            int newTpp = (int) (diff / 1000);
            Log.e("newTpp", newTpp + "");
            int tpp = shp.getTPP();
            Log.e("tpp", tpp + "");
            if (tpp != 0) {
                int t = (tpp + newTpp) / 2;
                Log.e("tppAve", t + "");
                shp.setTPP(t);
            } else {
                shp.setTPP(newTpp);
            }
            calculateFullChargeTime(true, false);
            l = shp.getLevel();
            time = temp;
        } else if (shp.getLevel() - l < 0) { // charge decreased
            calculateFullChargeTime(false, false);
            l = shp.getLevel();
            time = temp;
        }

    }

    private void calculateFullChargeTime(boolean increased, boolean useAverageTPP) {
        if (increased || (shp.getTPP() != 0 && useAverageTPP)) {
            int c = shp.getLevel();
            c = 100 - c;
            Log.e("remain level : ", c + "");
            c = c * shp.getTPP();
            Log.e("second :", c + "");
            int m = c / 60;
            Log.e("menute", m + "");
            int h = 0;
            if (m > 60) {
                h = m / 60;
                Log.e("hour :", h + "");
                m = m - h * 60;
                Log.e("remain minute", m + "");
            }
            c = (m * 60 + h * 60) - c;
            Log.e("c remain second" , c + "");
            if (h == 0) {
                if (m == 0) {
                    shp.setTimeToFullCharge(String.valueOf(c + "s"));
                } else {
                    if (c != 0 && c > 0) {
                        shp.setTimeToFullCharge(String.valueOf(m + "m:" + c + "s"));
                    } else {
                        shp.setTimeToFullCharge(String.valueOf(m + "m"));
                    }
                }
            } else {
                if (m == 0) {
                    shp.setTimeToFullCharge(String.valueOf(h + "h:" + c + "s"));
                } else {
                    if (c != 0 && c > 0) {
                        shp.setTimeToFullCharge(String.valueOf(h + "h:" + m + "m:" + c + "s"));
                    } else {
                        shp.setTimeToFullCharge(String.valueOf(h + "h:" + m + "m"));
                    }
                }
            }

        }
        if (!increased && !useAverageTPP) {
            shp.setTimeToFullCharge(String.valueOf("-"));
        }

    }


    private void update() {
        Intent intent = new Intent();
        intent.setAction("com.bs.behrah.batterystatus.update");
        context.sendBroadcast(intent);
    }


    private void play() {

        if (getMp() != null) {
            getMp().release();
            setMp(null);
        }
        if (shp.getRingtone().equals("پیش فرض")) {
            setMp(MediaPlayer.create(getContext(), R.raw.sharj));
        } else {
            Uri uri = Uri.parse("/system/media/audio/ringtones/" + shp.getRingtone());
            setMp(MediaPlayer.create(getContext(), uri));
        }

        getMp().setLooping(true);
        getMp().start();

        int volume = shp.getVol_Lev_val();
        if (volume > 0 && volume <= 4) {
            volume = 1;
        } else {

            volume = (int) volume / 4;
        }

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    private void alert() {
        Intent i = new Intent(getContext(), Main2Activity.class);
        pendingIntent = PendingIntent.getActivity(getContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }

    private void batStatus(Intent intent) {

        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (present) {
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            float volt = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000;
            shp.setVoltage(volt + "");

            //HealthHealthHealthHealth

            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    shp.setHealth(1);
                    break;

                case BatteryManager.BATTERY_HEALTH_DEAD:
                    shp.setHealth(2);
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    shp.setHealth(3);
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    shp.setHealth(4);
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    shp.setHealth(5);
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    shp.setHealth(6);
                    break;

                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                default:
                    shp.setHealth(7);
                    break;
            }


            //charging charging charging

            shp.setDarHalSharj(true);
            switch (chargePlug) {
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    shp.setCharging("WIRELESS");
                    break;

                case BatteryManager.BATTERY_PLUGGED_USB:
                    shp.setCharging("USB");
                    break;

                case BatteryManager.BATTERY_PLUGGED_AC:
                    shp.setCharging("AC");

                    break;

                default:
                    shp.setCharging("قطع");

                    shp.setDarHalSharj(false);
                    break;
            }

            //technology technology technology
            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    shp.setTechnology(technology);
                }
            }

            // LEVEL LEVEL LEVEL LEVEL
            if (rawlevel >= 0 && scale > 0) {
                shp.setLevel(level = (rawlevel * 100) / scale);
                contentText = "سطح : " + level + " درصد";
            }

            // capacity capacity capacity capacity
            float capacity = (float) getBatteryCapacity(context);
            if (capacity > 0) {
                shp.setCapacity(capacity);
            }

            //temperature temperature temperature
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            if (temperature > 0) {
                float temp = ((float) temperature) / 10f;
                shp.setTemperature(temp + "°C");
            }

        }
        update();
    }

    public double getBatteryCapacity(Context ctx) {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager BatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
            Long chargeCounter = BatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = BatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }
        }*/
        Object mPowerProfile_ = null;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            Log.e("try1", e.toString());
        }
        try {
            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile_);
            return batteryCapacity;
        } catch (Exception e) {
            Log.e("try2", e.toString());
        }

        return 0;
    }

}
