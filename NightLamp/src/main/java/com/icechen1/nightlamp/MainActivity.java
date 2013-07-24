package com.icechen1.nightlamp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.icechen1.nightlamp.controller.FrontLamp;
import com.icechen1.nightlamp.ui.FrontLampActivity;
import com.icechen1.nightlamp.ui.GeneralConfigFragment;
import com.icechen1.nightlamp.ui.SensorConfigFragment;

import static com.icechen1.nightlamp.R.*;

public class MainActivity extends SherlockFragmentActivity {
    /*
    Flip to change side
    Shake to stop
    Use camera and light sensor
    change color of light and clock
    change accel. sensitivity
    change hotword
    option for sensitivity
    inteferes with music playing
    surfaceholder solution
    add a glowring http://www.androidviews.net/2012/11/glowpadview-lockring/
    reset
    flashlight not starting if flipped too early
    clap sensor!!!
    hour format
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        //ActionBar gets initiated
        ActionBar actionbar = getSupportActionBar();
        //Tell the ActionBar we want to use Tabs.
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //initiating both tabs and set text to it.
        ActionBar.Tab OptionsTab = actionbar.newTab().setText("Options");
        ActionBar.Tab SensorsTab = actionbar.newTab().setText("Sensors");

        //create the two fragments we want to use for display content
        Fragment OptionsFragment = new GeneralConfigFragment();
        Fragment SensorsFragment = new SensorConfigFragment();

        //set the Tab listener. Now we can listen for clicks.
        OptionsTab.setTabListener(new TabsListener(OptionsFragment));
        SensorsTab.setTabListener(new TabsListener(SensorsFragment));

        //add the two tabs to the actionbar
        actionbar.addTab(OptionsTab);
        actionbar.addTab(SensorsTab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.action_start:
                startLight();
                return true;
            case id.action_reset:
                new AppPreferenceManager(this).reset();
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return false;
        }
    }

    public void startLight(){
        Intent intent = new Intent(this, FrontLampActivity.class);
        //String message =
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    class TabsListener implements ActionBar.TabListener {
        public Fragment fragment;

        public TabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
          //  Toast.makeText(StartActivity.appContext, "Reselected!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(id.fragment_container, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

    }
}
