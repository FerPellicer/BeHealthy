package com.example.behealthy.view.recipe

import android.Manifest
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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.databinding.FragmentRecipeFormBinding
import com.example.behealthy.model.data.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RecipeFormFragment : Fragment() {
    private var uri: Uri = Uri.parse("default")
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage: FirebaseStorage = Firebase.storage

    private var name : String = ""
    private var description : String = ""
    private var ingredients : String = ""
    private var steps : String = ""
    private var imageActive : Boolean = false
    private var modify : Boolean = false
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

    override fun onResume() {
        super.onResume()

        Log.e("Resume", this.imageActive.toString())

        if(! this.modify){
            if(this.imageActive){
                Log.e("Resume", this.ingredients)

                this.imageActive = false
                binding.ingredientsFormRecipe.setText(this.ingredients)
                binding.description.setText(this.description)
                binding.name.setText(this.name)
                binding.formSteps.setText(this.steps)
            }else {
                binding.ingredientsFormRecipe.setText("")
                binding.description.setText("")
                binding.name.setText("")
                binding.formSteps.setText("")
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.isEmpty == false){

            this.modify = true

            val recipeArgs : Recipe = arguments?.getSerializable("recipeArg") as Recipe
            binding.acceptForm.setOnClickListener { finishFormUpdate(recipeArgs) }
            binding.updatePhoto.setOnClickListener { changeRecipeImage(false) }

            binding.ingredientsFormRecipe.setText(recipeArgs.ingredients)
            binding.description.setText(recipeArgs.description)
            binding.name.setText(recipeArgs.name)
            binding.formSteps.setText(recipeArgs.steps)
            activity?.let { Glide.with(it).asBitmap().load(recipeArgs.image).into(binding.showPhoto) }

        }else{

            binding.acceptForm.setOnClickListener { finishForm() }
            binding.updatePhoto.setOnClickListener { changeRecipeImage(true) }

        }

    }

    private fun finishForm() {


        if (notEmptyFields()) {
            createRecipe()
        } else {
            Toast.makeText(activity, "Debe rellenar todos los datos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun finishFormUpdate(recipeArg : Recipe) {


        if (notEmptyFields()) {
            updateRecipe(recipeArg)
            Toast.makeText(activity, "Receta modificada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Debe rellenar todos los datos", Toast.LENGTH_SHORT).show()
        }

    }


    private fun createRecipe() {

        Toast.makeText(activity, "Subiendo receta", Toast.LENGTH_SHORT).show()

        if (uri.toString() != "default") {

            val storageRef = storage.reference
            val fileUri = Uri.parse(uri.toString())
            val fileName = storageRef.child("recipesImages/${fileUri.lastPathSegment}")
            fileName.downloadUrl
            fileName.putFile(fileUri)
                .addOnSuccessListener {

                    fileName.downloadUrl.addOnSuccessListener {

                        val recipe = hashMapOf(
                            "name" to binding.name.text.toString(),
                            "description" to binding.description.text.toString(),
                            "ingredients" to binding.ingredientsFormRecipe.text.toString(),
                            "image" to it.toString(),
                            "steps" to binding.formSteps.text.toString(),
                            "likes" to "0",
                            "user" to (firebaseAuth.currentUser?.uid)
                        )

                        db.collection("recipesData").add(recipe)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")

                                Toast.makeText(activity, "Receta subida correctamente", Toast.LENGTH_SHORT).show()

                                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_slide_menu)
                                    .navigate(R.id.nav_home)
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                                Toast.makeText(activity, "Fallo al crear la receta", Toast.LENGTH_SHORT).show()
                            }


                        Log.i("Recipe image", "Image uploaded successfully!")


                    }
                }

                .addOnFailureListener { e ->
                    Log.e("Recipe image", e.message.toString())
                }
        }
    }

    private fun updateRecipe(recipeArg : Recipe) {

        if (uri.toString() != "default") {

            val storageRef = storage.reference
            val fileUri = Uri.parse(uri.toString())
            val fileName = storageRef.child("recipesImages/${fileUri.lastPathSegment}")
            fileName.downloadUrl
            fileName.putFile(fileUri)
                .addOnSuccessListener {

                    fileName.downloadUrl.addOnSuccessListener {

                        Log.e("image", it.toString())

                        val recipe: HashMap<String, String?>

                        if(it != null){
                            recipe = hashMapOf(
                                "name" to binding.name.text.toString(),
                                "description" to binding.description.text.toString(),
                                "ingredients" to binding.ingredientsFormRecipe.text.toString(),
                                "image" to it.toString(),
                                "steps" to binding.formSteps.text.toString(),
                                "likes" to "0",
                                "user" to (firebaseAuth.currentUser?.uid)
                            )
                        }else{
                            recipe = hashMapOf(
                                "name" to binding.name.text.toString(),
                                "description" to binding.description.text.toString(),
                                "ingredients" to binding.ingredientsFormRecipe.text.toString(),
                                "image" to recipeArg.image,
                                "steps" to binding.formSteps.text.toString(),
                                "likes" to recipeArg.likes,
                                "user" to recipeArg.user
                            )
                        }


                        recipeArg.idRecipe?.let { it1 ->
                            db.collection("recipesData").document(it1).update(recipe as Map<String, Any>)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                                }
                        }

                        Log.i("Recipe image", "Image uploaded successfully!")
                    }
                }

                .addOnFailureListener { e ->
                    Log.e("Recipe image", e.message.toString())
                }
        }
    }

    private fun notEmptyFields(): Boolean {
        return (binding.name.text.toString().isNotEmpty()
                && binding.description.text.toString().isNotEmpty()
                && binding.ingredients.text.toString().isNotEmpty()
                && binding.formSteps.text.toString().isNotEmpty()
                && (! uri.toString().equals("default") || this.modify) )

    }

    private fun changeRecipeImage(option : Boolean) {

        this.imageActive = option
        this.name = binding.name.text.toString()
        this.description  = binding.description.text.toString()
        this.ingredients = binding.ingredientsFormRecipe.text.toString()
        this.steps  = binding.formSteps.text.toString()

        Log.e("Update", this.ingredients)

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
            uri = result.data!!.data!!
            binding.showPhoto.setImageURI(data)
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }
}