import re

content = open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt").read()

new_functions = """    fun startAnalyzerPronunciationPractice() {
        val input = _analyzerInput.value.trim()
        if (input.isEmpty()) {
            _analyzerError.value = "Please enter a word or sentence first.\nبراہِ کرم پہلے کوئی لفظ یا جملہ لکھیں۔"
            return
        }
        analyzerTargetForPractice = input
        _analyzerError.value = null
        _analyzerPracticeFeedback.value = null

        if (speechRecognizer == null) {
            _isAnalyzerPracticing.value = false
            _analyzerPracticeFeedback.value = "Voice practice is not available on this device right now. You can still listen and practise by repeating aloud.\nاس آلے پر آواز کی مشق ابھی دستیاب نہیں ہے۔ آپ سن کر بلند آواز سے دُہرا سکتے ہیں۔"
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Please repeat: $input")
        }

        try {
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { _isAnalyzerPracticing.value = true }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { _isAnalyzerPracticing.value = false }
                override fun onError(error: Int) {
                    _isAnalyzerPracticing.value = false
                    if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        _analyzerPracticeFeedback.value = "I could not hear your voice clearly. Please try again and speak a little closer to the microphone.\nمیں آپ کی آواز واضح طور پر نہیں سن سکا۔ براہِ کرم مائیکروفون کے قریب ہو کر دوبارہ بولیں۔"
                    } else {
                        _analyzerPracticeFeedback.value = "Your practice was recorded. Try saying the words clearly, one by one.\nآپ کی مشق ریکارڈ ہو گئی ہے۔ الفاظ کو ایک ایک کر کے واضح بولنے کی کوشش کریں۔"
                    }
                    setupSpeechRecognizer()
                }
                override fun onResults(results: Bundle?) {
                    _isAnalyzerPracticing.value = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spoken = matches[0]
                        val target = analyzerTargetForPractice
                        _analyzerPracticeFeedback.value = evaluatePronunciation(spoken, target)
                    } else {
                        _analyzerPracticeFeedback.value = "I could not hear your voice clearly. Please try again and speak a little closer to the microphone.\nمیں آپ کی آواز واضح طور پر نہیں سن سکا۔ براہِ کرم مائیکروفون کے قریب ہو کر دوبارہ بولیں۔"
                    }
                    setupSpeechRecognizer()
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isAnalyzerPracticing.value = false
            _analyzerPracticeFeedback.value = "Voice practice is not available on this device right now. You can still listen and practise by repeating aloud.\nاس آلے پر آواز کی مشق ابھی دستیاب نہیں ہے۔ آپ سن کر بلند آواز سے دُہرا سکتے ہیں۔"
            setupSpeechRecognizer()
        }
    }

    private fun evaluatePronunciation(spoken: String, target: String): String {
        val s = spoken.lowercase().trim().replace(Regex("[^a-z0-9\\\\s]"), "")
        val t = target.lowercase().trim().replace(Regex("[^a-z0-9\\\\s]"), "")
        return if (s == t || s.contains(t) || t.contains(s)) {
            "Excellent pronunciation! Your sentence was very clear.\nبہت عمدہ تلفظ! آپ کا جملہ بالکل واضح اور درست تھا۔"
        } else {
            "Good effort. Listen once more and repeat slowly.\nاچھی کوشش! ایک بار پھر سنیں اور آہستہ آہستہ دہرائیں۔"
        }
    }
"""

start_str = "    fun startAnalyzerPronunciationPractice() {"
end_str = "        setupSpeechRecognizer()\n    }\n"
start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx) + len(end_str)

updated_content = content[:start_idx] + new_functions + "\n    fun stopAnalyzerPronunciationPractice() {\n        try {\n            speechRecognizer?.stopListening()\n        } catch (_: Exception) {}\n        _isAnalyzerPracticing.value = false\n        setupSpeechRecognizer()\n    }\n" + content[end_idx:]

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(updated_content)

