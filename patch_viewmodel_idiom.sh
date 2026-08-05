sed -i '/private val _idiomOutput = MutableStateFlow<String?>/,/""".trimIndent()/c\
    private val _idiomOutput = MutableStateFlow<String?>(\
        """\
        **Start a friendly conversation.**\
\
        **Urdu:** بات چیت کا خوشگوار آغاز کرنا\
        **Arabic close meaning:** بدء حديث ودي\
\
        **Example:**\
        The new teacher smiled to break the ice.\
        """.trimIndent()\
    )
' app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt
