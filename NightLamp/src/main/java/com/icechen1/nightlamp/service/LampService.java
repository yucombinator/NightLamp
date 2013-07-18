package com.icechen1.nightlamp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Icechen1 on 09/07/13.
 */
public class LampService extends Service {
    private final IBinder mBinder = (IBinder) new LocalBinder();

    public class LocalBinder extends Binder {
        LampService getService() {
            return LampService.this;
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Bundle action = intent.getExtras();
        if(action != null){
            if(action.containsKey("action")){
                String _action = action.getString("action");
            }
            //Don't shut me off until I'm done
            return START_STICKY;
        }
        return START_NOT_STICKY;

    }
}
