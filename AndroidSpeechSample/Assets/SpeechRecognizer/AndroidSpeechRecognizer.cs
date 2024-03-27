using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;

namespace Jplumi.AndroidSpeechRecognizer
{
    public class AndroidSpeechRecognizer : MonoBehaviour
    {
        [SerializeField]
        private string language = "en-US";

        [SerializeField]
        private int maxResults = 1;

        public static AndroidSpeechRecognizer Instance { get; private set; }

        public event Action<string> OnResults;
        public event Action<string> OnPartialResults;
        public event Action OnRecognizerReady;

        public event Action OnReadyForSpeech;
        public event Action OnBeginningOfSpeech;
        public event Action OnEndOfSpeech;

        private static AndroidJavaObject androidBridge;
        private static BridgeCallbacks callbacks;

        private Queue<Action> jobsToRun = new();

        private void AddJob(Action job)
        {
            jobsToRun.Enqueue(job);
        }

        private void Awake()
        {
            Instance = this;
            CheckPermissions();
            
            callbacks = new BridgeCallbacks("com.jplumi.unitycontinuousspeechrecognition.BridgeCallbacks");
            androidBridge = new AndroidJavaObject("com.jplumi.unitycontinuousspeechrecognition.UnitySpeechRecognizerBridge");

            Setup();
        }

        private void Update()
        {
            // This is necessary to get all events to be called on the main thread of Unity
            while (jobsToRun.Count > 0) 
                jobsToRun.Dequeue().Invoke();
        }

        private void Setup()
        {
            AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            AndroidJavaObject unityActivity = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity");
            androidBridge.Call("setupRecognizer", unityActivity, callbacks, language, maxResults);
        }

        public void StartListening()
        {
            androidBridge.Call("startListening");
        }
        public void StopListening()
        {
            androidBridge.Call("stopListening");
        }
        public void Cancel()
        {
            androidBridge.Call("cancel");
        }

        private void CheckPermissions()
        {
            Debug.Log("Check permission");
            if(!Permission.HasUserAuthorizedPermission(Permission.Microphone))
            {
                Permission.RequestUserPermission(Permission.Microphone);
                Debug.Log("Request permission");
            }
        }

        private class BridgeCallbacks : AndroidJavaProxy
        {
            public BridgeCallbacks(string javaInterface) : base(javaInterface) {}

            // Android callbacks
            void onResults(string result)
            {
                Instance.AddJob(() => Instance.OnResults?.Invoke(result.Split("\n")[0]));
            }
            void onPartialResults(string result)
            {
                Instance.AddJob(() => Instance.OnPartialResults?.Invoke(result));
            }
            void onError(string error)
            {
                // Debug.Log("[RECOGNIZER]: ON ERROR: " + error);
            }
            void onReady()
            {
                Instance.AddJob(() => Instance.OnRecognizerReady?.Invoke());
            }
            void onReadyForSpeech()
            {
                Instance.AddJob(() => Instance.OnReadyForSpeech?.Invoke());
            }
            void onBeginningOfSpeech()
            {
                Instance.AddJob(() => Instance.OnBeginningOfSpeech?.Invoke());
            }
            void onEndOfSpeech()
            {
                Instance.AddJob(() => Instance.OnEndOfSpeech?.Invoke());
            }
        }
    }

    
}