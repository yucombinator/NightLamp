package com.icechen1.nightlamp.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * Created by Icechen1 on 06/07/13.
 * Took a hint from http://www.androidzeitgeist.com/2013/01/fixing-rotation-camera-picture.html
 */
public class OrientationListener extends Listener implements SensorEventListener{
    private final SensorManager mySensorManager;
    private final SensorListener mCallback;
    private Enums.Sensitivity sensitivity;
    private boolean FACING_UP;

    public OrientationListener(Context c, SensorListener callback){
        mySensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
        mCallback = callback;
        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(LightSensor != null){
            //We have the sensor

            mySensorManager.registerListener(
                    this,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

            Log.i(getClass().getName(), "Sensor.TYPE_ACCELEROMETER OrientationListener Activated");

        }else{
            Log.i(getClass().getName(), "Sensor.TYPE_ACCELEROMETER OrientationListener Failed");
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
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z_value = sensorEvent.values[2];
            if (z_value >= 0 && !FACING_UP){
                //Facing up
                mCallback.onTrigger(Enums.Types.ORIENTATION, Enums.Action.FACE_UP);
                FACING_UP=true;
            }
            else{
                if (z_value <= 0 && FACING_UP){
                    ///Facing down
                    mCallback.onTrigger(Enums.Types.ORIENTATION, Enums.Action.FACE_DOWN);
                    FACING_UP=false;
                }


            }
        }}
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
