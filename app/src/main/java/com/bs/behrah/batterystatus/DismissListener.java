package com.bs.behrah.batterystatus;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DismissListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("dismin", "onrecie");
        Intent i = new Intent();
        intent.setAction(StaticValues.dismissAction);
        context.sendBroadcast(i);
    }
}