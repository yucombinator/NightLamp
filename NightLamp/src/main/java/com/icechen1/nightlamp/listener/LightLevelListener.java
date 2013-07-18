package com.icechen1.nightlamp.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.icechen1.nightlamp.AppPreferenceManager;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class LightLevelListener extends Listener implements SensorEventListener {

    private SensorManager mySensorManager;
    private AppPreferenceManager pref;
    private Enums.Sensitivity sensitivity = Enums.Sensitivity.NORMAL;
    private SensorListener mCallback;
    private float previousValue = 0.0f;

    public LightLevelListener(Context c, SensorListener callback){
        mySensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
        mCallback = callback;
        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(LightSensor != null){
            //We have the sensor
            pref = new AppPreferenceManager(c);
            String LightSensorSensitivity = pref.getLightSensorSensitivity();
            if(LightSensorSensitivity.equals("high")){
                sensitivity = Enums.Sensitivity.HIGH;
            }
            if(LightSensorSensitivity.equals("normal")){
                sensitivity = Enums.Sensitivity.NORMAL;
            }
            if(LightSensorSensitivity.equals("low")){
                sensitivity = Enums.Sensitivity.LOW;
            }
            mySensorManager.registerListener(
                    this,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

            Log.i(getClass().getName(), "Sensor.TYPE_LIGHT Listener Activated, sensitivity: " + sensitivity);

        }else{
            Log.i(getClass().getName(), "Sensor.TYPE_LIGHT Listener Failed");
        }
    }

    @Override
    public void setSensitivity(Enums.Sensitivity sensitivity) {
        this.sensitivity = sensitivity;
    }

    @Override
    public void setOnSensorListener(SensorListener listener) {

    }

    @Override
    public void closeSensor() {
        mySensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            float value = sensorEvent.values[0];
            if(sensitivity == Enums.Sensitivity.LOW && value > 50.0f && previousValue < 50.0f){
                mCallback.onTrigger(Enums.Types.LIGHT,Enums.Action.DOWN);
            }
            if(sensitivity == Enums.Sensitivity.LOW && value < 50.0f && previousValue > 50.0f){
                mCallback.onTrigger(Enums.Types.LIGHT,Enums.Action.UP);

            }


            if(sensitivity == Enums.Sensitivity.NORMAL && value > 20.0f && previousValue < 20.0f){
                mCallback.onTrigger(Enums.Types.LIGHT,Enums.Action.DOWN);
            }
            if(sensitivity == Enums.Sensitivity.NORMAL && value < 20.0f && previousValue > 20.0f){
                mCallback.onTrigger(Enums.Types.LIGHT,Enums.Action.UP);
            }


            if(sensitivity == Enums.Sensitivity.HIGH && value > 10.0f && previousValue < 10.0f){
                mCallback.onTrigger(Enums.Types.LIGHT,Enums.Action.DOWN);
            }
            if(sensitivity == Enums.Sensitivity.HIGH && value < 10.0f && previousValue > 10.0f){
                mCallback.onTrigger(Enums.Types.LIGHT,Enums.Action.UP);
            }
            previousValue = value;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
