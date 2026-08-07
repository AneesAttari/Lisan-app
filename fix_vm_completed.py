with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "r") as f:
    content = f.read()

completed_code = """
    private val _isDailyVerbCompleted = MutableStateFlow(false)
    val isDailyVerbCompleted: StateFlow<Boolean> = _isDailyVerbCompleted.asStateFlow()
"""
content = content.replace("val isDailyVerbSaved: StateFlow<Boolean> = _isDailyVerbSaved.asStateFlow()", "val isDailyVerbSaved: StateFlow<Boolean> = _isDailyVerbSaved.asStateFlow()\n" + completed_code)

content = content.replace("_isDailyVerbSaved.value = assignment.savedAt != null", "_isDailyVerbSaved.value = assignment.savedAt != null\n                _isDailyVerbCompleted.value = assignment.completedAt != null")

complete_func_code = """
    fun completeDailyVerb() {
        val verb = _dailyVerb.value ?: return
        viewModelScope.launch {
            val assignment = database.dailyVerbAssignmentDao().getAssignmentByDateSync("current-user-id", todayDateKey)
            if (assignment != null && assignment.completedAt == null) {
                database.dailyVerbAssignmentDao().updateAssignment(assignment.copy(completedAt = System.currentTimeMillis()))
                _isDailyVerbCompleted.value = true
            }
        }
    }
"""
content = content.replace("fun toggleDailyVerbSaved() {", complete_func_code + "\n    fun toggleDailyVerbSaved() {")

with open("app/src/main/java/com/example/ui/viewmodel/LisanViewModel.kt", "w") as f:
    f.write(content)
