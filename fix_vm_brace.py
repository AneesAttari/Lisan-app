with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("private fun loadDailyChallenge()\n        loadDailyVerb() {", "private fun loadDailyChallenge() {\n        loadDailyVerb()")
content = content.replace("private fun loadDailyChallenge()        loadDailyVerb() {", "private fun loadDailyChallenge() {\n        loadDailyVerb()")

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
