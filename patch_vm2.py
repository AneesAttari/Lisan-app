import re
with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

verb_audio_code = """
    fun playVerbAudio(verbId: String) {
        val verb = _dailyVerb.value ?: return
        if (currentlySpeakingText.value == verb.id) {
            tutorAudioManager.stop()
            _currentlySpeakingText.value = null
        } else {
            val textToSpeak = "${verb.baseForm}. ${verb.thirdPerson}. ${verb.presentParticiple}. ${verb.pastSimple}. ${verb.pastParticiple}. Example: ${verb.examples.firstOrNull()?.english ?: ""}"
            _currentlySpeakingText.value = verb.id
            tutorAudioManager.speak(textToSpeak) {
                _currentlySpeakingText.value = null
            }
        }
    }
"""
content = content.replace("fun toggleDailyVerbSaved() {", verb_audio_code + "\n    fun toggleDailyVerbSaved() {")

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
