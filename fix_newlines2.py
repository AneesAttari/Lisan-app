with open("app/src/main/java/com/example/ui/screens/AnalyzerScreen.kt", "r") as f:
    content = f.read()

content = content.replace('practice.\nآپ', 'practice.\\nآپ')
content = content.replace('ہوگی.\n\nYou', 'ہوگی.\\n\\nYou')
content = content.replace('ہوگی。\n\nYou', 'ہوگی.\\n\\nYou')
content = content.replace('ہوگی۔\n\nYou', 'ہوگی.\\n\\nYou')
content = content.replace('typing.\nآپ', 'typing.\\nآپ')

with open("app/src/main/java/com/example/ui/screens/AnalyzerScreen.kt", "w") as f:
    f.write(content)
