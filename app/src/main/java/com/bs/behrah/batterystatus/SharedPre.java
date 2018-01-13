package com.bs.behrah.batterystatus;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPre {


    SharedPreferences shp;
    private SharedPreferences.Editor editor1;

    public boolean isDarHalSharj() {
        return shp.getBoolean("darHalSharj",false);
    }

    public void setDarHalSharj(boolean darHalSharj) {
        editor1.putBoolean("darHalSharj",darHalSharj);
        editor1.apply();
    }



    public String getTemperature() {
        return shp.getString("temperature", "0");
    }

    public void setTemperature(String temperature) {
        editor1.putString("temperature", temperature);
        editor1.apply();
    }

    public float getCapacity() {
        return shp.getFloat("capacity", 0);
    }

    public void setCapacity(float capacity) {
        editor1.putFloat("capacity", capacity);
        editor1.apply();
    }

    public String getTechnology() {
        return shp.getString("technology", "نامشخص");
    }

    public void setTechnology(String technology) {
        editor1.putString("technology", technology);
        editor1.apply();
    }

    void setTPP(int second){
        editor1.putInt("tpp" , second);
        editor1.apply();
    }
    int getTPP(){
        return shp.getInt("tpp" , 0);
    }

    void setTimeToFullCharge(String s) {
        editor1.putString("ttfc" , s);
        editor1.apply();
    }

    String getTimeToFullCharge(){
        return shp.getString("ttfc" , "");
    }

    public String getCharging() {
        return shp.getString("charging", "AC");
    }

    public void setCharging(String charging) {
        editor1.putString("charging", charging);
        editor1.apply();
    }

    boolean getAlarmEnabled(){
        return shp.getBoolean("alarmEnabled", true);
    }

    void setAlarmEnabled(boolean b){
        editor1.putBoolean("alarmEnabled" , b);
        editor1.apply();
    }

    public int getHealth() {
        return shp.getInt("health", 2);
    }

    public void setHealth(int health) {
        editor1.putInt("health", health);
        editor1.apply();
    }


    public String getVoltage() {
        return shp.getString("voltage", "0");
    }

    public void setVoltage(String voltage) {
        editor1.putString("voltage", voltage);
        editor1.apply();
    }




    public int getLevel() {
        return shp.getInt("level", -1);
    }

    public void setLevel(int level) {
        editor1.putInt("level", level);
        editor1.apply();
    }


    public int getContinue_() {

        return shp.getInt("continue_", 1);
    }

    public void setContinue_(int continue_) {
        editor1.putInt("continue_", continue_);
        editor1.apply();
    }


    public int getBat_Lev_val() {

        return shp.getInt("bat_Lev_val", 80);
    }

    public void setBat_Lev_val(int bat_Lev_val) {

        editor1.putInt("bat_Lev_val", bat_Lev_val);
        editor1.apply();
    }

    public int getVol_Lev_val() {
        return shp.getInt("Vol_Lev_val",30);
    }

    public void setVol_Lev_val(int vol_Lev_val) {

        editor1.putInt("Vol_Lev_val", vol_Lev_val);
        editor1.apply();
    }

    public String getRingtone() {
        return shp.getString("ringtone","پیش فرض");
    }

    public void setRingtone(String ringtone) {
        editor1.putString("ringtone",ringtone);
        editor1.apply();
    }


    public SharedPre(Context context) {
        shp = context.getSharedPreferences("percent", 0);
        editor1 = shp.edit();
    }
}
