using TMPro;
using UnityEngine;
using UnityEngine.UI;
using Jplumi.AndroidSpeechRecognizer;

public class UiManager : MonoBehaviour
{
    [SerializeField] private TMP_Text textResult;
    [SerializeField] private Button btn;

    private bool isListening = false;

    private AndroidSpeechRecognizer speechRecognizer;

    private void Start()
    {
        btn.onClick.AddListener(BtnStartStop);

        speechRecognizer = AndroidSpeechRecognizer.Instance;
        speechRecognizer.OnPartialResults += OnPartialResults;
    }

    private void OnPartialResults(string result)
    {
        textResult.text = result;
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
    }
}
