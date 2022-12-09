package com.example.behealthy.model.repository.user

import android.net.Uri
import android.util.Log
import com.example.fragments.data.Resource
import com.example.fragments.data.Resource.Success
import com.example.fragments.data.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth,
    private val firestorage: FirebaseStorage) : UserRepository {


    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun userData(): Resource<MutableMap<String, Any>?> {
        val uid = currentUser?.uid

        if (uid != null) {

            return try {
                val result = firestore.collection("users").document(uid.toString()).get().await()
                Success(result.data)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Failure(e.toString())
            }

        }

        else {
            return Resource.Failure("Error al intentar acceder al usuario")
        }

    }

    override suspend fun updateUserData(updatedData: HashMap<String, Any>): Resource<String> {
        val uid = currentUser?.uid

        if (uid != null) {

            return try {
                val result = firestore.collection("users")
                    .document(uid.toString()).update(updatedData).await()
                Resource.Success(result.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Failure(e.toString())
            }

        }
        else {
            return Resource.Failure("Error al intentar acceder al usuario")
        }
    }

    override fun changeUserImage(fileUri: Uri, fileName: String, uid: String):  Resource<String> {

        return try {
            var imageDonwloadUrl = ""

            //Log.e("Hola", "profileImages/${uid}/")

            val fileName = firestorage.reference.child(fileName)

            fileName.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    imageDonwloadUrl = it.toString()
                    Log.e("Url Imagen", imageDonwloadUrl)
                    Log.i("Progile image", "Image uploaded successfully!")

                    firestore.collection("users")
                        .document(uid)
                        .update("imageProfile", imageDonwloadUrl)

                    Log.e("ViewModel", "Imagen Actualizada")
                }
            }

            Success("Success".toString())

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e.toString())
        }

    }

}