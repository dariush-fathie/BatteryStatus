package com.bs.behrah.batterystatus;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;


public class Status {
    Context context ;
    SharedPre shp ;
    public Status(Context context) {
        this.context = context ;
        shp = new SharedPre(context);
    }


    private void batStatus(Intent intent) {

        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (present) {
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
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
                shp.setLevel( (rawlevel * 100) / scale);

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
