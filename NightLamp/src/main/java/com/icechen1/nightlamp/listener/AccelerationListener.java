package com.icechen1.nightlamp.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.icechen1.nightlamp.AppPreferenceManager;

/**
 * Created by Icechen1 on 11/07/13.
 */
public class AccelerationListener extends Listener implements SensorEventListener{
    private final SensorManager mySensorManager;
    private final SensorListener mCallback;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    int SHAKE_THRESHOLD = 800; //TODO option
    private Enums.Sensitivity sensitivity;

    public AccelerationListener(Context c, SensorListener callback){
        mySensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
        mCallback = callback;
        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(LightSensor != null){

            //We have the sensor
            AppPreferenceManager pref = new AppPreferenceManager(c);

            String AccelSensorSensitivity = pref.getLightSensorSensitivity();
            if(AccelSensorSensitivity.equals("high")){
                SHAKE_THRESHOLD = 400;
            }
            if(AccelSensorSensitivity.equals("normal")){
                SHAKE_THRESHOLD = 800;
            }
            if(AccelSensorSensitivity.equals("low")){
                SHAKE_THRESHOLD = 1600;
            }

            mySensorManager.registerListener(
                    this,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

            Log.i(getClass().getName(), "Sensor.TYPE_ACCELEROMETER Listener Activated");

        }else{
            Log.i(getClass().getName(), "Sensor.TYPE_ACCELEROMETER Listener Failed");
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
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                float accel_diff = (x + y + z - last_x - last_y - last_z);

                float speed = Math.abs(accel_diff / diffTime * 10000);

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("sensor", "shake detected w/ speed: " + speed);
                    mCallback.onTrigger(Enums.Types.ACCELERATION, Enums.Action.AMBIGUOUS);
                }
                last_x = x;
                last_y = y;
                last_z = z;
    }
        }}
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
