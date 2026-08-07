import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# Replace any lingering usage of _dailyIdiom inside loadDailyChallenge related assignments
# We can just remove completeDailyIdiom and toggleDailyIdiomSaved
content = re.sub(r'fun completeDailyIdiom\(\) \{.*?\n    \}', '', content, flags=re.DOTALL)
content = re.sub(r'fun toggleDailyIdiomSaved\(\) \{.*?\n    \}', '', content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
