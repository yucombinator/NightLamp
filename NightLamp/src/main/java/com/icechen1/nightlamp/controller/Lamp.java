package com.icechen1.nightlamp.controller;

/**
 * Created by Icechen1 on 09/07/13.
 */
public abstract class Lamp {
    boolean LAMP_ON = true;
    public abstract boolean openLamp(int duration, boolean lock);
    public abstract boolean closeLamp(int duration);

    public boolean toggleLamp(int duration,boolean lock){
        if (LAMP_ON){
            LAMP_ON = false;
            return closeLamp(duration);
        }else{
            LAMP_ON = true;
            return openLamp(duration,lock);
        }
    }
}
