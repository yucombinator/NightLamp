package com.icechen1.nightlamp.listener;

/**
 * Created by Icechen1 on 11/07/13.
 */
public class Enums{
    enum Sensitivity {
        LOW,NORMAL,HIGH
    }
    public enum Types {
        LIGHT,MICROPHONE,ORIENTATION,ACCELERATION
    }

    /**
     * UP = Turn light on
     * DOWN - Turn light off
     */
    public enum Action {
        UP,DOWN,AMBIGUOUS,FACE_UP,FACE_DOWN
    }



}
