package com.example.behealthy.model

import com.example.fragments.data.Resource
import com.google.firebase.auth.FirebaseUser

interface ProductRepository {
    suspend fun productData(id: String): Resource<MutableMap<String, Any>?>?
}