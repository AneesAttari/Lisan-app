package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAccount(
    val userId: String = "guest_user_" + System.currentTimeMillis(),
    val email: String = "",
    val displayName: String = "Lisan Guest Learner",
    val username: String = "",
    val profilePhotoPath: String? = null,
    val currentStreak: Int = 3,
    val lessonsCompleted: Int = 4,
    val joinedDate: String = "August 2026",
    val preferredLanguage: String = "Urdu", // English, Arabic, Urdu
    val themePreference: String = "System", // Light, Dark, System
    val isGuest: Boolean = true
)

sealed class AuthState {
    data class LoggedIn(val user: UserAccount) : AuthState()
    data class Guest(val user: UserAccount) : AuthState()
    object LoggedOut : AuthState()
}
