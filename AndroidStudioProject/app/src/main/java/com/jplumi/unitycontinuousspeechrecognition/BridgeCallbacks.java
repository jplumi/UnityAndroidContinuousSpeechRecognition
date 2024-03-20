package com.jplumi.unitycontinuousspeechrecognition;

public interface BridgeCallbacks {
    void onPartialResults(String result);
    void onResults(String result);

    void onError(String error);

    void onReady();
    void onReadyForSpeech();
    void onBeginningOfSpeech();
    void onEndOfSpeech();
}
