package com.vanshika.parkit.admin.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.parkit.admin.data.model.ProfileDataClass
import com.vanshika.parkit.admin.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<ProfileDataClass?>(null)
    val user = _user.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _profileUpdated = MutableStateFlow(false)
    val profileUpdated: StateFlow<Boolean> = _profileUpdated

    fun loadProfile(id: String) {
        viewModelScope.launch {
            try {
                _user.value = repo.getUserProfile(id)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun updateProfile(context: Context, id: String, name: String, email: String, photoUri: Uri?) {
        viewModelScope.launch {
            _isSaving.value = true
            _profileUpdated.value = false

            try {
                repo.updateUserProfile(context, id, name, email, photoUri)
                _user.value = repo.getUserProfile(id)
                _profileUpdated.value = true
            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                _isSaving.value = false
            }
        }
    }
}