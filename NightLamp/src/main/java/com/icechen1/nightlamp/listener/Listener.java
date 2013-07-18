package com.icechen1.nightlamp.listener;

/**
 * Created by Icechen1 on 09/07/13.
 */
public abstract class Listener {
    public interface SensorListener{
        public void onTrigger(Enums.Types type, Enums.Action action);
        public void onError(int error);
        public void onActivated();
    }
    public abstract void setSensitivity(Enums.Sensitivity sensitivity);
    public abstract void setOnSensorListener(SensorListener listener);
    public abstract void closeSensor();
}
