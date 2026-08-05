import re

with open('app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if "category = category" in line and "repository.insert" in lines[i+1]:
        new_lines.append(line)
        new_lines.append("            )\n")
        continue
    
    if "category = \"درس مقارن • Lesson\"" in line:
        new_lines.append(line)
        new_lines.append("            )\n")
        continue

    if "category = \"اختبار • Quiz\"" in line:
        new_lines.append(line)
        new_lines.append("            )\n")
        continue

    if "category = \"تقدم • Progress\"" in line:
        new_lines.append(line)
        new_lines.append("            )\n")
        continue

    if "setOf(\"verb_to_be\"," in line:
        new_lines.append(line)
        new_lines.append("    )\n")
        continue

    if "prefs.getBoolean(\"daily_completed_\"" in line:
        new_lines.append(line)
        new_lines.append("    )\n")
        continue

    if "prefs.getInt(\"daily_streak_count\"," in line:
        new_lines.append(line)
        new_lines.append("    )\n")
        continue

    if "prefs.getBoolean(\"daily_notification_enabled\"," in line:
        new_lines.append(line)
        new_lines.append("    )\n")
        continue

    if "quizAccuracyPercentage: Int {" in line:
        line = line.replace("quizAccuracyPercentage: Int {", "quizAccuracyPercentage: Int) {")
        new_lines.append(line)
        continue

    new_lines.append(line)

with open('app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt', 'w') as f:
    f.writelines(new_lines)
