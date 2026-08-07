package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.AuthState
import com.example.data.repository.AuthManager
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    val authState: StateFlow<AuthState> = authManager.authState

    fun signUp(email: String, pass: String, name: String, onResult: (Boolean, String?) -> Unit) {
        authManager.signUp(email, pass, name, onResult)
    }

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        authManager.login(email, pass, onResult)
    }

    fun continueAsGuest() {
        authManager.continueAsGuest()
    }

    fun updateProfile(
        name: String = "",
        username: String = "",
        profilePhotoPath: String? = null,
        lang: String? = null,
        theme: String? = null,
        currentStreak: Int? = null,
        lessonsCompleted: Int? = null
    ) {
        authManager.updateProfile(name, username, profilePhotoPath, lang, theme, currentStreak, lessonsCompleted)
    }

    fun updateEmail(newEmail: String, onResult: (Boolean, String?) -> Unit) {
        authManager.updateEmail(newEmail, onResult)
    }

    fun changePassword(oldPass: String, newPass: String, onResult: (Boolean, String?) -> Unit) {
        authManager.changePassword(oldPass, newPass, onResult)
    }

    fun logout() {
        authManager.logout()
    }

    fun deleteAccount() {
        authManager.deleteAccount()
    }
}
