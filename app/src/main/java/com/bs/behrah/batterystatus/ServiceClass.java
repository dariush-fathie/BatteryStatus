package com.bs.behrah.batterystatus;


import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class ServiceClass extends Service {



    BatteryInfoReceiver batteryInfoReceiver = new BatteryInfoReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onbind", "onbind");
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "onCreate");

        batteryInfoReceiver.setContext(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(batteryInfoReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
        unregisterReceiver(batteryInfoReceiver);
        stop();
    }



    private void stop(){
        if (batteryInfoReceiver.getMp() != null) {
            batteryInfoReceiver.getMp().stop();
            batteryInfoReceiver.getMp().release();
        }
    }

}
