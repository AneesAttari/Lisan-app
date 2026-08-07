package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyVerbAssignmentDao {
    @Query("SELECT * FROM daily_verb_assignments WHERE userId = :userId AND assignedDate = :date LIMIT 1")
    suspend fun getAssignmentByDateSync(userId: String, date: String): DailyVerbAssignment?
    @Query("SELECT * FROM daily_verb_assignments WHERE userId = :userId")
    suspend fun getAllAssignmentsSync(userId: String): List<DailyVerbAssignment>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAssignment(assignment: DailyVerbAssignment): Long
    @Update
    suspend fun updateAssignment(assignment: DailyVerbAssignment)
}

@Dao
interface DailyIdiomAssignmentDao {
    @Query("SELECT * FROM daily_idiom_assignments WHERE userId = :userId AND assignedDate = :date LIMIT 1")
    fun getAssignmentByDate(userId: String, date: String): Flow<DailyIdiomAssignment?>

    @Query("SELECT * FROM daily_idiom_assignments WHERE userId = :userId AND assignedDate = :date LIMIT 1")
    suspend fun getAssignmentByDateSync(userId: String, date: String): DailyIdiomAssignment?

    @Query("SELECT * FROM daily_idiom_assignments WHERE userId = :userId")
    fun getAllAssignments(userId: String): Flow<List<DailyIdiomAssignment>>

    @Query("SELECT * FROM daily_idiom_assignments WHERE userId = :userId")
    suspend fun getAllAssignmentsSync(userId: String): List<DailyIdiomAssignment>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAssignment(assignment: DailyIdiomAssignment): Long

    @Update
    suspend fun updateAssignment(assignment: DailyIdiomAssignment)
}

@Dao
interface DailyChallengeAssignmentDao {
    @Query("SELECT * FROM daily_challenge_assignments WHERE userId = :userId AND assignedDate = :date LIMIT 1")
    fun getAssignmentByDate(userId: String, date: String): Flow<DailyChallengeAssignment?>

    @Query("SELECT * FROM daily_challenge_assignments WHERE userId = :userId AND assignedDate = :date LIMIT 1")
    suspend fun getAssignmentByDateSync(userId: String, date: String): DailyChallengeAssignment?
    @Query("SELECT * FROM daily_challenge_assignments WHERE userId = :userId")
    suspend fun getAllAssignmentsSync(userId: String): List<DailyChallengeAssignment>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAssignment(assignment: DailyChallengeAssignment): Long

    @Update
    suspend fun updateAssignment(assignment: DailyChallengeAssignment)
}

@Dao
interface RevisionItemDao {
    @Query("SELECT * FROM revision_items WHERE userId = :userId ORDER BY nextReviewAt ASC")
    fun getRevisionItems(userId: String): Flow<List<RevisionItem>>

    @Query("SELECT * FROM revision_items WHERE userId = :userId AND sourceType = :sourceType AND sourceId = :sourceId LIMIT 1")
    fun getRevisionItem(userId: String, sourceType: String, sourceId: String): Flow<RevisionItem?>

    @Query("SELECT * FROM revision_items WHERE userId = :userId AND sourceType = :sourceType AND sourceId = :sourceId LIMIT 1")
    suspend fun getRevisionItemSync(userId: String, sourceType: String, sourceId: String): RevisionItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisionItem(item: RevisionItem)

    @Update
    suspend fun updateRevisionItem(item: RevisionItem)

    @Delete
    suspend fun deleteRevisionItem(item: RevisionItem)
}

@Dao
interface AchievementUnlockDao {
    @Query("SELECT * FROM achievement_unlocks WHERE userId = :userId")
    fun getAllUnlocks(userId: String): Flow<List<AchievementUnlock>>

    @Query("SELECT * FROM achievement_unlocks WHERE userId = :userId")
    suspend fun getAllUnlocksSync(userId: String): List<AchievementUnlock>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnlock(unlock: AchievementUnlock): Long
}

@Dao
interface DailyCompletionRewardDao {
    @Query("SELECT * FROM daily_completion_rewards WHERE userId = :userId AND completionDate = :date LIMIT 1")
    fun getRewardByDate(userId: String, date: String): Flow<DailyCompletionReward?>

    @Query("SELECT * FROM daily_completion_rewards WHERE userId = :userId AND completionDate = :date LIMIT 1")
    suspend fun getRewardByDateSync(userId: String, date: String): DailyCompletionReward?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReward(reward: DailyCompletionReward): Long
}
