import re

content = open("app/src/main/java/com/example/data/remote/RetrofitClient.kt").read()

new_fallback = """    private fun generateLocalGrammarAnalysis(inputSentence: String, level: String): String {
        val cleanInput = inputSentence.trim()
        val lower = cleanInput.lowercase().removeSuffix(".")

        return when {
            lower.contains("the boy is reading a book") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                لڑکا ایک کتاب پڑھ رہا ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** The boy
                  - Article: The
                  - Main noun: boy
                - **Verb phrase:** is reading
                  - Helping verb: is
                  - Main verb: reading
                - **Object:** a book
                  - Article: a
                  - Main noun: book

                **Tense or time meaning:** Present Continuous
                یہ کام ابھی ہو رہا ہے۔

                **Simple explanation:**
                اس جملے میں فاعل پورا "The boy" ہے۔ امدادی فعل "is" کے ساتھ اصل فعل "reading" ہے۔
                جیسے آپ نے عربی میں سیکھا ہے: مبتدأ 'الولدُ' اور خبر 'يقرأ كتاباً' ہے۔

                **Common mistake to avoid:**
                صرف 'The' کو فاعل مت سمجھیں۔ 'The boy' پورا فاعل ہے۔

                **Practice Question:**
                Is the girl reading a book?
            ""${'"'}.trimIndent()

            lower.contains("my sister speaks english") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                میری بہن انگریزی بولتی ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** My sister
                  - Possessive determiner: My
                  - Main noun: sister
                - **Verb phrase:** speaks
                  - Main verb: speaks
                - **Object:** English
                  - Main noun: English

                **Tense or time meaning:** Present Simple
                یہ ایک مستقل عادت یا سچائی ہے۔

                **Simple explanation:**
                اس جملے میں فاعل "My sister" ہے۔ فعل "speaks" ہے۔

                **Practice Question:**
                Does my brother study Arabic?
            ""${'"'}.trimIndent()

            lower.contains("these students are ready") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                یہ طلبہ تیار ہیں۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** These students
                  - Determiner: These
                  - Main noun: students
                - **Verb phrase:** are
                  - Linking verb: are
                - **Complement:** ready

                **Tense or time meaning:** Present Simple
                یہ موجودہ حالت ظاہر کرتا ہے۔

                **Simple explanation:**
                "These" اور "students" مل کر فاعل ہیں۔ "ready" مفعول نہیں بلکہ تکملہ (Complement) ہے۔

                **Practice Question:**
                Are those teachers busy?
            ""${'"'}.trimIndent()

            lower.contains("the book on the table is mine") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                میز پر رکھی کتاب میری ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** The book on the table
                  - Main noun: book
                  - Extra detail: on the table
                - **Verb phrase:** is
                  - Linking verb: is
                - **Complement:** mine

                **Tense or time meaning:** Present Simple
                موجودہ ملکیت۔

                **Simple explanation:**
                "The book on the table" پورا فاعل ہے۔

                **Practice Question:**
                Is the pen on the desk yours?
            ""${'"'}.trimIndent()

            lower.contains("a teacher gave the children homework") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                ایک استاد نے بچوں کو ہوم ورک دیا۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** A teacher
                - **Verb phrase:** gave
                  - Main verb: gave
                - **Indirect Object:** the children
                - **Direct Object:** homework

                **Tense or time meaning:** Past Simple
                یہ کام ماضی میں ہوا۔

                **Simple explanation:**
                "gave" فعل کے دو مفعول ہیں: بچوں کو (indirect) اور ہوم ورک (direct)۔

                **Practice Question:**
                Did the teacher give them a test?
            ""${'"'}.trimIndent()

            lower.contains("the students have completed the exercise") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                طلبہ نے مشق مکمل کر لی ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** The students
                - **Verb phrase:** have completed
                  - Helping verb: have
                  - Main verb: completed
                - **Object:** the exercise

                **Tense or time meaning:** Present Perfect
                کام حال ہی میں مکمل ہوا ہے۔

                **Simple explanation:**
                "have" امدادی فعل ہے اور "completed" اصل فعل ہے۔

                **Practice Question:**
                Have they finished the work?
            ""${'"'}.trimIndent()

            lower.contains("does ali like tea") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                کیا علی کو چائے پسند ہے؟

                **Sentence type:** Question

                **Main parts:**
                - **Subject:** Ali
                - **Verb phrase:** Does like
                  - Helping verb: Does
                  - Main verb: like
                - **Object:** tea

                **Tense or time meaning:** Present Simple Question
                موجودہ عادت یا پسند کے بارے میں سوال۔

                **Simple explanation:**
                “Does” سوال بنانے میں مدد دیتا ہے۔ اصل فعل “like” ہے۔

                **Practice Question:**
                Does Fatima drink milk?
            ""${'"'}.trimIndent()

            lower.contains("did they finish the work") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                کیا انہوں نے کام ختم کر لیا؟

                **Sentence type:** Question

                **Main parts:**
                - **Subject:** they
                - **Verb phrase:** Did finish
                  - Helping verb: Did
                  - Main verb: finish
                - **Object:** the work

                **Tense or time meaning:** Past Simple Question
                ماضی کے بارے میں سوال۔

                **Simple explanation:**
                “Did” ماضی کا سوال بنانے میں مدد دیتا ہے۔

                **Practice Question:**
                Did she eat dinner?
            ""${'"'}.trimIndent()

            lower.contains("open the door") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                دروازہ کھولیں۔

                **Sentence type:** Command

                **Main parts:**
                - **Subject:** Understood "you" (آپ)
                - **Verb phrase:** Open
                  - Main verb: Open
                - **Object:** the door

                **Tense or time meaning:** Imperative
                حکم یا درخواست۔

                **Simple explanation:**
                حکمیہ جملوں میں فاعل "you" چھپا ہوا (understood) ہوتا ہے۔

                **Practice Question:**
                Close the window.
            ""${'"'}.trimIndent()

            lower.contains("in the morning") -> ""${'"'}
                This is a phrase, not a complete sentence.
                It gives time information.

                یہ ایک فقرہ ہے، مکمل جملہ نہیں۔
                یہ وقت کے بارے میں معلومات دیتا ہے۔

                **Main parts:**
                - Preposition: In
                - Noun phrase: the morning
            ""${'"'}.trimIndent()

            lower.contains("she is not going anywhere") -> ""${'"'}
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                وہ کہیں نہیں جا رہی ہے۔

                **Sentence type:** Negative

                **Main parts:**
                - **Subject:** She
                - **Verb phrase:** is not going
                  - Helping verb: is
                  - Negative word: not
                  - Main verb: going
                - **Adverbial:** anywhere

                **Tense or time meaning:** Present Continuous Negative
                یہ کام ابھی نہیں ہو رہا ہے۔

                **Simple explanation:**
                اس میں "is" امدادی فعل اور "going" اصل فعل ہے۔ "anywhere" مفعول نہیں، بلکہ جگہ کا اشارہ (Adverbial) ہے۔

                **Practice Question:**
                Is he going somewhere?
            ""${'"'}.trimIndent()

            else -> ""${'"'}
                **Your sentence:** $cleanInput
                
                **Urdu meaning:**
                $cleanInput کا اردو ترجمہ

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** $cleanInput
                - **Verb phrase:**
                  - Main verb: unknown
            ""${'"'}.trimIndent()
        }
    }
"""

start_str = "    private fun generateLocalGrammarAnalysis(inputSentence: String, level: String): String {"
end_str = "        }\n    }\n"
start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx) + len(end_str)

updated_content = content[:start_idx] + new_fallback + content[end_idx:]

with open("app/src/main/java/com/example/data/remote/RetrofitClient.kt", "w") as f:
    f.write(updated_content)

