import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("tutorAudioManager.stop()", "ttsManager.stop()")
content = content.replace("tutorAudioManager.speak(textToSpeak) {", "ttsManager.speak(textToSpeak, verb.id)")

# ChatMessageEntity fixes
# I might have failed replacing them properly last time
content = re.sub(r'ChatMessageEntity\(sessionId = "daily_helper_\$todayDateKey", role = "(.*?)", content = (.*?)\)', r'ChatMessageEntity(role = "\1", text = \2)', content)

# _currentlySpeakingText -> currentlySpeakingText doesn't exist? Wait, it's `currentlySpeakingText` as a StateFlow in LisanViewModel?
# Let's check what state was used for currentlySpeaking
