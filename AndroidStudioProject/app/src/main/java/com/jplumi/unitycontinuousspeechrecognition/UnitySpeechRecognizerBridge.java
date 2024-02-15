package com.jplumi.unitycontinuousspeechrecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class UnitySpeechRecognizerBridge implements RecognitionListener {

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private boolean listening = false;

    private BridgeCallbacks callbacks;
    private Activity mainActivity;
    private String language = "en-US";
    private int maxResults;

    // Methods called by Unity
    public void setupRecognizer(Activity mainActivity, BridgeCallbacks callbacks, String language, int maxResults){
        this.callbacks = callbacks;
        this.mainActivity = mainActivity;
        this.language = language;
        this.maxResults = maxResults;
        this.callbacks.onReady();
        startRecognizer();
    }

    public void startListening() {
        listening = true;
        startRecognizer();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speech.startListening(recognizerIntent);
                Log.i(LOG_TAG, "START LISTENING ===========");
            }
        });
    }

    public void stopListening() {
        listening = false;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                turnOf();
            }
        });
    }

    private void startRecognizer() {
        RecognitionListener listener = this;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speech = SpeechRecognizer.createSpeechRecognizer(mainActivity);
                speech.setRecognitionListener(listener);
            }
        });

        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(mainActivity));

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_RESULTS, false);
    }

//    public static MainActivity currentActivity = null;

//    if (isChecked) {
//        listening = true;
//        start();
//        ActivityCompat.requestPermissions
//                (MainActivity.this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        REQUEST_RECORD_PERMISSION);
//    } else {
//        listening = false;
//        turnOf();
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        currentActivity = this;
//        Bridge.onReady();
//    }

    private void turnOf(){
        speech.stopListening();
        speech.destroy();
        Log.i(LOG_TAG, "STOP LISTENING ===========");
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_PERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(UnitySpeechRecognizerBridge.this, "start talk...", Toast
//                            .LENGTH_SHORT).show();
////                    speech.startListening(recognizerIntent);
//                } else {
//                    Toast.makeText(UnitySpeechRecognizerBridge.this, "Permission Denied!", Toast
//                            .LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
////        if (speech != null) {
////            speech.destroy();
////            Log.i(LOG_TAG, "destroy");
////        }
//    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
//        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        if(!listening){
            turnOf();
        }
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        speech.startListening(recognizerIntent);
        callbacks.onError(errorMessage);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onResults="+text);
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        callbacks.onPartialResults(text);
        Log.i(LOG_TAG, "onPartialResults="+text);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    private String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
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
                turnOf();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}