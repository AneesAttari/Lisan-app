sed -i '128,143c\
        val prompt = """\
            You are Lisan Grammar, an expert in English grammar teaching for Arabic-speaking learners.\
            Generate a detailed bilingual grammar lesson about the topic: "$topicEn" ($topicAr).\
            The learner'\''s proficiency level is: $level.\
            \
            Return the lesson strictly as a valid JSON object matching this schema:\
            {\
                "topicTitleEn": "String",\
                "topicTitleAr": "String",\
                "englishRule": "String",\
                "arabicConnection": "String",\
                "similarities": ["String", "String"],\
                "differences": ["String", "String"],\
                "examples": [\
                    {\
                        "englishSentence": "String",\
                        "arabicTranslation": "String",\
                        "explanationAr": "String"\
                    }\
                ],\
                "commonMistake": "String",\
                "practiceQuestion": "String",\
                "practiceAnswerHint": "String"\
            }\
        """.trimIndent()
' app/src/main/java/com/example/data/remote/RetrofitClient.kt

sed -i '288,303c\
        val prompt = """\
            You are Lisan Grammar, an expert in English grammar for Arabic-speaking learners.\
            Perform a Deep Grammatical Analysis (تحليل نحوي مقارن مفصل) on the following sentence or grammatical query:\
            "$inputSentence"\
\
            Provide your analysis entirely in proper Arabic script, focusing on:\
            1. **English Grammatical Dissection (تحليل قواعد اللغة الإنجليزية)**: Break down the sentence (Tense, Clause types, parts of speech).\
            2. **Arabic Comparative Analysis (تحليل مقارن وإعراب باللغة العربية)**: Provide the Arabic translation and connect its grammatical structure to Arabic grammar (Nahw/Sarf). Point out key similarities and differences in verb placement and prepositions.\
            3. **Common Pitfalls for Arabic Speakers**: Explain where literal translation fails.\
        """.trimIndent()
' app/src/main/java/com/example/data/remote/RetrofitClient.kt

sed -i '325,340c\
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

sed -i '387,402c\
        val prompt = """\
            You are Lisan Grammar, an expert in English grammar for Arabic-speaking learners.\
            Generate a single, high-quality, challenging multiple-choice grammar question based on the topic: "$topic".\
            The learner'\''s proficiency level is $level.\
            \
            Return strictly a valid JSON object matching this schema:\
            {\
                "tenseTitleEn": "String",\
                "tenseTitleAr": "String",\
                "question": "String",\
                "options": ["String", "String", "String", "String"],\
                "correctAnswer": "String",\
                "explanationAr": "String",\
                "commonMistake": "String"\
            }\
        """.trimIndent()
' app/src/main/java/com/example/data/remote/RetrofitClient.kt
