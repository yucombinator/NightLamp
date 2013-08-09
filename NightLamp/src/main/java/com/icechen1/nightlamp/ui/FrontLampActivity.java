package com.icechen1.nightlamp.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DigitalClock;
import android.widget.FrameLayout;
import android.widget.TextClock;

import com.icechen1.nightlamp.AppPreferenceManager;
import com.icechen1.nightlamp.R;
import com.icechen1.nightlamp.controller.LampManager;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

public class FrontLampActivity extends FragmentActivity implements GestureDetector.OnGestureListener {
    private boolean BACKGROUND_DIM = false;
    private ValueAnimator colorAnimation;
    private ValueAnimator brightnessAnimation;
    private WindowManager.LayoutParams layoutParams;
    private ValueAnimator bkgAnimation;
    private LampManager lampManager;

    //Duration times for the transitions
    public static int DURATION_VERY_LONG = 60000; //1 min
    public static int DURATION_LONG = 5000;
    public static int DURATION_SHORT = 1000;
    private Handler dimTimer;
    private AppPreferenceManager pref;
    private float MAX_BRIGHTNESS = 1;
    private GestureDetector gDetector;

    //TODO add notification
    //TODO Change clock color and background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Screen keep on while activity is active
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_frontlamp);
        pref = new AppPreferenceManager(this);
        final FrameLayout bkgView = (FrameLayout) findViewById(R.id.light_container);

        //Hide the NavBar on supported devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bkgView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
        //Let's change background's color with a fade effect
        bkgAnimation= ValueAnimator.ofObject(new ArgbEvaluator(), pref.getBkgColor(), Color.BLACK);

        bkgAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bkgView.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        bkgView.setBackgroundColor(pref.getBkgColor());

        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), pref.getClockColorLight(), pref.getClockColorDim());

        int size = 0;
        //Load clock size settings
        if(pref.getClockSize().equals("big")){
            size = 120;
        }
        if(pref.getClockSize().equals("normal")){
            size = 60;
        }
        if(pref.getClockSize().equals("small")){
            size = 40;
        }
        //This will work also on old devices.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final TextClock clockView = (TextClock) findViewById(R.id.digitalClock);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    clockView.setTextColor((Integer)animator.getAnimatedValue());
                }

            });
            clockView.setTextSize(size);
        }else{
            final DigitalClock clockView = (DigitalClock) findViewById(R.id.digitalClock);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    clockView.setTextColor((Integer) animator.getAnimatedValue());
                }

            });
            clockView.setTextSize(size);
        }
        //The part that makes the magic work
/*        bkgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });*/

        //Set screen to full brightness
        layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1;
        getWindow().setAttributes(layoutParams);


        //Brightness Animation
        brightnessAnimation = ValueAnimator.ofObject(new FloatEvaluator(), MAX_BRIGHTNESS, 0.01f);
        brightnessAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //Change brightness
                //float BackLightValue = (float)arg1/100;
                layoutParams = getWindow().getAttributes();
                layoutParams.screenBrightness = (Float)animator.getAnimatedValue();
                //Log.d(getClass().getName(), (animator.getAnimatedValue()).toString());
                getWindow().setAttributes(layoutParams);
            }

        });

        //Start the sensors
        //lampManager = new LampManager(this);
        gDetector = new GestureDetector(this);
    }

    Runnable dimRunnable = new Runnable(){
        public void run() {
            backgroundDown(DURATION_VERY_LONG);
        }};

    /**
     * Lights up activity
      * @param duration
     *  @param lock
     */
    public void backgroundUp(int duration, boolean lock){
        if(BACKGROUND_DIM){
            //Change layer colors
            bkgAnimation.setDuration(duration);
            bkgAnimation.reverse();
            //Clock colors
            colorAnimation.setDuration(duration);
            colorAnimation.reverse();
            //Change brightness gradually
            brightnessAnimation.setDuration(duration);
            brightnessAnimation.reverse();

            BACKGROUND_DIM = false;

            //Lock light sensor changes
            if(lock)lampManager.locked = true;

            //Automatic dim TODO option for time
            dimTimer = new Handler();
            dimTimer.postDelayed(dimRunnable, pref.getFadeOutTime()*60000); //min to ms


        }
    }

    /**
     * Dims the activity
     * @param duration
     */
    public void backgroundDown(int duration){
        if(!BACKGROUND_DIM){
            //Change layer colors
            bkgAnimation.setDuration(duration);
            bkgAnimation.start();
            //Clock colors
            colorAnimation.setDuration(duration);
            colorAnimation.start();
            //Change brightness gradually
            brightnessAnimation.setDuration(duration);
            brightnessAnimation.start();

            BACKGROUND_DIM = true;
            //unlock light sensor changes
            lampManager.locked = false;

            if(dimTimer != null){
                //cancel the timer
                dimTimer.removeCallbacks(dimRunnable);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPause(){
        super.onPause();
        lampManager.closeEverything();
    }
    @Override
    public void onResume(){
        super.onResume();
        //Start the sensors
        lampManager = new LampManager(this);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.i(getClass().getName(), "Background dim: " + BACKGROUND_DIM);
        //Update the animator
        //Brightness Animation
        brightnessAnimation = ValueAnimator.ofObject(new FloatEvaluator(), MAX_BRIGHTNESS, 0);
        brightnessAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //Change brightness
                //float BackLightValue = (float)arg1/100;
                layoutParams = getWindow().getAttributes();
                layoutParams.screenBrightness = (Float)animator.getAnimatedValue();
                //Log.d(getClass().getName(), (animator.getAnimatedValue()).toString());
                getWindow().setAttributes(layoutParams);
            }

        });

        if(BACKGROUND_DIM){
            backgroundUp(DURATION_SHORT,false);
        }else{
            backgroundDown(DURATION_SHORT);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float v, float v2) {
        layoutParams = getWindow().getAttributes();
        Log.i(getClass().getName(), e2.getX()+ " " +e1.getX() + " " + e2.getY()+ " " +e1.getY());

        double dy = e2.getY()-e1.getY();
        double dx = e2.getX()-e1.getX();
        if(dx > 0){
            //Downward Swipe
            layoutParams.screenBrightness = (float) (MAX_BRIGHTNESS - Math.sqrt(Math.pow(dy,2)+Math.pow(dx,2))/10000);
        }else{
            //Upward Swipe
            layoutParams.screenBrightness = (float) (MAX_BRIGHTNESS + Math.sqrt(Math.pow(dy,2)+Math.pow(dx,2))/10000);
        }
        if(layoutParams.screenBrightness > 1f){
            layoutParams.screenBrightness = 1f;
        }

        if(layoutParams.screenBrightness < 0f){
            layoutParams.screenBrightness = 0.01f;
        }
        MAX_BRIGHTNESS = layoutParams.screenBrightness;
        //Log.d(getClass().getName(), (animator.getAnimatedValue()).toString());
        Log.i(getClass().getName(), "Background brightness: " + layoutParams.screenBrightness);

        getWindow().setAttributes(layoutParams);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v2) {

        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gDetector.onTouchEvent(me);
    }
}
