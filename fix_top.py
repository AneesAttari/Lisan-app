import re
with open('app/src/main/java/com/example/ui/components/PictureVocabularyImageHelper.kt', 'r') as f:
    content = f.read()

content = content.replace("package com.example.ui.components", "package com.example.ui.components\n\n")
content = content.replace("import ", "\nimport ")
# Clean up duplicate newlines
content = re.sub(r'\n{3,}', '\n\n', content)

with open('app/src/main/java/com/example/ui/components/PictureVocabularyImageHelper.kt', 'w') as f:
    f.write(content)
