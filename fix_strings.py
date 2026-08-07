import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# Replace all these problematic strings with """..."""
def replacer(match):
    # Get the contents of the string
    inner = match.group(1)
    # If it has \n, replace it with a real newline for triple quotes
    inner = inner.replace("\\n", "\n")
    return '"""' + inner + '"""'

content = re.sub(r'"([^"]*?[\u0600-\u06FF][^"]*?)"', replacer, content)

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/AnalyzerScreen.kt", "r") as f:
    content2 = f.read()

content2 = re.sub(r'"([^"]*?[\u0600-\u06FF][^"]*?)"', replacer, content2)

with open("app/src/main/java/com/example/ui/screens/AnalyzerScreen.kt", "w") as f:
    f.write(content2)

