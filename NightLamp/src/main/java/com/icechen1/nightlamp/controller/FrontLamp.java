package com.icechen1.nightlamp.controller;

import android.hardware.Camera;
import com.icechen1.nightlamp.ui.FrontLampActivity;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class FrontLamp extends Lamp {

    private final FrontLampActivity act;

    public FrontLamp(FrontLampActivity act){
        this.act = act;
    }

    @Override
    public boolean openLamp(int duration, boolean lock) {
        try{
            act.backgroundUp(duration,lock);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    @Override
    public boolean closeLamp(int duration) {
        try{
            act.backgroundDown(duration);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
