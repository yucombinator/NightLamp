package com.icechen1.nightlamp.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.icechen1.nightlamp.AppPreferenceManager;
import com.icechen1.nightlamp.ui.FrontLampActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Icechen1 on 09/07/13.
 * code partly from https://github.com/fcrisciani/android-speech-recognition/blob/master/VoiceRecognition/src/com/speech/fcrisciani/voicerecognition/ContinuousDictationFragment.java
 * todo offline recog
 */
public class MicrophoneListener implements RecognitionListener {
    private final ConnectivityManager connectivityManager;
    private final List<String> HotWordsList;
    Activity c;
    Listener.SensorListener mCallback;
    private SpeechRecognizer speech;
    private Timer speechTimeout;
    private AudioManager mAudioManager;

    public MicrophoneListener(Activity c, Listener.SensorListener callback){
        this.c = c;
        mCallback = callback;
        //get hotwords
        AppPreferenceManager pref = new AppPreferenceManager(c);
        String hotwords = pref.getMicSensorHotwords();
        HotWordsList = Arrays.asList(hotwords.split(","));

        mAudioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(SpeechRecognizer.isRecognitionAvailable(c)){
            startVoiceRecognitionCycle();
        }; //todo warn this before
    }

    //User says "Lights" to toggle
    //Custom hot word

    // Timer task used to reproduce the timeout input error that seems not be called on android 4.1+
    public class SilenceTimer extends TimerTask {
        @Override
        public void run() {
            onError(SpeechRecognizer.ERROR_SPEECH_TIMEOUT);
        }
    }
    // Lazy instantiation method for getting the speech recognizer
    private SpeechRecognizer getSpeechRecognizer(){
        if (speech == null) {
            speech = SpeechRecognizer.createSpeechRecognizer(c);
            speech.setRecognitionListener(this);
          //  speech.
        }

        return speech;
    }

    /**
     * Fire an intent to start the voice recognition process.
     */
    public void startVoiceRecognitionCycle()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //mute the beep sound
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getSpeechRecognizer().startListening(intent);
    }

    /**
     * Stop the voice recognition process and destroy the recognizer.
     */
    public void stopVoiceRecognition()
    {

        try{
            speechTimeout.cancel();
        }catch(NullPointerException e){
            //carry on
        }
        if (speech != null) {
            speech.destroy();

            speech = null;
        }
    }

    /* RecognitionListener interface implementation */

    @Override
    public void onReadyForSpeech(Bundle params) {
       // Log.i(getClass().getName(), "onReadyForSpeech");
        // create and schedule the input speech timeout
        speechTimeout = new Timer();
        speechTimeout.schedule(new SilenceTimer(), 3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //mute the beep sound
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        // Cancel the timeout because voice is arriving
        speechTimeout.cancel();
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
        String message;
        boolean restart = true;
        switch (error)
        {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                restart = false;
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                restart = false;
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Not recognised";
                break;
        }
       // Log.d(getClass().getName(), "onError code:" + error + " message: " + message);

        if (restart) {
            c.runOnUiThread(new Runnable() {
                public void run() {
                    getSpeechRecognizer().cancel();
                    startVoiceRecognitionCycle();
                }
            });
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onResults(Bundle results) {
        // Restart new dictation cycle
        startVoiceRecognitionCycle();
        //
        StringBuilder scores = new StringBuilder();
        for (int i = 0; i < results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES).length; i++) {
            scores.append(results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[i] + " ");
        }
        Log.d(getClass().getName(),"onResults: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) + " scores: " + scores.toString());
        // Return to the container activity dictation results
        if (results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null) {
            ArrayList<String> voice_results = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(String s: voice_results){
                //Check for hotwords
                for(String hotword:HotWordsList){
                    if(s.toLowerCase().matches("(?i).*"+hotword+"*")){
                        mCallback.onTrigger(Enums.Types.MICROPHONE, Enums.Action.AMBIGUOUS);
                        return;
                    }
                }
            }
        }

    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    //TODO implement connectivity check
    public boolean networkCheck(){

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }
}
