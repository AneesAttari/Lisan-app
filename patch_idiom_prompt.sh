sed -i '/val prompt = """/,/""".trimIndent()/c\
        val prompt = """\
            You are Lisan Grammar, an expert in English idioms for Arabic and Urdu speaking learners.\
            Please explain the idiom or phrase: "$query"\
\
            Format your response STRICTLY exactly like this (use markdown):\
\
            **[Simple English meaning in 1-2 sentences]**\
\
            **Urdu:** [Urdu meaning]\
            **Arabic close meaning:** [Arabic meaning or closest equivalent]\
\
            **Example:**\
            [Provide a clear, simple example sentence in English using the idiom.]\
\
            *Note: Only provide an Arabic equivalent if a real one exists. Otherwise, explain the closest meaning.*\
        """.trimIndent()
' app/src/main/java/com/example/data/remote/RetrofitClient.kt
