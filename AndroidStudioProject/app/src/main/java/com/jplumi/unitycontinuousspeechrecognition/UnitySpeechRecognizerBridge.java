package com.jplumi.unitycontinuousspeechrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class UnitySpeechRecognizerBridge implements RecognitionListener {

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "SPEECH_RECON_PLUGIN";
    private boolean listening = false;

    private BridgeCallbacks callbacks;
    private Activity mainActivity;
    private String language = "en-US";
    private int maxResults;

    private AudioManager audioManager;

    // Methods called by Unity
    public void setupRecognizer(Activity mainActivity, BridgeCallbacks callbacks, String language, int maxResults){
        this.callbacks = callbacks;
        this.mainActivity = mainActivity;
        this.language = language;
        this.maxResults = maxResults;
        this.callbacks.onReady();

        setupAudioFocus();

        startRecognizer();
    }
    public void startListening() {
        listening = true;
        startRecognizer();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speech.startListening(recognizerIntent);
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
        releaseAudioFocus();
    }
    public void cancel() {
        listening = false;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speech.cancel();
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

//        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(mainActivity));

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_RESULTS, true);
    }

    private void turnOf(){
        speech.stopListening();
        speech.destroy();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        callbacks.onReadyForSpeech();
//        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        callbacks.onBeginningOfSpeech();
//        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        if(!listening){
            turnOf();
        }
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
//        Log.i(LOG_TAG, "onBufferReceived: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {
        callbacks.onEndOfSpeech();
//        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
//        Log.d(LOG_TAG, "FAILED " + errorMessage);
        speech.startListening(recognizerIntent);
        callbacks.onError(errorMessage);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        callbacks.onResults(text);
//        Log.i(LOG_TAG, "onResults="+text);
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        callbacks.onPartialResults(text);
//        Log.i(LOG_TAG, "onPartialResults="+text);
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

    private void setupAudioFocus() {
        audioManager = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        } else {
            Log.w(LOG_TAG, "Audio focus request failed. Notification sounds may play during recognition.");
        }
    }

    private void releaseAudioFocus() {
        if(audioManager != null)
        {
            audioManager.abandonAudioFocus(null);
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        }
    }
}