package com.icechen1.nightlamp.controller;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import com.icechen1.nightlamp.ui.FrontLampActivity;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class BackLamp extends Lamp {
    private static Camera camera;
    public static boolean LIGHT_ON;
    BackLamp(){

    }
    @Override
    public boolean closeLamp(int duration) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            LIGHT_ON = false;
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean openLamp(int duration, boolean lock) {
        // Open the default i.e. the first rear facing camera.
        camera = Camera.open();
        Log.d(getClass().getName(), "BackLamp opening");
        if(camera == null) {
            //   Toast.makeText(context, R.string.no_camera, Toast.LENGTH_SHORT).show();
            //   No camera or error
        } else {
            // Set the torch flash mode
            Camera.Parameters param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
                camera.setParameters(param);
                camera.startPreview();
                LIGHT_ON = true;
                Log.d(getClass().getName(), "BackLamp on");
                return true;
            } catch (Exception e) {
                //  Toast.makeText(context, R.string.no_flash, Toast.LENGTH_SHORT).show();
                // Error
            }
        }
        return false;
    }

    public boolean checkIfCameraIsUsable() {
        if(camera == null){
            Log.i(getClass().getName(), "No camera!");

            return false;
        } else return true;
    }
}
