package com.example.behealthy.view.ui.recipe

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.behealthy.databinding.FragmentRecipeFormBinding
import com.example.behealthy.model.Recipe
import com.example.behealthy.view.ui.home.HomeFragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class RecipeFormFragment : Fragment() {
    private var uri: String = ""
    private var url: String = ""

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage: FirebaseStorage = Firebase.storage

    private var _binding: FragmentRecipeFormBinding? = null

    private val binding get() = _binding!!


    companion object {
        fun newInstance() = RecipeFormFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecipeFormBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.acceptForm.setOnClickListener { finishForm() }
        binding.updatePhoto.setOnClickListener { changeRecipeImage() }
    }

    private fun finishForm() {


        if (notEmptyFields()) {
            createRecipe()
        } else {
            Toast.makeText(activity, "Debe rellenar todos los datos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createRecipe() {

        if (uri != "default") {

            val storageRef = storage.reference
            val fileUri = Uri.parse(uri)
            val fileName = storageRef.child("recipesImages/${fileUri.lastPathSegment}")
            fileName.downloadUrl
            fileName.putFile(fileUri)
                .addOnSuccessListener { taskSnapshot ->

                    fileName.downloadUrl.addOnSuccessListener {

                        val recipe = hashMapOf(
                            "name" to binding.name.text.toString(),
                            "description" to binding.description.text.toString(),
                            "ingredients" to binding.ingredientsFormRecipe.text.toString(),
                            "image" to it.toString(),
                            "steps" to binding.formSteps.text.toString(),
                            "likes" to "0 likes",
                            "user" to (firebaseAuth.currentUser?.uid)
                        )

                        db.collection("recipesData").add(recipe)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                        Log.i("Recipe image", "Image uploaded successfully!")
                    }
                }

                .addOnFailureListener(OnFailureListener { e ->
                    Log.e("Recipe image", e.message.toString())
                })
        }
    }

    private fun notEmptyFields(): Boolean {
        return binding.name.text.toString().isNotEmpty()
                && binding.description.text.toString().isNotEmpty()
                && binding.ingredients.text.toString().isNotEmpty()
                && binding.formSteps.text.toString().isNotEmpty()
                && binding.updatePhoto.toString().isNotEmpty()
                && binding.updatePhoto.toString().isNotEmpty()
    }

    private fun changeRecipeImage() {

        Log.e("", "Holis")

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
        if (result.resultCode == RESULT_OK) {
            val data = result.data?.data
            uri = data.toString()
            binding.showPhoto.setImageURI(data)
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }
}