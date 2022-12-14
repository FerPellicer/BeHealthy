package com.example.behealthy.view.user

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.behealthy.R
import com.example.behealthy.databinding.FragmentRegisterBinding
import com.example.behealthy.model.utils.Compressor
import com.example.behealthy.view.SlideMenuActivity
import com.example.behealthy.viewModel.AuthViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private var uri: Uri = Uri.parse("default")

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        firebaseAuth = FirebaseAuth.getInstance()
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.register.setOnClickListener { register() }
        binding.profileImage.setOnClickListener { changeProfileImage() }
        binding.login.setOnClickListener { loginFrgament() }
    }

    private fun loginFrgament() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, LoginFragment.newInstance())
            ?.commitNow()
    }

    private fun register() {

        if (notEmptyFields()) {
            if (binding.password.text.toString().length < 6 && validateEmail()) {
                Toast.makeText(
                    activity, "La contraseña debe tener 6 caracteres como mínimo",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (!checkUserName()) {
                Toast.makeText(
                    activity,
                    "Nombre de usuario en uso, por favor, introduzca otro",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                createUser()

            }
        } else {
            Toast.makeText(activity, "Debe rellenar todos los datos", Toast.LENGTH_SHORT).show()
        }


    }

    private fun notEmptyFields(): Boolean {
        return binding.name.text.toString().isNotEmpty() && binding.surname.text.isNotEmpty()
                && binding.userName.toString().isNotEmpty()
                && binding.email.toString().isNotEmpty() &&
                binding.password.toString().isNotEmpty()
    }


    private fun createUser() {

        if (uri.toString().equals("default")) {
            Toast.makeText(activity, "Debe seleccionar una foto de perfil", Toast.LENGTH_SHORT)
                .show()
        } else {


            Toast.makeText(activity, "Creando usuario", Toast.LENGTH_SHORT).show()

            firebaseAuth.createUserWithEmailAndPassword(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {

                        val currentUser = firebaseAuth.currentUser

                        if (currentUser != null) {


                            val userInfo = hashMapOf(
                                "email" to binding.email.text.toString(),
                                "name" to binding.name.text.toString(),
                                "surname" to binding.surname.text.toString(),
                                "userName" to binding.userName.text.toString(),
                                "likesUser" to ArrayList<String>(),
                                "saveRecipes" to ArrayList<String>(),
                                "imageProfile" to ""
                            )

                            db.collection("users").document(currentUser.uid)
                                .set(userInfo)
                                .addOnSuccessListener {

                                    Log.d(TAG, "DocumentSnapshot added with ID: ${currentUser.uid}")


                                    Log.i("User", "User created successfully")

                                    authViewModel.loginUser(
                                        binding.email.text.toString(),
                                        binding.password.text.toString()
                                    )

                                    val fileName = storage.reference
                                        .child("profileImages/${currentUser.uid}/${uri.lastPathSegment}")

                                    uri = Compressor.reduceImageSize(
                                        context = requireContext(),
                                        uri,
                                        quality = 20,
                                        angle = 90f
                                    )
                                    fileName.putFile(uri).addOnSuccessListener { taskSnapshot ->
                                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {

                                            Log.i("Progile image", "Image uploaded successfully!")

                                            Toast.makeText(
                                                activity, "Usuario creado correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            db.collection("users").document(currentUser.uid)
                                                .update("imageProfile", it.toString())
                                                .addOnCompleteListener {

                                                    val intent = Intent(
                                                        activity,
                                                        SlideMenuActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    this.requireActivity().finish()
                                                }

                                        }
                                    }

                                        .addOnFailureListener(OnFailureListener { e ->
                                            Log.e("Progile image", e.message.toString())
                                        })


                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }


                    } else {
                        Log.e("User", "Failed trying to create new user")
                        Toast.makeText(
                            activity, "Se ha producido un error al intentar" +
                                    "crear el usuario", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }

    private fun changeProfileImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_DENIED) {

                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    pickImageFromGallery()
                }
                else -> requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

        } else {
            pickImageFromGallery()
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    )
    { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            Toast.makeText(
                activity, "Necesita conceder permisos a la aplicación",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    )
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            uri = result.data!!.data!!
            binding.profileImage.setImageURI(data)
        }

    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }


    private fun validateEmail(): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(binding.email.text.toString()).matches()
    }


    private fun checkUserName(): Boolean {

        var used = 0

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (user in result) {

                    if (binding.userName.text.toString() == user.data.get("userName").toString()) {
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