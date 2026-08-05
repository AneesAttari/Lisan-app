sed -i '/""".trimIndent()/,/val idiomOutput: StateFlow<String?>/c\
        """.trimIndent()\
    )\
    val idiomOutput: StateFlow<String?> = _idiomOutput.asStateFlow()
' app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt
