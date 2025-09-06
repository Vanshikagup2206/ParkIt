package com.vanshika.parkit.authentication.ui

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.R
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel

@Composable
fun SignupScreen(
    viewModel: AuthenticationViewModel,
    navHostController: NavHostController
) {
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var idError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val error by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Theme-based field background
    val isDark = isSystemInDarkTheme()
    val fieldBackground = if (isDark) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) // Dark background
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 1f) // White background
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedContainerColor = fieldBackground,
        unfocusedContainerColor = fieldBackground,
        disabledContainerColor = fieldBackground,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ParkIt",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ID Field
        OutlinedTextField(
            value = id,
            onValueChange = {
                id = it
                idError = ""
            },
            label = { Text("Admin Id/ Student Id/ Vehicle No.") },
            placeholder = { Text("Enter your id/vehicle no.") },
            isError = idError.isNotEmpty(),
            supportingText = {
                if (idError.isNotEmpty()) Text(
                    text = idError,
                    color = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(fieldBackground),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = ""
            },
            label = { Text("EmailId") },
            placeholder = { Text("Enter your email") },
            isError = emailError.isNotEmpty(),
            supportingText = {
                if (emailError.isNotEmpty()) Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(fieldBackground),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ""
            },
            label = { Text("Enter Password") },
            placeholder = { Text("Enter your password") },
            isError = passwordError.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty()) Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(fieldBackground),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.baseline_visibility_24
                            else R.drawable.baseline_visibility_off_24
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = ""
            },
            label = { Text("Confirm Password") },
            placeholder = { Text("Re-enter your password") },
            isError = confirmPasswordError.isNotEmpty(),
            supportingText = {
                if (confirmPasswordError.isNotEmpty()) Text(
                    text = confirmPasswordError,
                    color = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(fieldBackground),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isConfirmPasswordVisible) R.drawable.baseline_visibility_24
                            else R.drawable.baseline_visibility_off_24
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Signup Button
        Button(
            onClick = {
                when {
                    id.isBlank() -> idError = "Enter this field"
                    email.isBlank() -> emailError = "Enter your email address"
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> emailError =
                        "Enter a valid email address"

                    password.isBlank() -> passwordError = "Enter password"
                    password.length < 6 -> passwordError = "Password length should be ≥6"
                    confirmPassword.isBlank() -> confirmPasswordError = "Enter password"
                    confirmPassword.length < 6 -> confirmPasswordError =
                        "Password length should be ≥6"

                    confirmPassword != password -> confirmPasswordError = "Passwords should match"
                    else -> {
                        viewModel.signup(email.trim(), password.trim(), id.trim())
                        Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT).show()
                        if (id.trim().equals("12200814", ignoreCase = true)) {
                            navHostController.navigate(MainNavRoutes.AdminMain.route) {
                                popUpTo("signup") { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navHostController.navigate(MainNavRoutes.UserMain.route) {
                                popUpTo("signup") { inclusive = true }
                                launchSingleTop = true
                            }
                            Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(14.dp)
        ) {
            Text("Sign Up", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString("Already have an account? Login"),
            onClick = { navHostController.navigate(MainNavRoutes.Login.route) },
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}