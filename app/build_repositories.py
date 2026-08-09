import sys
import generate_verbs
import generate_questions

verbs = generate_verbs.verbs_data
questions = generate_questions.all_generated_questions

print(f"Loaded {len(verbs)} verbs and {len(questions)} questions.")

def escape_kt_str(s):
    if s is None:
        return '""'
    return s.replace('\\', '\\\\').replace('"', '\\"').replace('\n', '\\n')

# 1. Build DailyVerbRepository.kt
verb_kt_content = """package com.example.data.repository

import com.example.data.local.DailyVerbAssignmentDao
import com.example.data.model.DailyVerb
import com.example.data.model.DailyVerbAssignment
import com.example.data.model.DailyVerbExample
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyVerbRepository(private val dailyDaos: DailyVerbAssignmentDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val allVerbs: List<DailyVerb> = listOf(
"""

verb_items_code = []
for idx, v in enumerate(verbs):
    base, third, pres, past, pastP, urdu, examples, noteEn, noteUr = v
    ex_code_list = []
    for en, ur in examples:
        ex_code_list.append(f'DailyVerbExample("{escape_kt_str(en)}", "{escape_kt_str(ur)}")')
    ex_code_str = ",\n                ".join(ex_code_list)
    
    item_code = f"""        DailyVerb(
            id = "verb_{idx+1}_{base}",
            baseForm = "{escape_kt_str(base)}",
            thirdPerson = "{escape_kt_str(third)}",
            presentParticiple = "{escape_kt_str(pres)}",
            pastSimple = "{escape_kt_str(past)}",
            pastParticiple = "{escape_kt_str(pastP)}",
            urduMeaning = "{escape_kt_str(urdu)}",
            examples = listOf(
                {ex_code_str}
            ),
            usageNoteEn = "{escape_kt_str(noteEn)}",
            usageNoteUr = "{escape_kt_str(noteUr)}"
        )"""
    verb_items_code.append(item_code)

verb_kt_content += ",\n".join(verb_items_code)
verb_kt_content += """
    )

    fun getDayIndex(dateKey: String): Int {
        return try {
            val date = dateFormat.parse(dateKey) ?: Date()
            val epoch = dateFormat.parse("2025-01-01") ?: Date()
            val diffMs = date.time - epoch.time
            val days = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            kotlin.math.abs(days)
        } catch (e: Exception) {
            kotlin.math.abs(dateKey.hashCode())
        }
    }

    suspend fun getTodayVerbAssignment(userId: String = "user_default", dateKey: String): DailyVerbAssignment {
        val existing = dailyDaos.getAssignmentByDateSync(userId, dateKey)
        if (existing != null) return existing

        val dayIndex = getDayIndex(dateKey)
        val selectedVerb = getVerbForDay(dateKey, dayIndex)

        val newAssignment = DailyVerbAssignment(userId = userId, verbId = selectedVerb.id, assignedDate = dateKey)
        dailyDaos.insertAssignment(newAssignment)
        return dailyDaos.getAssignmentByDateSync(userId, dateKey) ?: newAssignment
    }

    fun getVerbForDay(dateKey: String, dayIndex: Int): DailyVerb {
        val baseVerb = allVerbs[dayIndex % allVerbs.size]
        val cycle = dayIndex / allVerbs.size
        if (cycle == 0) return baseVerb

        // Procedural variation for subsequent 6-month cycles
        val seed = kotlin.math.abs(dateKey.hashCode())
        val subject = when (seed % 3) {
            0 -> "They"
            1 -> "We"
            else -> "You"
        }
        val customEx = DailyVerbExample("$subject ${baseVerb.baseForm} every single day.", "$subject ہر روز ${baseVerb.urduMeaning} کرتے ہیں۔")
        return baseVerb.copy(
            id = "${baseVerb.id}_cycle_$cycle",
            examples = listOf(customEx) + baseVerb.examples.take(4)
        )
    }

    fun getVerbById(id: String): DailyVerb? {
        val cleanId = id.substringBefore("_cycle_")
        return allVerbs.find { it.id == cleanId || it.id == id }
    }
}
"""

