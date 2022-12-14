package com.example.behealthy.model.repository.user

import android.net.Uri
import android.text.BoringLayout
import com.example.fragments.data.Resource
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    val currentUser: FirebaseUser?
    suspend fun userData(): Resource<MutableMap<String, Any>?>
    suspend fun updateUserData(updatedData: HashMap<String, Any>): Resource<String>
    fun changeUserImage(fileUri: Uri, fileName: String, uid: String): Resource<String>
    suspend fun checkUserName(userName: String): Boolean
}