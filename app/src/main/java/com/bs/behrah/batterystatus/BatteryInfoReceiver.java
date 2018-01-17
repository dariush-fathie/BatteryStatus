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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;


public class BatteryInfoReceiver extends BroadcastReceiver {

    SharedPre shp;
    MediaPlayer mp = new MediaPlayer();
    PendingIntent pendingIntent;
    Context context;
    boolean usbCharge, acCharge;
    int level = -1;
    Notification.Builder n;
    NotificationManager notificationManager;
    boolean ifChargeNotChanged = false; // if charge not change -> do not calculate full charge time again

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
        batStatus(intent);
        int percent = shp.getBat_Lev_val();
        if (shp.isDarHalSharj()) {
            if (intent.getAction().equals(StaticValues.updateAction)) {
                ifChargeNotChanged = false;
            }
            if (intent.getAction().equals(StaticValues.dismissAction)){
                Log.e("action"  ,intent.getAction()+"");
                shp.setNotificationForcedClosed(true);
            }
            timeCalculate();
            Log.e("Continue" , shp.getContinue_() + "");
            Log.e("AlarmEnambled" , shp.getAlarmEnabled() + "");
            if (shp.getContinue_() == 1 && shp.getAlarmEnabled()) {
                if ((level == 100 || level == percent) && !getMp().isPlaying()) {
                    play();
                    alert();
                }
            }
        } else {
            shp.setTime(0); // clear time
            shp.setL(0);// clear level
            Log.e("not charging", "AAAAAAAAAAAAAA");
            ifChargeNotChanged = false;
            shp.setContinue_(1);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
    }

    synchronized private void timeCalculate() {
        long temp = System.currentTimeMillis();
        Log.e("currentTime", temp + "");
        Log.e("time", shp.getTime() + "");
        Log.e("L", shp.getL() + "");
        if (shp.getL() == 0) { // first time
            shp.setTime(temp);
            shp.setL(shp.getLevel());
            Log.e("L set .", shp.getL() + "");
            calculateFullChargeTime();
        } else if (shp.getLevel() - shp.getL() > 0) { // charge increased
            long diff = temp - shp.getTime();
            Log.e("diff ", diff + "");
            int newTpp = (int) (diff / 1000); // convert milli to second
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
            calculateFullChargeTime();
            shp.setL(shp.getLevel());
            shp.setTime(temp);
        } else if (shp.getLevel() - shp.getL() == 0 && shp.getTPP() != 0 && !ifChargeNotChanged) {
            ifChargeNotChanged = true;
            Log.e("adsfdasdf", "sXXXXXXXXXXXXx");
            calculateFullChargeTime();
        } else if (shp.getLevel() - shp.getL() < 0) { // charge decreased
            decreased();
            shp.setL(shp.getLevel());
            shp.setTime(temp);
        }

    }

    private void calculateFullChargeTime() {
        if (shp.getTPP() != 0) {
            int c = shp.getLevel();
            c = 100 - c;
            c = c * shp.getTPP();
            int m = c / 60;
            int h = 0;
            if (m > 60) {
                h = m / 60;
                m = m - h * 60;
            }
            c = c - (m * 60 + h * 3600);
            formatTime(h, m, c);
        }
    }

    void formatTime(int h, int m, int s) {
        if (h == 0) {
            if (m == 0) {
                shp.setTimeToFullCharge(String.valueOf(s + "s"));
            } else {
                if (s != 0) {
                    shp.setTimeToFullCharge(String.valueOf(m + "m:" + s + "s"));
                } else {
                    shp.setTimeToFullCharge(String.valueOf(m + "m"));
                }
            }
        } else {
            if (m == 0) {
                shp.setTimeToFullCharge(String.valueOf(h + "h:"));
            } else {
                shp.setTimeToFullCharge(String.valueOf(h + "h:" + m + "m"));
            }
        }
        updateNotification();
    }

    void updateNotification() {
        if (!shp.getNotificationForcedClose()) {
            if (n != null && notificationManager != null) {
                n.setContentText(shp.getTimeToFullCharge());
                notificationManager.notify(StaticValues.NOTIFICATION_ID, n.build());
            } else {
                showNotification();
            }
        }
    }

    void showNotification() {
        Intent i = new Intent(getContext(), MainActivity.class);
        pendingIntent = PendingIntent.getActivity(getContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        n = new Notification.Builder(context)
                .setContentTitle("شارژ کامل در ")
                .setContentText(shp.getTimeToFullCharge())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_clock)
                .setAutoCancel(false).setDeleteIntent(createOnDismissedIntent(context));

        notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(StaticValues.NOTIFICATION_ID, n.build());
    }


    private PendingIntent createOnDismissedIntent(Context context) {
        Intent intent = new Intent(context, DismissListener.class);
        return PendingIntent.getService(context, 1, intent, 0);
    }

    void decreased() {
        shp.setTimeToFullCharge(String.valueOf("-"));
    }


    public void update() {
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
