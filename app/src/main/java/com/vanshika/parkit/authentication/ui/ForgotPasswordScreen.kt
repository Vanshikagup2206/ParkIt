package com.vanshika.parkit.authentication.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthenticationViewModel,
    navHostController: NavHostController
) {
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var triggerShake by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val passwordResetStatus by viewModel.passwordResetStatus.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(passwordResetStatus) {
        passwordResetStatus?.let { success ->
            if (success) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "✅ Password reset link sent to your email",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
                isError = false
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "❌ Failed to send reset email",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
                isError = true
                triggerShake = true
            }
        }
    }

    val emailPattern = remember {
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    }

    val isValidEmail by derivedStateOf {
        emailPattern.matcher(email.trim()).matches()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shakeAnim")
    val shakeOffsetFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (triggerShake) 8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 400
                0f at 0
                -8f at 100
                8f at 200
                -8f at 300
                0f at 400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shakeOffset"
    )
    val shakeOffset = shakeOffsetFloat.dp

    LaunchedEffect(triggerShake) {
        if (triggerShake) {
            delay(500)
            triggerShake = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon/Logo
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Forgot Password",
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Forgot Password?",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Don't worry! Enter your email and we'll send you a reset link",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Email Input Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { input ->
                                email = input.trim().lowercase()
                                isError = false
                            },
                            label = { Text("Email Address") },
                            placeholder = { Text("Enter your email") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = if (isError && triggerShake) shakeOffset else 0.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            isError = isError,
                            trailingIcon = {
                                if (isError) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                } else if (isValidEmail) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Valid",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isError)
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (isError)
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Send Reset Link Button
                        Button(
                            onClick = {
                                val trimmedEmail = email.trim().lowercase()

                                when {
                                    trimmedEmail.isEmpty() -> {
                                        isError = true
                                        triggerShake = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar("⚠️ Email cannot be empty")
                                        }
                                    }

                                    !isValidEmail -> {
                                        isError = true
                                        triggerShake = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar("⚠️ Please enter a valid email")
                                        }
                                    }

                                    else -> {
                                        isError = false
                                        viewModel.forgotPassword(trimmedEmail)
                                    }
                                }
                            },
                            enabled = !isLoading && isValidEmail,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.12f
                                )
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Send Reset Link",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Back to Login Button
                        OutlinedButton(
                            onClick = { navHostController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Back to Login",
                                style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Help Text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Check your spam folder if you don't receive the email within a few minutes",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}