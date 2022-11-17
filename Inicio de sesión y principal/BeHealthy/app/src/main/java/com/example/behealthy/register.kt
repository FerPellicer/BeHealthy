package com.example.behealthy

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.behealthy.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.regex.Pattern


class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var uri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        db = Firebase.firestore

        storage = Firebase.storage

        binding.next.setOnClickListener { nextForm() }
        binding.profileImage.setOnClickListener{ changeProfileImage() }


    }




    private fun changeProfileImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    pickImageFromGallery()
                }
                else -> requestPermissionLauncher.launch(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }
        else {
            pickImageFromGallery()
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted) {
                pickImageFromGallery()
            }

            else {
                Toast.makeText(this, "Necesita conceder permisos a la aplicación",
                    Toast.LENGTH_SHORT).show()
            }
        }



    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            uri = data.toString()
            binding.profileImage.setImageURI(data)
        }
        
    }




    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }


    private fun nextForm() {

        val name = binding.name.text.toString()
        val surname = binding.surname.text.toString()
        val userName = binding.userName.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()


        if (name.isNotEmpty() && surname.isNotEmpty() && userName.isNotEmpty()
            && email.isNotEmpty() && password.isNotEmpty()) {



            if (password.length < 6 && validateEmail(email)) {
                Toast.makeText(this, "La contraseña debe tener 6 caracteres como mínimo",
                    Toast.LENGTH_SHORT).show()
            }


            if (checkUserName(userName)) {
                createUser(name, surname, userName, email, password)
            }

            else {
                Toast.makeText(this, "Nombre de usuario en uso, por favor, introduzca otro", Toast.LENGTH_SHORT).show()
            }
        }


        else {
            Toast.makeText(this, "Tiene que rellenar todos los campos", Toast.LENGTH_SHORT).show()
        }



    }




    private fun createUser(name: String, surname: String, userName: String, email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {

                    val currentUser = firebaseAuth.currentUser

                    val userInfo = hashMapOf(
                        "email" to email,
                        "name" to name,
                        "surname" to surname,
                        "userName" to userName
                    )


                    if (currentUser != null) {

                        uploadImage(currentUser.uid)

                        db.collection("users").document(currentUser.uid)
                            .set(userInfo)
                            .addOnSuccessListener {

                                Log.d(TAG, "DocumentSnapshot added with ID: ${currentUser.uid}")


                                Log.i("User", "User created successfully")
                                Toast.makeText(this, "Usuario creado correctamente",
                                    Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }


                }

                else {
                    Log.e("User", "Failed trying to create new user")
                    Toast.makeText(this, "Se ha procudico un error al intentar" +
                            "crear el usuario", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun uploadImage(uid: String) {

        Log.e("dw", "efef")

        if (uri != "default") {

            val storageRef = storage.reference
            val fileUri = Uri.parse(uri)
            val fileName = storageRef.child("profileImages/${uid}/${fileUri.lastPathSegment}")

            fileName.putFile(fileUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        //val imageUrl = it.toString()
                        Log.i("Progile image", "Image uploaded successfully!")
                    }
                }

                ?.addOnFailureListener(OnFailureListener { e ->
                    Log.e("Progile image", e.message.toString())
                })
        }
    }



    private fun validateEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }



    private fun checkUserName(userName: String): Boolean {

        var used = 0

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (userName == document.data.get("userName")) {
                        used = 1
                        break
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        return used == 0

    }


}