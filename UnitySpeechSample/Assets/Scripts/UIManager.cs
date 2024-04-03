using TMPro;
using UnityEngine;
using UnityEngine.UI;
using Jplumi.SpeechRecognizer;

public class UiManager : MonoBehaviour
{
    [SerializeField] private TMP_Text textResult;
    [SerializeField] private TMP_Text textPartialResult;
    [SerializeField] private Image beginningImg;
    [SerializeField] private Image endImg;
    [SerializeField] private Button btn;

    private bool isListening = false;

    private SpeechRecognizer speechRecognizer;

    private void Start()
    {
        btn.onClick.AddListener(BtnStartStop);

        speechRecognizer = SpeechRecognizer.Instance;
        speechRecognizer.OnResults += OnResults;
        speechRecognizer.OnPartialResults += OnPartialResults;
        speechRecognizer.OnBeginningOfSpeech += OnBeginningOfSpeech;
        speechRecognizer.OnEndOfSpeech += OnEndOfSpeech;
    }

    private void OnResults(string result)
    {
        textResult.text = result;
    }

    private void OnPartialResults(string result)
    {
        textPartialResult.text = result;
    }

    private void OnBeginningOfSpeech()
    {
        beginningImg.color = Color.green;
        endImg.color = Color.red;
    }

    private void OnEndOfSpeech()
    {
        beginningImg.color = Color.red;
        endImg.color = Color.green;
    }

    public void BtnStartStop()
    {
        if(isListening)
        {
            speechRecognizer.StopListening();

            btn.GetComponentInChildren<TMP_Text>().text = "Start";
            btn.GetComponent<Image>().color = Color.green;
        }
        else
        {
            speechRecognizer.StartListening();

            btn.GetComponentInChildren<TMP_Text>().text = "Stop";
            btn.GetComponent<Image>().color = Color.red;
        }
        isListening = !isListening;
    }

    private void OnDisable()
    {
        speechRecognizer.OnPartialResults -= OnPartialResults;
        speechRecognizer.OnBeginningOfSpeech -= OnBeginningOfSpeech;
        speechRecognizer.OnEndOfSpeech -= OnEndOfSpeech;
    }
}
