package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import coil.compose.rememberAsyncImagePainter
import com.example.ui.theme.*
import com.example.data.model.AuthState
import com.example.data.model.UserAccount
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun AuthDialog(
    viewModel: LisanViewModel,
    onDismiss: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EditorialTheme.colors.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUp) "Create Lisan Account" else "Welcome Back to Lisan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTheme.colors.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialTheme.colors.textSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isSignUp) "Sign up to sync your English & Arabic learning progress and chat history across devices." else "Log in to access your saved notes, past conversations, and personalized learner profile.",
                    fontSize = 13.sp,
                    color = EditorialTheme.colors.textSecondary,
                    lineHeight = 18.sp
                )

                if (isSignUp) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it; errorMessage = null },
                        label = { Text("Full Name (پورا نام)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = { Text("Email Address") },
                    placeholder = { Text("name@example.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password (Min 6 characters)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFC62828),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        if (isSignUp) {
                            viewModel.signUp(email, password, displayName) { success, err ->
                                isLoading = false
                                if (success) {
                                    onDismiss()
                                } else {
                                    errorMessage = err
                                }
                            }
                        } else {
                            viewModel.login(email, password) { success, err ->
                                isLoading = false
                                if (success) {
                                    onDismiss()
                                } else {
                                    errorMessage = err
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialTheme.colors.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            text = if (isSignUp) "Create Account & Sync" else "Log In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUp) "Already have an account?" else "Don't have an account?",
                        fontSize = 13.sp,
                        color = EditorialTheme.colors.textSecondary
                    )
                    TextButton(onClick = { isSignUp = !isSignUp; errorMessage = null }) {
                        Text(
                            text = if (isSignUp) "Log In" else "Sign Up",
                            fontWeight = FontWeight.Bold,
                            color = EditorialTheme.colors.accentPurple,
                            fontSize = 13.sp
                        )
                    }
                }

                HorizontalDivider(color = EditorialTheme.colors.border)

                OutlinedButton(
                    onClick = {
                        viewModel.continueAsGuest()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialTheme.colors.accentPurple)
                ) {
                    Icon(Icons.Default.PersonOutline, contentDescription = null, tint = EditorialTheme.colors.accentPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue as Guest (مہمان جاری رکھیں)",
                        color = EditorialTheme.colors.accentPurple,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun UserAvatar(
    photoPath: String?,
    displayName: String,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    modifier: Modifier = Modifier
) {
    if (!photoPath.isNullOrBlank()) {
        val file = File(photoPath)
        if (file.exists()) {
            Image(
                painter = rememberAsyncImagePainter(model = file),
                contentDescription = "User Avatar",
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            return
        }
    }
    // Fallback to initials
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(EditorialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        val initials = displayName.trim().split("\\s+".toRegex()).let { parts ->
            if (parts.size >= 2) {
                "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
            } else if (displayName.isNotBlank()) {
                displayName.trim().take(2).uppercase()
            } else {
                "LS"
            }
        }
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

fun saveBitmapToInternalStorage(context: android.content.Context, bitmap: Bitmap): String? {
    return try {
        val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.png")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        file.absolutePath
    } catch (e: Exception) {
        android.util.Log.e("AuthProfile", "Failed to save bitmap", e)
        null
    }
}

fun saveUriToInternalStorage(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.png")
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        android.util.Log.e("AuthProfile", "Failed to save Uri", e)
        null
    }
}

@Composable
fun ProfileDialog(
    viewModel: LisanViewModel,
    user: UserAccount,
    onDismiss: () -> Unit,
    onOpenAuth: () -> Unit
) {
    val context = LocalContext.current
    val learnerProfile by viewModel.learnerProfile.collectAsState()
    
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user.displayName) }
    var editUsername by remember { mutableStateOf(user.username) }
    
    var showEmailForm by remember { mutableStateOf(false) }
    var editEmail by remember { mutableStateOf(user.email) }
    
    var showPasswordForm by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    
    var profileMessage by remember { mutableStateOf<String?>(null) }
    var isMessageError by remember { mutableStateOf(false) }
    
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val localPath = saveBitmapToInternalStorage(context, bitmap)
            if (localPath != null) {
                viewModel.updateProfile(profilePhotoPath = localPath)
                profileMessage = "Profile photo updated!"
                isMessageError = false
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val localPath = saveUriToInternalStorage(context, uri)
            if (localPath != null) {
                viewModel.updateProfile(profilePhotoPath = localPath)
                profileMessage = "Profile photo updated!"
                isMessageError = false
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out of your Lisan account?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account & Data?") },
            text = { Text("This will permanently delete your account and clear your learning history and profile data.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteAccount()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EditorialTheme.colors.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "User Profile & Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTheme.colors.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialTheme.colors.textSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Circular profile picture center layout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatar(
                        photoPath = user.profilePhotoPath,
                        displayName = user.displayName,
                        size = 80.dp,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Profile Picture",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EditorialTheme.colors.textSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { cameraLauncher.launch(null) }) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo", tint = EditorialTheme.colors.primary)
                        }
                        IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Choose From Gallery", tint = EditorialTheme.colors.primary)
                        }
                        if (!user.profilePhotoPath.isNullOrBlank()) {
                            IconButton(onClick = {
                                viewModel.updateProfile(profilePhotoPath = "")
                                profileMessage = "Profile photo removed!"
                                isMessageError = false
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Photo", tint = Color(0xFFC62828))
                            }
                        }
                    }
                }

                // Header info card
                Surface(
                    color = EditorialTheme.colors.container,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EditorialTheme.colors.border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = user.displayName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTheme.colors.primary
                        )
                        if (user.username.isNotBlank()) {
                            Text(
                                text = "@${user.username}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = EditorialTheme.colors.accentPurple
                            )
                        }
                        Text(
                            text = if (user.isGuest) "Guest Learner (مہمان)" else user.email,
                            fontSize = 13.sp,
                            color = EditorialTheme.colors.textSecondary
                        )
                        Text(
                            text = "Joined: ${user.joinedDate}",
                            fontSize = 11.sp,
                            color = EditorialTheme.colors.textMuted
                        )
                    }
                }

                // Profile message indicator
                if (profileMessage != null) {
                    Surface(
                        color = if (isMessageError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = profileMessage!!,
                            color = if (isMessageError) Color(0xFFC62828) else Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (user.isGuest) {
                    Surface(
                        color = EditorialTheme.colors.accentPurple.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EditorialTheme.colors.accentPurple.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sync your progress",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EditorialTheme.colors.primary
                                )
                                Text(
                                    text = "Create an account to securely save your learning stats.",
                                    fontSize = 11.sp,
                                    color = EditorialTheme.colors.textSecondary
                                )
                            }
                            Button(
                                onClick = {
                                    onDismiss()
                                    onOpenAuth()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialTheme.colors.accentPurple),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sign Up", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Stats Section
                Text(
                    text = "LEARNING STATISTICS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTheme.colors.accentPurple,
                    letterSpacing = 1.2.sp
                )
                Surface(
                    color = EditorialTheme.colors.container,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EditorialTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stat 1: Streak
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${user.currentStreak} Days",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTheme.colors.primary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Streak",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(EditorialTheme.colors.border)
                        )

                        // Stat 2: Completed
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text(
                                text = "${user.lessonsCompleted} Lessons",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTheme.colors.primary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Completed",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(EditorialTheme.colors.border)
                        )

                        // Stat 3: Level
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = learnerProfile.learningLevel.overall,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTheme.colors.primary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Level",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }
                    }
                }

                if (isEditing) {
                    Text(
                        text = "EDIT PROFILE SETTINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTheme.colors.accentPurple,
                        letterSpacing = 1.2.sp
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateProfile(name = editName, username = editUsername)
                                isEditing = false
                                profileMessage = "Profile updated successfully!"
                                isMessageError = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialTheme.colors.primary)
                        ) {
                            Text("Save Changes")
                        }
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Name & Username", fontSize = 12.sp)
                    }
                }

                // Editable email sub-form for non-guests
                if (!user.isGuest) {
                    if (showEmailForm) {
                        Text(
                            text = "CHANGE EMAIL ADDRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTheme.colors.accentPurple,
                            letterSpacing = 1.2.sp
                        )
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("New Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateEmail(editEmail) { success, err ->
                                        if (success) {
                                            showEmailForm = false
                                            profileMessage = "Email changed successfully!"
                                            isMessageError = false
                                        } else {
                                            profileMessage = err ?: "Failed to update email."
                                            isMessageError = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialTheme.colors.primary)
                            ) {
                                Text("Update Email")
                            }
                            OutlinedButton(
                                onClick = { showEmailForm = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showEmailForm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Email Address", fontSize = 12.sp)
                        }
                    }

                    // Editable password sub-form
                    if (showPasswordForm) {
                        Text(
                            text = "CHANGE PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTheme.colors.accentPurple,
                            letterSpacing = 1.2.sp
                        )
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("Current Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password (min 6 chars)") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.changePassword(oldPassword, newPassword) { success, err ->
                                        if (success) {
                                            showPasswordForm = false
                                            oldPassword = ""
                                            newPassword = ""
                                            profileMessage = "Password changed successfully!"
                                            isMessageError = false
                                        } else {
                                            profileMessage = err ?: "Failed to change password."
                                            isMessageError = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialTheme.colors.primary)
                            ) {
                                Text("Save Password")
                            }
                            OutlinedButton(
                                onClick = { showPasswordForm = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showPasswordForm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Password", fontSize = 12.sp)
                        }
                    }
                }

                // Logout & delete section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Out", fontSize = 12.sp)
                    }
                }

                if (!user.isGuest) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Delete Account & Clear History",
                            color = Color(0xFFC62828),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = EditorialTheme.colors.container,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, EditorialTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = EditorialTheme.colors.textSecondary
            )
        }
    }
}
