with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# I will replace the unescaped newlines within the string.
# Actually I will just replace the strings cleanly.
content = content.replace('aloud.\nاس', 'aloud.\\nاس')
content = content.replace('microphone.\nمیں', 'microphone.\\nمیں')
content = content.replace('one by one.\nآپ', 'one by one.\\nآپ')
content = content.replace('clear.\nبہت', 'clear.\\nبہت')
content = content.replace('slowly.\nاچھی', 'slowly.\\nاچھی')
content = content.replace('first.\nبراہِ', 'first.\\nبراہِ')

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
