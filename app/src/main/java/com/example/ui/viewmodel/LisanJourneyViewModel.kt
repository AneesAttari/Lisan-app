package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.LisanJourneyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LisanJourneyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = LisanJourneyRepository(database.lisanJourneyDao())

    val activeDefinitions: StateFlow<List<AchievementDefinitionEntity>> = repository.activeDefinitions
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allProgress: StateFlow<List<AchievementProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userStats: StateFlow<UserAchievementStatsEntity?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val tutorSchedule: StateFlow<TutorTimeScheduleEntity?> = repository.tutorSchedule
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val pendingRewards: StateFlow<List<RewardTransactionEntity>> = repository.pendingRewards
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val isLowRam = com.example.utils.DeviceCapabilityHelper.isLowRamDevice(application)

    private val _isInitializing = MutableStateFlow(false)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()

    @Volatile
    private var hasInitialized = false

    init {
        if (!isLowRam) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                repository.initializeDefinitionsIfNeeded()
                hasInitialized = true
            }
        }
    }

    fun ensureInitialized() {
        if (hasInitialized) return
        hasInitialized = true
        _isInitializing.value = true
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                repository.initializeDefinitionsIfNeeded()
            } finally {
                _isInitializing.value = false
            }
        }
    }

    fun saveTutorSchedule(
        isEnabled: Boolean,
        startTime: String,
        endTime: String,
        selectedDaysJson: String,
        remindersEnabled: Boolean
    ) {
        viewModelScope.launch {
            val existing = tutorSchedule.value ?: TutorTimeScheduleEntity()
            val updated = existing.copy(
                isEnabled = isEnabled,
                startTime = startTime,
                endTime = endTime,
                selectedDaysJson = selectedDaysJson,
                remindersEnabled = remindersEnabled,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveTutorSchedule(updated)
        }
    }

    fun claimReward(txId: String) {
        viewModelScope.launch {
            repository.claimReward(txId)
        }
    }

    fun recordEvent(
        eventType: String,
        featureSource: String,
        entityId: String? = null,
        sessionId: String? = null,
        metadataJson: String? = null,
        deduplicationKey: String
    ) {
        viewModelScope.launch {
            repository.recordEvent(eventType, featureSource, entityId, sessionId, metadataJson, deduplicationKey)
        }
    }

    fun checkDailyTutorTimeCompletion(sessionId: String, timestamp: Long) {
        viewModelScope.launch {
            repository.checkDailyTutorTimeCompletion(sessionId, timestamp)
        }
    }
}
