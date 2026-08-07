import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

verb_audio_code_fixed = """
    fun playVerbAudio(verbId: String) {
        val verb = _dailyVerb.value ?: return
        if (currentlySpeakingText.value == verb.id) {
            ttsManager.stop()
        } else {
            val textToSpeak = "${verb.baseForm}. ${verb.thirdPerson}. ${verb.presentParticiple}. ${verb.pastSimple}. ${verb.pastParticiple}. Example: ${verb.examples.firstOrNull()?.english ?: ""}"
            ttsManager.speak(textToSpeak, verb.id)
        }
    }
"""

# Replace the broken playVerbAudio function
start = content.find("fun playVerbAudio(verbId: String) {")
if start != -1:
    end = content.find("}", content.find("}", start) + 1) + 1
    content = content[:start] + verb_audio_code_fixed.strip() + content[end:]

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
