package com.bs.behrah.batterystatus;

import android.content.Context;
import android.content.SharedPreferences;


 class SharedPre {


    private SharedPreferences shp;
    private SharedPreferences.Editor editor1;

     SharedPre(Context context) {
        shp = context.getSharedPreferences("percent", 0);
        editor1 = shp.edit();
    }

    boolean isFirstTime(){
         return shp.getBoolean("isFirstTime" , true);
    }

    void setIsFirstTime(boolean b){
        editor1.putBoolean("isFirstTime" , b);
        editor1.apply();
    }

     boolean isDarHalSharj() {
        return shp.getBoolean("darHalSharj", false);
    }

     void setDarHalSharj(boolean darHalSharj) {
        editor1.putBoolean("darHalSharj", darHalSharj);
        editor1.apply();
    }

     String getTemperature() {
        return shp.getString("temperature", "0");
    }

     void setTemperature(String temperature) {
        editor1.putString("temperature", temperature);
        editor1.apply();
    }

     float getCapacity() {
        return shp.getFloat("capacity", 0);
    }

     void setCapacity(float capacity) {
        editor1.putFloat("capacity", capacity);
        editor1.apply();
    }

     String getTechnology() {
        return shp.getString("technology", "نامشخص");
    }

     void setTechnology(String technology) {
        editor1.putString("technology", technology);
        editor1.apply();
    }

    int getTPP() {
        return shp.getInt("tpp", 0);
    }

    void setTPP(int second) {
        editor1.putInt("tpp", second);
        editor1.apply();
    }

    String getTimeToFullCharge() {
        return shp.getString("ttfc", "");
    }

    void setTimeToFullCharge(String s) {
        editor1.putString("ttfc", s);
        editor1.apply();
    }

     String getCharging() {
        return shp.getString("charging", "AC");
    }

     void setCharging(String charging) {
        editor1.putString("charging", charging);
        editor1.apply();
    }

    boolean getAlarmEnabled() {
        return shp.getBoolean("alarmEnabled", true);
    }

    void setAlarmEnabled(boolean b) {
        editor1.putBoolean("alarmEnabled", b);
        editor1.apply();
    }

     boolean getNotificationForcedClose() {
         return shp.getBoolean("notiEnabled", false);
     }

     void setNotificationForcedClosed(boolean b) {
         editor1.putBoolean("notiEnabled", b);
         editor1.apply();
     }

     int getHealth() {
        return shp.getInt("health", 2);
    }

     void setHealth(int health) {
        editor1.putInt("health", health);
        editor1.apply();
    }

     String getVoltage() {
        return shp.getString("voltage", "0");
    }

     void setVoltage(String voltage) {
        editor1.putString("voltage", voltage);
        editor1.apply();
    }

     int getLevel() {
        return shp.getInt("level", -1);
    }

     void setLevel(int level) {
        editor1.putInt("level", level);
        editor1.apply();
    }

     int getContinue_() {

        return shp.getInt("continue_", 1);
    }

     void setContinue_(int continue_) {
        editor1.putInt("continue_", continue_);
        editor1.apply();
    }

    long getTime() {
        return shp.getLong("time", 0);
    }

    void setTime(long time) {
        editor1.putLong("time", time);
        editor1.apply();
    }

    void setL(int l){
        editor1.putInt("l" , l);
        editor1.apply();
    }
    int getL(){
        return shp.getInt("l" ,0);
    }

     int getBat_Lev_val() {

        return shp.getInt("bat_Lev_val", 95);
    }

     void setBat_Lev_val(int bat_Lev_val) {

        editor1.putInt("bat_Lev_val", bat_Lev_val);
        editor1.apply();
    }

     int getVol_Lev_val() {
        return shp.getInt("Vol_Lev_val", 45);
    }

     void setVol_Lev_val(int vol_Lev_val) {

        editor1.putInt("Vol_Lev_val", vol_Lev_val);
        editor1.apply();
    }

     String getRingtone() {
        return shp.getString("ringtone", "پیش فرض");
    }

     void setRingtone(String ringtone) {
        editor1.putString("ringtone", ringtone);
        editor1.apply();
    }
}
