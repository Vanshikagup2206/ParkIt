package com.vanshika.parkit.admin.screen.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.viewmodel.ProfileViewModel
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel

@Composable
fun AdminProfileScreen(
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    authViewModel: AuthenticationViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val authUser by authViewModel.user.collectAsStateWithLifecycle()
    val userProfile by profileViewModel.user.collectAsStateWithLifecycle()
    val isSaving by profileViewModel.isSaving.collectAsStateWithLifecycle()
    val profileUpdated by profileViewModel.profileUpdated.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authUser?.uid) {
        authUser?.uid?.let {
            profileViewModel.loadProfile(it)
        }
    }
    LaunchedEffect(profileUpdated) {
        if (profileUpdated) {
            showEditProfileDialog = false
            authUser?.uid?.let {
                profileViewModel.loadProfile(it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Profile section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val profileImageModifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { showProfileDialog = true }
                    userProfile?.profilePicUrl?.let {
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = "Profile Picture",
                            modifier = profileImageModifier,
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = profileImageModifier,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = userProfile?.userName ?: "Admin",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ID: ${authUser?.email ?: "Unknown"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showEditProfileDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // New options section as rounded cards
        NewProfileOptions(
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange,
            onChangePassword = { showChangePasswordDialog = true },
            onContactSupport = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:vanshikagupta0009@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                }
                context.startActivity(intent)
            },
            onPrivacyPolicy = {
                val url = "https://yourapp.com/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
            onLogout = { showLogoutDialog = true }
        )

        // App version at bottom
        Text(
            text = "Version 1.0.0",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                            authUser?.uid?.let { uid ->
                                authViewModel.updatePassword(uid, newPassword, context)
                            }
                            showChangePasswordDialog = false
                        }
                    }
                ) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        val currentName = userProfile?.userName ?: ""
        var newName by remember { mutableStateOf(currentName) }
        var newPhotoUri by remember { mutableStateOf<Uri?>(null) }
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? -> newPhotoUri = uri }
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
                TextButton(onClick = { showProfileDialog = false }) { Text("Close") }
            }
        )
    }
}

// --------- Modern Option Cards ----------

@Composable
fun NewProfileOptions(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onChangePassword: () -> Unit,
    onContactSupport: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProfileOptionCard(
            iconRes = R.drawable.baseline_password_24,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "Change Password",
            onClick = onChangePassword
        )
        ProfileOptionSwitchCard(
            iconRes = R.drawable.baseline_app_shortcut_24,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "App Theme toggle",
            checked = isDarkTheme,
            onCheckedChange = onThemeChange
        )
        ProfileOptionCard(
            iconRes = R.drawable.baseline_mail_outline_24,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "Contact Support",
            onClick = onContactSupport
        )
        ProfileOptionCard(
            iconRes = R.drawable.baseline_privacy_tip_24,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "Privacy Policy",
            onClick = onPrivacyPolicy
        )
        ProfileOptionCard(
            iconRes = R.drawable.baseline_lock_open_24,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "Logout",
            onClick = onLogout
        )
    }
}

@Composable
fun ProfileOptionCard(
    iconRes: Int,
    iconTint: Color,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ProfileOptionSwitchCard(
    iconRes: Int,
    iconTint: Color,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )
        }
    }
}