package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.AuthState
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class AuthManager(context: Context) {
    private val prefs = context.getSharedPreferences("lisan_auth_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val _authState = MutableStateFlow<AuthState>(loadInitialAuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    companion object {
        private const val TAG = "AuthManager"
        private const val PREF_CURRENT_USER = "current_user_v1"
        private const val PREF_USER_DATABASE = "user_database_v1"
    }

    private fun loadInitialAuthState(): AuthState {
        val rawUser = prefs.getString(PREF_CURRENT_USER, null)
        if (!rawUser.isNullOrBlank()) {
            try {
                val user = json.decodeFromString<UserAccount>(rawUser)
                return if (user.isGuest) AuthState.Guest(user) else AuthState.LoggedIn(user)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse saved user", e)
            }
        }
        val guestUser = UserAccount(
            userId = "guest_" + System.currentTimeMillis(),
            email = "",
            displayName = "Lisan Guest",
            joinedDate = "August 2026",
            preferredLanguage = "Urdu",
            themePreference = "System",
            isGuest = true
        )
        saveUserSession(guestUser)
        return AuthState.Guest(guestUser)
    }

    private fun saveUserSession(user: UserAccount) {
        try {
            val raw = json.encodeToString(user)
            prefs.edit().putString(PREF_CURRENT_USER, raw).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user session", e)
        }
    }

    fun signUp(email: String, password: String, displayName: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim().lowercase()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            onResult(false, "Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            onResult(false, "Password must be at least 6 characters long.")
            return
        }
        if (displayName.isBlank()) {
            onResult(false, "Please enter your full name.")
            return
        }

        val dbJson = prefs.getString(PREF_USER_DATABASE, null) ?: "{}"
        try {
            val userMap = json.decodeFromString<Map<String, String>>(dbJson).toMutableMap()
            if (userMap.containsKey(trimmedEmail)) {
                onResult(false, "Email is already registered. Please log in.")
                return
            }

            val newUser = UserAccount(
                userId = "user_" + System.currentTimeMillis(),
                email = trimmedEmail,
                displayName = displayName.trim(),
                joinedDate = "August 2026",
                preferredLanguage = "Urdu",
                themePreference = "System",
                isGuest = false
            )

            val userJson = json.encodeToString(newUser)
            userMap[trimmedEmail] = "$password||$userJson"
            prefs.edit().putString(PREF_USER_DATABASE, json.encodeToString(userMap.toMap())).apply()

            saveUserSession(newUser)
            _authState.value = AuthState.LoggedIn(newUser)
            onResult(true, null)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up error", e)
            onResult(false, "Sign up failed: ${e.message}")
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim().lowercase()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            onResult(false, "Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            onResult(false, "Password must be at least 6 characters long.")
            return
        }

        val dbJson = prefs.getString(PREF_USER_DATABASE, null) ?: "{}"
        try {
            val userMap = json.decodeFromString<Map<String, String>>(dbJson)
            val storedValue = userMap[trimmedEmail]
            if (storedValue == null) {
                onResult(false, "No account found with this email. Please sign up.")
                return
            }

            val parts = storedValue.split("||", limit = 2)
            if (parts.size == 2 && parts[0] == password) {
                val user = json.decodeFromString<UserAccount>(parts[1])
                saveUserSession(user)
                _authState.value = AuthState.LoggedIn(user)
                onResult(true, null)
            } else {
                onResult(false, "Incorrect password. Please try again.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            onResult(false, "Login failed: ${e.message}")
        }
    }

    fun continueAsGuest() {
        val guestUser = UserAccount(
            userId = "guest_" + System.currentTimeMillis(),
            email = "",
            displayName = "Lisan Guest Learner",
            joinedDate = "August 2026",
            preferredLanguage = "Urdu",
            themePreference = "System",
            isGuest = true
        )
        saveUserSession(guestUser)
        _authState.value = AuthState.Guest(guestUser)
    }

    fun updateProfile(
        displayName: String = "",
        username: String = "",
        profilePhotoPath: String? = null,
        preferredLanguage: String? = null,
        themePreference: String? = null,
        currentStreak: Int? = null,
        lessonsCompleted: Int? = null
    ) {
        val current = when (val state = _authState.value) {
            is AuthState.LoggedIn -> state.user
            is AuthState.Guest -> state.user
            is AuthState.LoggedOut -> return
        }

        val updated = current.copy(
            displayName = if (displayName.isNotBlank()) displayName.trim() else current.displayName,
            username = if (username.isNotBlank()) username.trim() else current.username,
            profilePhotoPath = profilePhotoPath ?: current.profilePhotoPath,
            preferredLanguage = preferredLanguage ?: current.preferredLanguage,
            themePreference = themePreference ?: current.themePreference,
            currentStreak = currentStreak ?: current.currentStreak,
            lessonsCompleted = lessonsCompleted ?: current.lessonsCompleted
        )

        saveUserSession(updated)
        if (updated.isGuest) {
            _authState.value = AuthState.Guest(updated)
        } else {
            _authState.value = AuthState.LoggedIn(updated)
            val dbJson = prefs.getString(PREF_USER_DATABASE, null) ?: "{}"
            try {
                val userMap = json.decodeFromString<Map<String, String>>(dbJson).toMutableMap()
                val stored = userMap[updated.email]
                if (stored != null) {
                    val parts = stored.split("||", limit = 2)
                    if (parts.size == 2) {
                        val newJson = json.encodeToString(updated)
                        userMap[updated.email] = "${parts[0]}||$newJson"
                        prefs.edit().putString(PREF_USER_DATABASE, json.encodeToString(userMap.toMap())).apply()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update database profile", e)
            }
        }
    }

    fun updateEmail(newEmail: String, onResult: (Boolean, String?) -> Unit) {
        val current = when (val state = _authState.value) {
            is AuthState.LoggedIn -> state.user
            else -> {
                onResult(false, "Guests cannot change email.")
                return
            }
        }
        val trimmedEmail = newEmail.trim().lowercase()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            onResult(false, "Please enter a valid email address.")
            return
        }
        if (trimmedEmail == current.email) {
            onResult(true, null)
            return
        }

        val dbJson = prefs.getString(PREF_USER_DATABASE, null) ?: "{}"
        try {
            val userMap = json.decodeFromString<Map<String, String>>(dbJson).toMutableMap()
            if (userMap.containsKey(trimmedEmail)) {
                onResult(false, "This email is already registered to another account.")
                return
            }

            val stored = userMap[current.email]
            if (stored != null) {
                val parts = stored.split("||", limit = 2)
                if (parts.size == 2) {
                    val password = parts[0]
                    val updated = current.copy(email = trimmedEmail)
                    
                    userMap.remove(current.email)
                    userMap[trimmedEmail] = "$password||${json.encodeToString(updated)}"
                    
                    prefs.edit().putString(PREF_USER_DATABASE, json.encodeToString(userMap.toMap())).apply()
                    saveUserSession(updated)
                    _authState.value = AuthState.LoggedIn(updated)
                    onResult(true, null)
                } else {
                    onResult(false, "Could not locate account details.")
                }
            } else {
                onResult(false, "Account not found.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update email error", e)
            onResult(false, "Failed to update email: ${e.message}")
        }
    }

    fun changePassword(oldPass: String, newPass: String, onResult: (Boolean, String?) -> Unit) {
        val current = when (val state = _authState.value) {
            is AuthState.LoggedIn -> state.user
            else -> {
                onResult(false, "Guests cannot change password.")
                return
            }
        }
        if (newPass.length < 6) {
            onResult(false, "New password must be at least 6 characters.")
            return
        }

        val dbJson = prefs.getString(PREF_USER_DATABASE, null) ?: "{}"
        try {
            val userMap = json.decodeFromString<Map<String, String>>(dbJson).toMutableMap()
            val stored = userMap[current.email]
            if (stored != null) {
                val parts = stored.split("||", limit = 2)
                if (parts.size == 2) {
                    if (parts[0] != oldPass) {
                        onResult(false, "Incorrect current password.")
                        return
                    }
                    userMap[current.email] = "$newPass||${parts[1]}"
                    prefs.edit().putString(PREF_USER_DATABASE, json.encodeToString(userMap.toMap())).apply()
                    onResult(true, null)
                } else {
                    onResult(false, "Could not read account info.")
                }
            } else {
                onResult(false, "Account not found.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Change password error", e)
            onResult(false, "Failed to change password: ${e.message}")
        }
    }

    fun logout() {
        prefs.edit().remove(PREF_CURRENT_USER).apply()
        val guestUser = UserAccount(
            userId = "guest_" + System.currentTimeMillis(),
            email = "",
            displayName = "Lisan Guest",
            joinedDate = "August 2026",
            preferredLanguage = "Urdu",
            themePreference = "System",
            isGuest = true
        )
        saveUserSession(guestUser)
        _authState.value = AuthState.Guest(guestUser)
    }

    fun deleteAccount() {
        val current = when (val state = _authState.value) {
            is AuthState.LoggedIn -> state.user
            else -> null
        }
        if (current != null && !current.isGuest) {
            val dbJson = prefs.getString(PREF_USER_DATABASE, null) ?: "{}"
            try {
                val userMap = json.decodeFromString<Map<String, String>>(dbJson).toMutableMap()
                userMap.remove(current.email)
                prefs.edit().putString(PREF_USER_DATABASE, json.encodeToString(userMap.toMap())).apply()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove user from database", e)
            }
        }
        logout()
    }
}
