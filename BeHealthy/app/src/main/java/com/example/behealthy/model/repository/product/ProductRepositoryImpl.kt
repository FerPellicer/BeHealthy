package com.example.behealthy.model.repository.product

import com.example.fragments.data.Resource
import com.example.fragments.data.await
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore) : ProductRepository {

    override suspend fun productData(id: String): Resource<MutableMap<String, Any>?>? {

        return try {
            val result = firestore.collection("products").document(id).get().await()
            Resource.Success(result.data)


        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e.toString())
        }

    }


    suspend fun productDataSnapshot(id: String): Resource.Success<Task<DocumentSnapshot>> {

        val result = firestore.collection("products").document(id).get()
        result.await()
        return Resource.Success(result)
    }


}