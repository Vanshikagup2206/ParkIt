package com.vanshika.parkit.authentication.ui

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.R
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel

@Composable
fun LoginScreen(
    viewModel: AuthenticationViewModel,
    navHostController: NavHostController
) {
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var idError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val error by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val user by viewModel.user.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val setIdError: (String) -> Unit = { idError = it }
    val setEmailError: (String) -> Unit = { emailError = it }
    val setPasswordError: (String) -> Unit = { passwordError = it }

    LaunchedEffect(user) {
        if (user != null) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            if (id.trim().equals("12200814", ignoreCase = true)) {
                navHostController.navigate(MainNavRoutes.AdminMain.route) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                navHostController.navigate(MainNavRoutes.UserMain.route) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon/Logo Area
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "ParkIt Logo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "ParkIt",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = "Welcome Back!",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login Form Card
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
                    // ID Field
                    OutlinedTextField(
                        value = id,
                        onValueChange = {
                            id = it.trim()
                            idError = ""
                        },
                        label = { Text("ID / Vehicle No.") },
                        isError = idError.isNotEmpty(),
                        supportingText = {
                            if (idError.isNotEmpty())
                                Text(idError, color = MaterialTheme.colorScheme.error)
                        },
                        singleLine = true,
                        placeholder = { Text("Enter your ID") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "ID"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it.lowercase().trim()
                            emailError = ""
                        },
                        label = { Text("Email") },
                        isError = emailError.isNotEmpty(),
                        supportingText = {
                            if (emailError.isNotEmpty())
                                Text(emailError, color = MaterialTheme.colorScheme.error)
                        },
                        singleLine = true,
                        placeholder = { Text("Enter your email") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it.trim()
                            passwordError = ""
                        },
                        label = { Text("Password") },
                        isError = passwordError.isNotEmpty(),
                        supportingText = {
                            if (passwordError.isNotEmpty())
                                Text(passwordError, color = MaterialTheme.colorScheme.error)
                        },
                        singleLine = true,
                        placeholder = { Text("Enter your password") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                performLogin(
                                    id,
                                    email,
                                    password,
                                    context,
                                    viewModel,
                                    setIdError,
                                    setEmailError,
                                    setPasswordError
                                )
                            }
                        ),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (isPasswordVisible)
                                painterResource(id = R.drawable.baseline_visibility_24)
                            else
                                painterResource(id = R.drawable.baseline_visibility_off_24)
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(painter = icon, contentDescription = "Toggle password")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot Password
                    TextButton(
                        onClick = { navHostController.navigate(MainNavRoutes.ForgotPassword.route) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Forgot password?",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    Button(
                        onClick = {
                            performLogin(
                                id,
                                email,
                                password,
                                context,
                                viewModel,
                                setIdError,
                                setEmailError,
                                setPasswordError
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
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
                                imageVector = Icons.Default.Login,
                                contentDescription = "Login",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Login",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    error?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    it,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = { navHostController.navigate(MainNavRoutes.Signup.route) }
                ) {
                    Text(
                        "Sign Up",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun performLogin(
    id: String,
    email: String,
    password: String,
    context: Context,
    viewModel: AuthenticationViewModel,
    setIdError: (String) -> Unit,
    setEmailError: (String) -> Unit,
    setPasswordError: (String) -> Unit
) {
    when {
        id.isBlank() -> setIdError("Enter this field")
        email.isBlank() -> setEmailError("Enter your email")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> setEmailError("Enter a valid email")
        password.isBlank() -> setPasswordError("Enter password")
        password.length < 6 -> setPasswordError("Password must be at least 6 characters")
        else -> viewModel.login(email.trim(), password.trim())
    }
}