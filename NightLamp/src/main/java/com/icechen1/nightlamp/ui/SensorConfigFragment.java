package com.icechen1.nightlamp.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.icechen1.nightlamp.AppPreferenceManager;
import com.icechen1.nightlamp.R;

/**
 * Created by Icechen1 on 09/07/13.
 * TODO Hide unavailable sensors
 */
public class SensorConfigFragment extends Fragment {
    private AppPreferenceManager pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor_options, container, false);
        pref = new AppPreferenceManager(getActivity());
        final String[] sensitivityArray = getResources().getStringArray(R.array.sensitivity);

        //LIGHT SENSOR
        CheckBox lightSensorLightBox = (CheckBox) view.findViewById(R.id.lightSensorCheckBox);
        final Spinner spinnerLightSensor = (Spinner) view.findViewById(R.id.lightSensorSpinner);

        lightSensorLightBox.setChecked(pref.getLightSensorEnabled());
        lightSensorLightBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                pref.setLightSensorEnabled(b);
                spinnerLightSensor.setVisibility(b?View.VISIBLE :View.GONE);
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, sensitivityArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLightSensor.setAdapter(dataAdapter);
        spinnerLightSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(spinnerLightSensor.getSelectedItem().equals(sensitivityArray[0])){
                    pref.setLightSensorSensitivity("high");
                }
                if(spinnerLightSensor.getSelectedItem().equals(sensitivityArray[1])){
                    pref.setLightSensorSensitivity("normal");
                }
                if(spinnerLightSensor.getSelectedItem().equals(sensitivityArray[2])){
                    pref.setLightSensorSensitivity("low");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        //ACCELERATIONSENSOR
        CheckBox accelSensorCheckBox = (CheckBox) view.findViewById(R.id.accelSensorCheckBox);
        final Spinner spinnerAccelSensor = (Spinner) view.findViewById(R.id.accelSensorSpinner);

        accelSensorCheckBox.setChecked(pref.getAccelSensorEnabled());
        accelSensorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                pref.setAccelSensorEnabled(b);
                spinnerAccelSensor.setVisibility(b?View.VISIBLE :View.GONE);
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccelSensor.setAdapter(dataAdapter);
        spinnerAccelSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(spinnerAccelSensor.getSelectedItem().equals(sensitivityArray[0])){
                    pref.setAccelSensorSensitivity("high");
                }
                if(spinnerAccelSensor.getSelectedItem().equals(sensitivityArray[1])){
                    pref.setAccelSensorSensitivity("normal");
                }
                if(spinnerAccelSensor.getSelectedItem().equals(sensitivityArray[2])){
                    pref.setAccelSensorSensitivity("low");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        //MICROPHONE
        CheckBox micSensorCheckBox = (CheckBox) view.findViewById(R.id.voiceCheckBox);
        final EditText micSensorEditText = (EditText) view.findViewById(R.id.voiceRecogEditText);

        micSensorCheckBox.setChecked(pref.getMicSensorEnabled());
        micSensorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                pref.setMicSensorEnabled(b);
                micSensorEditText.setVisibility(b?View.VISIBLE :View.GONE);

            }
        });

        micSensorEditText.setHint(pref.getMicSensorHotwords());
        micSensorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(""))return;
                pref.setMicSensorHotwords(editable.toString());
            }
        });
        return view;
    }

}
