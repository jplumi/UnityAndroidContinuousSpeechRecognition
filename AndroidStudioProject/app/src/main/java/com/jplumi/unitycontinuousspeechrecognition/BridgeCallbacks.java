package com.jplumi.unitycontinuousspeechrecognition;

public interface BridgeCallbacks {
    void onError(String error);
    void onPartialResults(String result);
    void onResults(String result);
    void onReady();
}
