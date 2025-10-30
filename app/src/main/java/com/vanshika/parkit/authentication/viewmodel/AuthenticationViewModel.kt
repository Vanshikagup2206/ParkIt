package com.vanshika.parkit.authentication.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.onesignal.OneSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor() : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _customUserId = MutableStateFlow<String?>(null)
    val customUserId: StateFlow<String?> = _customUserId

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _passwordResetStatus = MutableStateFlow<Boolean?>(null)
    val passwordResetStatus: StateFlow<Boolean?> = _passwordResetStatus

    init {
        auth.currentUser?.let {
            fetchAndSetCustomUserId(it)
        }
    }

    fun signup(email: String, password: String, customId: String) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    _user.value = firebaseUser
                    _errorMessage.value = null
                    firebaseUser?.let { saveCustomUserId(it, customId) }
                } else {
                    _errorMessage.value = task.exception?.message ?: "Signup failed"
                }
            }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    _user.value = firebaseUser
                    _errorMessage.value = null
                    firebaseUser?.let { fetchAndSetCustomUserId(it) }
                } else {
                    _errorMessage.value = task.exception?.message ?: "Login failed"
                }
            }
    }

    fun updatePassword(userId: String, newPassword: String, context: Context) {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.uid == userId) {
            _isLoading.value = true
            currentUser.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            task.exception?.message ?: "Failed to update password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(context, "No logged-in user found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCustomUserId(user: FirebaseUser, customId: String) {
        val role = if (customId == "12200814") "admin" else "user"
        val userRef = firestore.collection("customId").document()
        val userData = hashMapOf(
            "customUserId" to customId,
            "email" to user.email,
            "role" to role
        )
        userRef.set(userData, SetOptions.merge())
            .addOnSuccessListener {
                _customUserId.value = customId
                _role.value = role
                try {
                    OneSignal.login(customId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .addOnFailureListener {
                _errorMessage.value = it.message
            }
    }

    private fun fetchAndSetCustomUserId(user: FirebaseUser) {
        firestore.collection("customId")
            .whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val fetchedCustomId = doc.getString("customUserId")
                    _customUserId.value = doc.getString("customUserId")
                    _role.value = doc.getString("role")
                    try {
                        fetchedCustomId?.let { OneSignal.login(it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    _customUserId.value = null
                    _role.value = null
                }
            }
            .addOnFailureListener {
                _errorMessage.value = it.message
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _customUserId.value = null
        _role.value = null
        _errorMessage.value = null
        _passwordResetStatus.value = null
        OneSignal.logout()
    }

    fun checkLoggedInUser() {
        _user.value = auth.currentUser
        _user.value?.let { fetchAndSetCustomUserId(it) }
    }

    fun forgotPassword(email: String) {
        _isLoading.value = true
        _passwordResetStatus.value = null
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _passwordResetStatus.value = true
                } else {
                    _passwordResetStatus.value = false
                    _errorMessage.value = task.exception?.message ?: "Failed to send reset email"
                }
            }
    }
}