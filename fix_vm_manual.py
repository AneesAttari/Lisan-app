with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    print(f"{i}: {line.rstrip()}")
