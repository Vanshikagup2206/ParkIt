package com.vanshika.parkit.admin.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.vanshika.parkit.admin.data.model.ProfileDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class UserRepository(
    private val firestore: com.google.firebase.firestore.CollectionReference =
        Firebase.firestore.collection("users")
) {
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "diey61vc3",
            "api_key" to "554239114899574",
            "api_secret" to "xZlLAAnfKGUyHZuT9-I7txXPhhE"
        )
    )

    suspend fun getUserProfile(id: String): ProfileDataClass? {
        val snapshot = firestore.document(id).get().await()
        return snapshot.toObject(ProfileDataClass::class.java)
    }

    suspend fun updateUserProfile(
        context: Context,
        uid: String,
        name: String,
        email: String,
        photoUri: Uri?
    ): Boolean {
        return try {
            var photoUrl: String? = null

            // Upload to Cloudinary if new photo is picked
            if (photoUri != null) {
                val inputStream = context.contentResolver.openInputStream(photoUri) ?: return false

                val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
                inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val uploadResult = withContext(Dispatchers.IO) {
                    cloudinary.uploader().upload(
                        tempFile,
                        ObjectUtils.asMap("folder", "profile_uploads")
                    )
                }

                photoUrl = uploadResult["secure_url"] as String
                Log.d("CloudinaryUpload", "Uploaded Profile Image: $photoUrl")
            }

            val updateMap = mutableMapOf<String, Any>(
                "userName" to name,
                "email" to email
            )
            if (photoUrl != null) updateMap["profilePicUrl"] = photoUrl

            firestore.document(uid).set(updateMap, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e("CloudinaryProfile", "Update failed: ${e.message}", e)
            false
        }
    }

    suspend fun fetchAllUsers(): List<ProfileDataClass> {
        val snapshot = firestore.get().await()
        return snapshot.toObjects(ProfileDataClass::class.java)
    }

}