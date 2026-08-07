import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# I will just remove the whole `completeDailyIdiom` function.
start = content.find("fun completeDailyIdiom() {")
end = content.find("}", content.find("}", start) + 1) + 1
# wait, finding the end of the function might be tricky with regex if it has multiple blocks.
# Let's just sed it.
