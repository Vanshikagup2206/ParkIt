package com.vanshika.parkit.user.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanshika.parkit.user.data.model.ReservationRequestDataClass
import com.vanshika.parkit.user.data.repository.ReservationRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val repository: ReservationRepository
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess: StateFlow<Boolean?> = _isSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _allRequests = MutableStateFlow<List<ReservationRequestDataClass>>(emptyList())
    val allRequests: StateFlow<List<ReservationRequestDataClass>> = _allRequests


    // 🔹 Submit new reservation
    fun submitReservation(request: ReservationRequestDataClass) {
        viewModelScope.launch {
            Log.d("ReservationDebug", "🚀 Starting reservation submission for ${request.slotId}")
            _isLoading.value = true
            _errorMessage.value = null
            _isSuccess.value = null

            try {
                val success = repository.submitReservationRequest(request)
                _isSuccess.value = success
                Log.d("ReservationDebug", "✅ Reservation submitted successfully: $success")
            } catch (e: Exception) {
                Log.e("ReservationDebug", "❌ Error submitting reservation: ${e.message}", e)
                _errorMessage.value = e.message
                _isSuccess.value = false
            } finally {
                _isLoading.value = false
                Log.d("ReservationDebug", "🏁 Submission complete. Loading: ${_isLoading.value}")
            }
        }
    }

    // 🔹 Fetch all reservation requests (Admin)
    fun fetchAllRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("AdminDebug", "📤 Calling repository to get all requests...")
                val requests = repository.getAllReservationRequests()
                Log.d("AdminDebug", "📥 Received ${requests.size} requests from repo")
                requests.forEach {
                    Log.d("AdminDebug", "🪪 Request: ${it.id}, Status: ${it.requestStatus}, Slot: ${it.slotId}")
                }

                _allRequests.value = requests.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                Log.d("AdminDebug", "✅ Total requests loaded: ${_allRequests.value.size}")
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Log.e("AdminDebug", "❌ Error loading requests: ${e.message}", e)
            } finally {
                _isLoading.value = false
                Log.d("AdminDebug", "🏁 Fetch complete. Loading: ${_isLoading.value}")
            }
        }
    }

    // 🔹 Update reservation status
    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            Log.d("ReservationDebug", "🔄 Updating status of request $requestId to '$status'")
            _isLoading.value = true
            try {
                repository.updateRequestStatus(requestId, status)
                _allRequests.value = _allRequests.value.map {
                    if (it.id == requestId) it.copy(requestStatus = status) else it
                }
                Log.d("ReservationDebug", "✅ Status updated successfully for $requestId")
            } catch (e: Exception) {
                Log.e("ReservationDebug", "❌ Error updating status: ${e.message}", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
                Log.d("ReservationDebug", "🏁 Status update complete. Loading: ${_isLoading.value}")
            }
        }
    }
}