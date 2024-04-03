# Android Continuous Speech Recognizer for Unity
This simple Unity package uses a workaround to enable continuous speech recognition on Android devices.

## How to install
You have two options:
1. Download the Unity package: You can download the Unity package [here](https://github.com/jplumi/UnityAndroidContinuousSpeechRecognition/releases/tag/v0.1.0-alpha).
2. Clone the repository: Clone this project and drag and drop the folder **SpeechRecognizer** located
at [UnitySpeechSample/Assets](https://github.com/jplumi/UnityAndroidContinuousSpeechRecognition/tree/main/UnitySpeechSample/Assets) into your Unity project.

## How to use
1. Attach SpeechRecognizer script: Attach the SpeechRecognizer script to any game object in your Unity project.
2. Set properties: In the Inspector, set the language and maxResults properties as per your requirements.
3. Use the singleton instance:
``` c#
using UnityEngine;

// import SpeechRecognizer
using Jplumi.SpeechRecognizer;

public class SpeechManager : MonoBehaviour
{
    private SpeechRecognizer recognizer;

    private void Start()
    {
        // Use the SpeechRecognizer singleton
        recognizer = SpeechRecognizer.Instance;

        // OnResults will be invoked when the whole speech is ready
        recognizer.OnResults += OnRecognizerResults;
        // OnResults will be invoked when partial results are available
        recognizer.OnPartialResults += OnRecognizerPartialResults;
    }

    public void StartRecognizer()
    {
        recognizer.StartListening();
    }
    public void StopRecognizer()
    {
        recognizer.StopListening();
    }

    public void OnRecognizerResults(string result)
    {
        Debug.Log("Recognizer result: " + result);
    }
    public void OnRecognizerPartialResults(string result)
    {
        Debug.Log("Recognizer partial result: " + result);
    }

    private void OnDisable()
    {
        // Unregister event listeners to avoid memory leaks
        recognizer.OnResults -= OnRecognizerResults;
        recognizer.OnPartialResults -= OnRecognizerPartialResults;
    }
}
```
