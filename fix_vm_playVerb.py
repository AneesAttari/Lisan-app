import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# Replace the broken playVerbAudio completely
clean_code = """
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

start = content.find("    fun playVerbAudio(verbId: String) {")
end = content.find("            }", start) + 14 # roughly

# Wait, let's use regex to find all of it and replace it.
content = re.sub(r'    fun playVerbAudio\(verbId: String\) \{.*?\n            \}', clean_code.strip('\n'), content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
