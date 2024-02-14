package com.jplumi.unitycontinuousspeechrecognition;

public class Bridge {
    public static void Double(int num, BridgeCallbacks callbacks) {
        callbacks.OnPartialResult(String.valueOf(num + num));
    }
}
