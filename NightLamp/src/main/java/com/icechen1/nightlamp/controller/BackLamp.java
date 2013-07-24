package com.icechen1.nightlamp.controller;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import com.icechen1.nightlamp.ui.FrontLampActivity;

import java.io.IOException;
import java.util.List;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class BackLamp extends Lamp {
    private static Camera camera;
    public static boolean LIGHT_ON;
    private int count;

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

    @Override
    public boolean openLamp(int duration, boolean lock) {
        // Open the default i.e. the first rear facing camera.
        camera = Camera.open();
        Log.d(getClass().getName(), "BackLamp opening");
        if(camera == null) {
            //   Toast.makeText(context, R.string.no_camera, Toast.LENGTH_SHORT).show();
            //   No camera or error
        } else {
            try {
                final Camera.Parameters params = camera.getParameters();

                List<String> flashModes = params.getSupportedFlashModes();

                if (flashModes == null) {
                    return false;
                } else {
                    if (count == 0) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(params);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            camera.setPreviewTexture(new SurfaceTexture(0));
                        }
                        camera.startPreview();
                    }

                    String flashMode = params.getFlashMode();

                    if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {

                        if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(params);
                        } else {
                            // Toast.makeText(this,
                            // "Flash mode (torch) not supported",Toast.LENGTH_LONG).show();

                            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

                            camera.setParameters(params);
                            try {
                                camera.autoFocus(new Camera.AutoFocusCallback() {

                                    public void onAutoFocus(boolean success, Camera camera) {
                                        count = 1;
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    }
                }


                // Set the torch flash mode
              //  Camera.Parameters param = camera.getParameters();
               // param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
              //  camera.setParameters(param);

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
