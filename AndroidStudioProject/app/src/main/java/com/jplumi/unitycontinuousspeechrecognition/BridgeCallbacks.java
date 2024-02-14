package com.jplumi.unitycontinuousspeechrecognition;

public interface BridgeCallbacks {
    void OnError();
    void OnPartialResult(String result);
}
