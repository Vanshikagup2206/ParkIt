package com.vanshika.parkit.user.screen.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.viewmodel.ProfileViewModel
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import com.vanshika.parkit.ui.theme.BlueAccent
import com.vanshika.parkit.ui.theme.RedAccent
import com.vanshika.parkit.ui.theme.ThemePreference

@Composable
fun UserProfileScreen(
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    authViewModel: AuthenticationViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark by ThemePreference.getTheme(context).collectAsState(initial = false)
    val backgroundColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFE9EEF5)
    val lightShadow = if (isDark) Color(0xFF2A2A2A) else Color.White
    val darkShadow = if (isDark) Color(0xFF141414) else Color(0xFFBEC8D2)

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val authUser by authViewModel.user.collectAsStateWithLifecycle()
    val userProfile by profileViewModel.user.collectAsStateWithLifecycle()
    val isSaving by profileViewModel.isSaving.collectAsStateWithLifecycle()
    val profileUpdated by profileViewModel.profileUpdated.collectAsStateWithLifecycle()

    LaunchedEffect(authUser?.uid) {
        authUser?.uid?.let { profileViewModel.loadProfile(it) }
    }
    LaunchedEffect(profileUpdated) {
        if (profileUpdated) {
            showEditProfileDialog = false
            authUser?.uid?.let { profileViewModel.loadProfile(it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .neumorphic(lightShadow, darkShadow, backgroundColor, 16.dp)
                        .clip(CircleShape)
                        .clickable { showProfileDialog = true }
                ) {
                    val painter = if (userProfile?.profilePicUrl != null)
                        rememberAsyncImagePainter(model = userProfile?.profilePicUrl)
                    else
                        painterResource(R.drawable.baseline_person_24)
                    Image(
                        painter = painter,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BlueAccent)
                        .border(2.dp, backgroundColor, CircleShape)
                        .clickable { showEditProfileDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Name + Email
            Text(
                text = userProfile?.userName ?: "User",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else Color(0xFF1A1A1A)
            )
            Text(
                text = authUser?.email ?: "Unknown",
                fontSize = 15.sp,
                color = if (isDark) Color(0xFFAAAAAA) else Color.Gray
            )

            // Options (Neumorphic)
            ProfileOption(
                title = "Change Password",
                iconRes = R.drawable.baseline_password_24,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                backgroundColor = backgroundColor
            ) {
                showChangePasswordDialog = true
            }

            ProfileOptionSwitch(
                title = "Theme Toggle",
                iconRes = R.drawable.baseline_app_shortcut_24,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                backgroundColor = backgroundColor,
                checked = isDarkTheme,
                onCheckedChange = { onThemeChange(it) }
            )

            ProfileOption(
                title = "Contact Support",
                iconRes = R.drawable.baseline_mail_outline_24,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                backgroundColor = backgroundColor
            ) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:vanshikagupta0009@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                }
                context.startActivity(intent)
            }

            ProfileOption(
                title = "Privacy Policy",
                iconRes = R.drawable.baseline_privacy_tip_24,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                backgroundColor = backgroundColor
            ) {
                val url = "https://yourapp.com/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }

            ProfileOption(
                title = "Logout",
                iconRes = R.drawable.baseline_lock_open_24,
                lightShadow = lightShadow,
                darkShadow = darkShadow,
                backgroundColor = backgroundColor,
                isLogout = true
            ) {
                showLogoutDialog = true
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Version 1.0.0",
                fontSize = 13.sp,
                color = if (isDark) Color.Gray else Color.DarkGray.copy(alpha = 0.7f)
            )
        }
    }

    // -------- Change Password Dialog --------
    if (showChangePasswordDialog) {
        var step by remember { mutableStateOf(1) } // 1: send OTP, 2: verify OTP, 3: new password
        var generatedOtp by remember { mutableStateOf("") }
        var enteredOtp by remember { mutableStateOf(List(6) { "" }) } // 6 digits
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        val userEmail = authUser?.email ?: ""
        val focusManager = LocalFocusManager.current
        val focusRequesters = List(6) { FocusRequester() }

        fun resetOtp() {
            enteredOtp = List(6) { "" }
        }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (step) {
                        1 -> {
                            Text("An OTP will be sent to your registered email: $userEmail")
                        }

                        2 -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                enteredOtp.forEachIndexed { index, value ->
                                    OutlinedTextField(
                                        value = value,
                                        onValueChange = { input ->
                                            if (input.length <= 1 && input.all { it.isDigit() }) {
                                                enteredOtp = enteredOtp.toMutableList()
                                                    .also { it[index] = input }

                                                if (input.isNotEmpty() && index < 5) {
                                                    focusRequesters[index + 1].requestFocus()
                                                }
                                            } else if (input.isEmpty() && index > 0) {
                                                focusRequesters[index - 1].requestFocus()
                                            }
                                        },
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(
                                            textAlign = TextAlign.Center,
                                            fontSize = 20.sp
                                        ),
                                        modifier = Modifier
                                            .width(38.dp)
                                            .height(45.dp)
                                            .focusRequester(focusRequesters[index]),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = if (index == 5) ImeAction.Done
                                            else ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = {
                                                if (index < 5) focusRequesters[index + 1].requestFocus()
                                            },
                                            onDone = {
                                                focusManager.clearFocus()
                                            }
                                        )
                                    )
                                }
                            }
                        }

                        3 -> {
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("New Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    when (step) {
                        1 -> {
                            // Generate OTP and send to email
                            generatedOtp = (100000..999999).random().toString()
                            sendOtpEmail(userEmail, generatedOtp, context)
                            Toast.makeText(context, "OTP sent to $userEmail", Toast.LENGTH_SHORT)
                                .show()
                            step = 2
                            resetOtp()
                        }

                        2 -> {
                            val enteredOtpStr = enteredOtp.joinToString("")
                            if (enteredOtpStr == generatedOtp) {
                                step = 3
                            } else {
                                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                            }
                        }

                        3 -> {
                            if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                                authUser?.uid?.let { uid ->
                                    authViewModel.updatePassword(uid, newPassword, context)
                                }
                                Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT)
                                    .show()
                                showChangePasswordDialog = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "Passwords do not match",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }) {
                    Text(
                        when (step) {
                            1 -> "Send OTP"
                            2 -> "Verify OTP"
                            else -> "Update Password"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------- Logout Confirmation Dialog --------
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------- Edit Profile Dialog --------
    if (showEditProfileDialog) {
        val currentName = userProfile?.userName ?: ""
        var newName by remember { mutableStateOf(currentName) }
        var newPhotoUri by remember { mutableStateOf<Uri?>(null) }
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            newPhotoUri = uri
        }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val imagePainter = rememberAsyncImagePainter(
                            model = newPhotoUri ?: userProfile?.profilePicUrl,
                            placeholder = painterResource(id = R.drawable.baseline_image_24)
                        )
                        Image(
                            painter = imagePainter,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                                contentDescription = "Change photo",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = authUser?.uid
                        if (userId != null) {
                            profileViewModel.updateProfile(
                                context,
                                userId,
                                newName,
                                authUser?.email ?: "",
                                newPhotoUri
                            )
                        }
                    },
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = userProfile?.profilePicUrl),
                        contentDescription = "Profile Preview",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(CircleShape)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

fun sendOtpEmail(userEmail: String, otp: String, context: Context) {
    val subject = "Your OTP for Password Change"
    val body = """
        Hello,
        
        Your OTP to change your password is: $otp
        
        Please do not share it with anyone.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822" // forces only email apps
        putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Send OTP Email"))
    } catch (e: Exception) {
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ProfileOption(
    title: String,
    iconRes: Int,
    lightShadow: Color,
    darkShadow: Color,
    backgroundColor: Color,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isDark by ThemePreference.getTheme(context).collectAsState(initial = false)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .neumorphic(lightShadow, darkShadow, backgroundColor, 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(backgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = if (isLogout) RedAccent else if (isDark) Color.White else Color(0xFF1A1A1A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (isLogout) RedAccent else if (isDark) Color.White else Color(0xFF1A1A1A),
                fontWeight = if (isLogout) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

fun Modifier.neumorphic(
    lightShadow: Color,
    darkShadow: Color,
    backgroundColor: Color,
    blur: Dp = 12.dp,
    cornerRadius: Dp = 16.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val shadowColor1 = lightShadow.copy(alpha = 0.8f)
        val shadowColor2 = darkShadow.copy(alpha = 0.8f)
        val offset = 6.dp.toPx()

        drawRoundRect(
            color = shadowColor1,
            topLeft = Offset(-offset, -offset),
            size = size,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
            blendMode = BlendMode.SrcOver
        )
        drawRoundRect(
            color = shadowColor2,
            topLeft = Offset(offset, offset),
            size = size,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
            blendMode = BlendMode.SrcOver
        )
        drawRoundRect(
            color = backgroundColor,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
)

@Composable
fun ProfileOptionSwitch(
    title: String,
    iconRes: Int,
    lightShadow: Color,
    darkShadow: Color,
    backgroundColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val isDark by ThemePreference.getTheme(context).collectAsState(initial = false)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .neumorphic(lightShadow, darkShadow, backgroundColor, 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = if (isDark) Color.White else Color(0xFF1A1A1A),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = if (isDark) Color.White else Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
            }

            androidx.compose.material3.Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = if (isDark) Color.LightGray else Color.Gray,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedTrackColor = if (isDark) Color.DarkGray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}