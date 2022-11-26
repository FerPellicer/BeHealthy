package com.example.behealthy.model


import com.example.fragments.data.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

interface FirestoreRepository {

    val dataBase: FirebaseFirestore?

    //val storageDb: FirebaseStorage?
    suspend fun newUser(userId: String, data: HashMap<String, String>): Resource<DocumentReference>
    suspend fun getUserNames(): Resource<QuerySnapshot>
}
