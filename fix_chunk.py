with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

import re

old_chunk = '        """.trimIndent()\n    )\n    val idiomOutput: StateFlow<String?> = _idiomOutput.asStateFlow()'
new_chunk = '        """.trimIndent()\n        _chatHistory.value = listOf(com.example.data.remote.Content(role = "model", parts = listOf(com.example.data.remote.Part.Text(welcome))))\n    }\n\n    private val _idiomQuery = MutableStateFlow("")\n    val idiomQuery: StateFlow<String> = _idiomQuery.asStateFlow()\n    private val _idiomOutput = MutableStateFlow<String?>(null)\n    val idiomOutput: StateFlow<String?> = _idiomOutput.asStateFlow()'

content = content.replace(old_chunk, new_chunk)

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
