import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# Replace multi-line ChatMessageEntity instantiation
content = re.sub(
    r'ChatMessageEntity\(\s*sessionId\s*=\s*"[^"]*",\s*role\s*=\s*(".*?"),\s*content\s*=\s*(".*?")\s*\)',
    r'ChatMessageEntity(\n                        role = \1,\n                        text = \2\n                    )',
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
