sed -i 's/category = category/category = category\n            )/g' app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt
sed -i 's/quizAccuracyPercentage: Int {/quizAccuracyPercentage: Int) {/g' app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt
sed -i '/prefs.getBoolean("daily_completed_\$todayDateKey", false)/c\
        prefs.getBoolean("daily_completed_\$todayDateKey", false)\
    )
' app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt
