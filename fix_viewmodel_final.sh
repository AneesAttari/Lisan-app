sed -i 's/DailyGrammarRepository.allQuestions/dailyGrammarRepository.getAllQuestions()/g' app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt

sed -i 's/private val allQuestions/val allQuestions/g' app/src/main/java/com/example/data/repository/DailyGrammarRepository.kt
sed -i 's/class DailyGrammarRepository/class DailyGrammarRepository/g' app/src/main/java/com/example/data/repository/DailyGrammarRepository.kt

cat << 'INNER_EOF' >> app/src/main/java/com/example/data/repository/DailyGrammarRepository.kt

    fun getAllQuestions(): List<DailyChallengeQuestion> {
        return allQuestions
    }
INNER_EOF

