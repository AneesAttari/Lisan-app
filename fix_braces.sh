sed -i '163,167d' app/src/main/java/com/example/data/repository/DailyGrammarRepository.kt
sed -i 's/fun getQuestionById/fun getAllQuestions(): List<DailyChallengeQuestion> = allQuestions\n\n    fun getQuestionById/' app/src/main/java/com/example/data/repository/DailyGrammarRepository.kt
