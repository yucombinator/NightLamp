package com.icechen1.nightlamp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class AppPreferenceManager {
    Context cxt;

    SharedPreferences settings;
    SharedPreferences.Editor edit;

    public AppPreferenceManager(Context cxt){
        this.cxt = cxt;
        settings = PreferenceManager.getDefaultSharedPreferences(cxt.getApplicationContext());
        edit = settings.edit();
    }

    /**
     * Creates a new instance of SettingsProvider
     * @param cxt Context
     * @return a SettingsProvider object
     */
    public static AppPreferenceManager newInstance(Context cxt){
        return new AppPreferenceManager(cxt);
    }

    /**
     * Fetches the saved fadeout_enabled value
     * @return boolean: value
     */
    public boolean getFadeOutEnabled(){
        return settings.getBoolean("fadeout_enabled", true);
    }

    /**
     * Saves the fadeout_enabled preference
     * @param value value
     */
    public void setFadeOutEnabled(boolean value){
        edit.putBoolean("fadeout_enabled", value).commit();
    }

    /**
     * Get the fade out time
      * @return time in minutes
     */
    public int getFadeOutTime(){
        return settings.getInt("fadeout_time", 30);
    }


    public void setFadeOutTime(int value){
        if(value > 0){
            edit.putInt("fadeout_time", value).commit();
        }
    }


    public boolean getFlip(){
        return settings.getBoolean("flip_enabled", true);
    }


    public void setFlip(boolean value){
        edit.putBoolean("flip_enabled", value).commit();
    }


    public int getBkgColor(){
        return settings.getInt("bkg_color", (Color.parseColor("#ffffff")));
    }


    public void setBkgColor(int value){
        edit.putInt("bkg_color", value).commit();
    }


    public int getClockColorDim(){
        return settings.getInt("clock_color_dim", (Color.RED));
    }

    public void setClockColorDim(int value){
        edit.putInt("clock_color_dim", value).commit();
    }

    public int getClockColorLight(){
        return settings.getInt("clock_color_light", (Color.LTGRAY));
    }

    public void setClockColorLight(int value){
        edit.putInt("clock_color_light", value).commit();
    }

    public boolean getLightSensorEnabled(){
        return settings.getBoolean("light_sensor_enabled", true);
    }

    public void setLightSensorEnabled(boolean value){
        edit.putBoolean("light_sensor_enabled", value).commit();
    }

    public boolean getAccelSensorEnabled(){
        return settings.getBoolean("accel_sensor_enabled", true);
    }

    public void setAccelSensorEnabled(boolean value){
        edit.putBoolean("accel_sensor_enabled", value).commit();
    }

    public boolean getMicSensorEnabled(){
        return settings.getBoolean("microphone_sensor_enabled", false);
    }

    public void setMicSensorEnabled(boolean value){
        edit.putBoolean("microphone_sensor_enabled", value).commit();
    }

    public String getLightSensorSensitivity(){
        return settings.getString("light_sensor_sens", "normal");
    }

    public void setLightSensorSensitivity(String value){
        edit.putString("light_sensor_sens", value).commit();
    }

    public String getAccelSensorSensitivity(){
        return settings.getString("accel_sensor_sens", "normal");
    }

    public void setAccelSensorSensitivity(String value){
        edit.putString("accel_sensor_sens", value).commit();
    }

    public String getMicSensorHotwords(){
        return settings.getString("hotwords", "light,night,might,like");
    }

    public void setMicSensorHotwords(String value){
        edit.putString("hotwords", value).commit();
    }

    public String getClockSize(){
        return settings.getString("ClockSize", "normal");
    }

    public void setClockSize(String value){
        edit.putString("ClockSize", value).commit();
    }

    public void reset(){
        edit.clear().commit();
    }

}