with open("app/src/main/java/com/example/data/repository/DailyVerbRepository.kt", "w", encoding="utf-8") as f:
    f.write(verb_kt_content)

print("DailyVerbRepository.kt created successfully!")

# 2. Build DailyGrammarRepository.kt
q_kt_content = """package com.example.data.repository

import com.example.data.local.DailyChallengeAssignmentDao
import com.example.data.model.DailyChallengeAssignment
import com.example.data.model.DailyChallengeQuestion
import com.example.data.model.DailyChallengeQuestionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyGrammarRepository(private val assignmentDao: DailyChallengeAssignmentDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val allQuestionsList: List<DailyChallengeQuestion> = listOf(
"""

q_items_code = []
for idx, q in enumerate(questions):
    topic, qtype, qtext, opts, corr, acc, exp_en, exp_ur, ara_ref, ex_eg, ur_tr = q
    opts_str = ", ".join([f'"{escape_kt_str(o)}"' for o in opts]) if opts else ""
    corr_str = f"{corr}" if corr is not None else "null"
    acc_str = ", ".join([f'"{escape_kt_str(a)}"' for a in acc]) if acc else ""
    
    item_code = f"""        DailyChallengeQuestion(
            id = "dg_{idx+1}",
            topic = "{escape_kt_str(topic)}",
            type = DailyChallengeQuestionType.{qtype},
            questionText = "{escape_kt_str(qtext)}",
            options = listOf({opts_str}),
            correctOptionIndex = {corr_str},
            acceptedAnswers = listOf({acc_str}),
            explanationEn = "{escape_kt_str(exp_en)}",
            explanationUr = "{escape_kt_str(exp_ur)}",
            arabicReference = "{escape_kt_str(ara_ref)}",
            extraExample = "{escape_kt_str(ex_eg)}",
            urduTranslation = "{escape_kt_str(ur_tr)}"
        )"""
    q_items_code.append(item_code)

q_kt_content += ",\n".join(q_items_code)
q_kt_content += """
    )

    fun getDayIndex(dateKey: String): Int {
        return try {
            val date = dateFormat.parse(dateKey) ?: Date()
            val epoch = dateFormat.parse("2025-01-01") ?: Date()
            val diffMs = date.time - epoch.time
            val days = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            kotlin.math.abs(days)
        } catch (e: Exception) {
            kotlin.math.abs(dateKey.hashCode())
        }
    }

    fun getAllQuestions(): List<DailyChallengeQuestion> = allQuestionsList

    fun getQuestionById(id: String): DailyChallengeQuestion? {
        val cleanId = id.substringBefore("_cycle_")
        return allQuestionsList.find { it.id == cleanId || it.id == id }
    }

    suspend fun getDailyQuestions(userId: String = "user_default", dateKey: String): List<DailyChallengeQuestion> {
        val existing = assignmentDao.getAssignmentByDateSync(userId, dateKey)
        if (existing != null) {
            val q = getQuestionById(existing.questionId)
            if (q != null) return listOf(q)
        }

        val dayIndex = getDayIndex(dateKey)
        val selectedQuestion = getQuestionForDay(dateKey, dayIndex)

        val newAssignment = DailyChallengeAssignment(userId = userId, questionId = selectedQuestion.id, assignedDate = dateKey)
        assignmentDao.insertAssignment(newAssignment)
        return listOf(selectedQuestion)
    }

    fun getQuestionForDay(dateKey: String, dayIndex: Int): DailyChallengeQuestion {
        val baseQuestion = allQuestionsList[dayIndex % allQuestionsList.size]
        val cycle = dayIndex / allQuestionsList.size
        if (cycle == 0) return baseQuestion

        // Procedural variation for future cycles after 6 months
        return baseQuestion.copy(
            id = "${baseQuestion.id}_cycle_$cycle",
            questionText = baseQuestion.questionText + " (Day $dayIndex Practice)"
        )
    }
}
"""

with open("app/src/main/java/com/example/data/repository/DailyGrammarRepository.kt", "w", encoding="utf-8") as f:
    f.write(q_kt_content)

print("DailyGrammarRepository.kt created successfully!")
