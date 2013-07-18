package com.icechen1.nightlamp.controller;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import com.icechen1.nightlamp.AppPreferenceManager;
import com.icechen1.nightlamp.listener.*;
import com.icechen1.nightlamp.ui.FrontLampActivity;

/**
 * Created by Icechen1 on 11/07/13.
 */
public class LampManager implements Listener.SensorListener{

    private final FrontLamp frontLamp;
    private LightLevelListener lightLevelListener;
    private MicrophoneListener microphoneListener;
    private AccelerationListener accelerationListener;
    private BackLamp backLamp = null;
    private OrientationListener orientationListener = null;
    private AppPreferenceManager pref;
    public boolean locked = false;

    public LampManager(FrontLampActivity act){
        pref = new AppPreferenceManager(act);
        //Accelerometer/ Flip detector
        frontLamp = new FrontLamp(act);
        boolean flip = act.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH); //checkIfCameraFlashIsUsable
        if(flip){
            backLamp = new BackLamp();
        }

       // USE_BACK_LAMP = backLamp.checkIfCameraIsUsable();

        //Turn on the light sensor (if available, and is set to be used in the options)
        if(pref.getLightSensorEnabled())lightLevelListener = new LightLevelListener(act.getApplicationContext(),this);

        //Turn on speech recognizer
        if(pref.getMicSensorEnabled())microphoneListener = new MicrophoneListener(act,this);

        //Accelerometer/ Shake detector
        if(pref.getAccelSensorEnabled())
            accelerationListener = new AccelerationListener(act.getApplicationContext(),this);


        if(pref.getFlip() && flip){;
            orientationListener  = new OrientationListener(act.getApplicationContext(),this);
        }

    }
    public void closeEverything(){
        if(lightLevelListener != null){
            lightLevelListener.closeSensor();
        }

        if(microphoneListener != null)
            microphoneListener.stopVoiceRecognition();
        if(accelerationListener != null)
            accelerationListener.closeSensor();

        if(backLamp != null){
            backLamp.closeLamp(0);
        }
        if(orientationListener != null){
            orientationListener.closeSensor();
        }
    }

    @Override
    public void onTrigger(Enums.Types type, Enums.Action act) {
        switch(type){
            case LIGHT:
                Log.i(getClass().getName(), "Sensor.TYPE_LIGHT call back");

                if(!locked){
                    if(act == Enums.Action.UP){ //do not change if locked
                        frontLamp.openLamp(FrontLampActivity.DURATION_SHORT,false);
                    }else{
                        frontLamp.closeLamp(FrontLampActivity.DURATION_SHORT);
                    }
                }
                return;
            case MICROPHONE:
                Log.i(getClass().getName(), "Sensor.MICROPHONE call back");
                if(act == Enums.Action.AMBIGUOUS){
                    toggleLamp(FrontLampActivity.DURATION_SHORT,true);
                    return;
                }
            case ACCELERATION:
                Log.i(getClass().getName(), "Sensor.ACCELERATION call back");
                if(act == Enums.Action.AMBIGUOUS){
                    toggleLamp(FrontLampActivity.DURATION_SHORT,true);
                    return;
                }
            case ORIENTATION:
                Log.i(getClass().getName(), "Sensor.ACCEL_ORIENTATION call back");
                if(act == Enums.Action.FACE_DOWN){
                    triggerBackLamp();
                    return;
                }else{
                    triggerFrontLamp();
                    return;
                }
            default:
                return;
        }

    }

    private void triggerFrontLamp() {
        frontLamp.openLamp(2000,false);
        backLamp.closeLamp(0);
    }

    private void triggerBackLamp() {
        frontLamp.closeLamp(2000);
        backLamp.openLamp(0,false);
    }

    private void toggleLamp(int duration,boolean lock) {
        frontLamp.toggleLamp(duration,lock);
        locked = !locked; //prevent changes
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onActivated() {

    }
}
