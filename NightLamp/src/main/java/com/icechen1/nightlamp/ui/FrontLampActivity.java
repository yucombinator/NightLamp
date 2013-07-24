package com.icechen1.nightlamp.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
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

public class FrontLampActivity extends FragmentActivity {
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

        //This will work also on old devices.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final TextClock clockView = (TextClock) findViewById(R.id.digitalClock);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    clockView.setTextColor((Integer)animator.getAnimatedValue());
                }

            });
        }else{
            final DigitalClock clockView = (DigitalClock) findViewById(R.id.digitalClock);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    clockView.setTextColor((Integer) animator.getAnimatedValue());
                }

            });
        }
        //The part that makes the magic work
        bkgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(getClass().getName(), "Background dim: " + BACKGROUND_DIM);

                if(BACKGROUND_DIM){
                    backgroundUp(DURATION_SHORT,false);
                }else{
                    backgroundDown(DURATION_SHORT);
                }
            }
        });

        //Set screen to full brightness
        layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1;
        getWindow().setAttributes(layoutParams);


        //Brightness Animation
        brightnessAnimation = ValueAnimator.ofObject(new FloatEvaluator(), 1, 0);
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


}
