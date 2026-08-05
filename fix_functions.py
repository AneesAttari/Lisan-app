with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

import re

# Fix generateBilingualLesson
old_chunk1 = """                    level = _proficiencyLevel.value.labelEn                            _activeLesson.value = generated"""
new_chunk1 = """                    level = _proficiencyLevel.value.labelEn
                )
                _activeLesson.value = generated"""
content = content.replace(old_chunk1, new_chunk1)

# Fix checkPracticeAnswer
old_chunk2 = """                    ruleContext = "English Rule: ${lesson.englishRule} | Arabic Connection: ${lesson.arabicConnection}"                            _quizFeedback.value = feedback"""
new_chunk2 = """                    ruleContext = "English Rule: ${lesson.englishRule} | Arabic Connection: ${lesson.arabicConnection}"
                )
                _quizFeedback.value = feedback"""
content = content.replace(old_chunk2, new_chunk2)

# Fix initChatWelcomeMessage if not fixed fully
# I think it's fixed

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
