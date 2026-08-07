import re

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

# Fix RetrofitClient.geminiApiService to RetrofitClient.service
content = content.replace("RetrofitClient.geminiApiService", "RetrofitClient.service")

# Fix systemInstruction type
bad_sys_instr = """                    systemInstruction = com.example.data.model.SystemInstruction(
                        parts = listOf(com.example.data.model.Part(text = sysPrompt))
                    )"""
good_sys_instr = """                    systemInstruction = com.example.data.model.Content(
                        role = "system",
                        parts = listOf(com.example.data.model.Part(text = sysPrompt))
                    )"""
content = content.replace(bad_sys_instr, good_sys_instr)

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
