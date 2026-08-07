package com.example.data.repository


import com.example.data.model.DailyVerb
import com.example.data.model.DailyVerbAssignment
import com.example.data.model.DailyVerbExample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyVerbRepository(private val dailyDaos: com.example.data.local.DailyVerbAssignmentDao) {

    private val allVerbs = listOf(
        DailyVerb(
            id = "verb_learn",
            baseForm = "learn",
            thirdPerson = "learns",
            presentParticiple = "learning",
            pastSimple = "learned / learnt",
            pastParticiple = "learned / learnt",
            urduMeaning = "سیکھنا",
            examples = listOf(
                DailyVerbExample("I learn English every day.", "میں ہر روز انگریزی سیکھتا / سیکھتی ہوں۔"),
                DailyVerbExample("She learns English every day.", "وہ ہر روز انگریزی سیکھتی ہے۔"),
                DailyVerbExample("I am learning English now.", "میں ابھی انگریزی سیکھ رہا / رہی ہوں۔"),
                DailyVerbExample("I learned English yesterday.", "میں نے کل انگریزی سیکھی۔"),
                DailyVerbExample("I have learned this lesson.", "میں نے یہ سبق سیکھ لیا ہے۔")
            ),
            usageNoteEn = "Use \"learn\" with I, you, we, and they.\nUse \"learns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “learn” استعمال ہوتا ہے۔\n“He, she, it” کے ساتھ “learns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_go",
            baseForm = "go",
            thirdPerson = "goes",
            presentParticiple = "going",
            pastSimple = "went",
            pastParticiple = "gone",
            urduMeaning = "جانا",
            examples = listOf(
                DailyVerbExample("I go to school every day.", "میں روزانہ سکول جاتا ہوں۔"),
                DailyVerbExample("He goes to the market.", "وہ بازار جاتا ہے۔"),
                DailyVerbExample("They are going home.", "وہ گھر جا رہے ہیں۔"),
                DailyVerbExample("She went to Lahore.", "وہ لاہور گئی۔"),
                DailyVerbExample("I have gone there before.", "میں پہلے وہاں جا چکا ہوں۔")
            ),
            usageNoteEn = "Use \"go\" with I, you, we, and they.\nUse \"goes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “go” استعمال ہوتا ہے۔\n“He, she, it” کے ساتھ “goes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_be",
            baseForm = "be",
            thirdPerson = "is / am / are",
            presentParticiple = "being",
            pastSimple = "was / were",
            pastParticiple = "been",
            urduMeaning = "ہونا",
            examples = listOf(
                DailyVerbExample("I am a student.", "میں ایک طالب علم ہوں۔"),
                DailyVerbExample("She is happy.", "وہ خوش ہے۔"),
                DailyVerbExample("They are my friends.", "وہ میرے دوست ہیں۔"),
                DailyVerbExample("He was at home.", "وہ گھر پر تھا۔"),
                DailyVerbExample("We have been there.", "ہم وہاں رہ چکے ہیں۔")
            ),
            usageNoteEn = "\"Be\" changes completely depending on the subject and tense.",
            usageNoteUr = "“Be” کی شکل فائل (subject) اور زمانے کے لحاظ سے مکمل تبدیل ہوتی ہے۔"
        ),
        DailyVerb(
            id = "verb_eat",
            baseForm = "eat",
            thirdPerson = "eats",
            presentParticiple = "eating",
            pastSimple = "ate",
            pastParticiple = "eaten",
            urduMeaning = "کھانا",
            examples = listOf(
                DailyVerbExample("I eat an apple.", "میں ایک سیب کھاتا ہوں۔"),
                DailyVerbExample("He eats lunch.", "وہ دوپہر کا کھانا کھاتا ہے۔"),
                DailyVerbExample("We are eating dinner.", "ہم رات کا کھانا کھا رہے ہیں۔"),
                DailyVerbExample("She ate a sandwich.", "اس نے سینڈوچ کھایا۔"),
                DailyVerbExample("I have eaten my food.", "میں اپنا کھانا کھا چکا ہوں۔")
            ),
            usageNoteEn = "Use \"eat\" with I, you, we, and they.\nUse \"eats\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “eat” استعمال ہوتا ہے۔\n“He, she, it” کے ساتھ “eats” استعمال ہوتا ہے۔"
        )
    )

    suspend fun getTodayVerbAssignment(): DailyVerbAssignment {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existing = dailyDaos.getAssignmentByDateSync("current-user-id", todayStr)
        if (existing != null) return existing

        val allAssignments = dailyDaos.getAllAssignmentsSync("current-user-id")
        val usedVerbIds = allAssignments.map { it.verbId }.toSet()
        
        var availableVerbs = allVerbs.filter { it.id !in usedVerbIds }
        if (availableVerbs.isEmpty()) {
            // Cycle completed, reset but avoid repeating yesterday's
            val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() - 86400000))
            val yesterdayAssignment = dailyDaos.getAssignmentByDateSync("current-user-id", yesterdayStr)
            availableVerbs = allVerbs.filter { it.id != yesterdayAssignment?.verbId }
            // Optionally could clear old assignments or mark cycle
        }

        val selectedVerb = availableVerbs.randomOrNull() ?: allVerbs.first()
        val newAssignment = DailyVerbAssignment(verbId = selectedVerb.id, assignedDate = todayStr)
        dailyDaos.insertAssignment(newAssignment)
        return dailyDaos.getAssignmentByDateSync("current-user-id", todayStr) ?: newAssignment
    }

    fun getVerbById(id: String): DailyVerb? {
        return allVerbs.find { it.id == id }
    }
}
