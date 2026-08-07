import re

with open("app/src/main/java/com/example/ui/screens/LisanDailyScreen.kt", "r") as f:
    content = f.read()

# The Daily Verb component I wanted to delete starts with `dailyVerb?.let { idiom ->` and ends just before `// --- 3. Daily Lisan Helper ---`
start_str = "        dailyVerb?.let { idiom ->\n            EditorialCard("
end_str = "        // --- 3. Daily Lisan Helper ---"

start_idx = content.find(start_str)
end_idx = content.find(end_str)

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + content[end_idx:]

with open("app/src/main/java/com/example/ui/screens/LisanDailyScreen.kt", "w") as f:
    f.write(content)
