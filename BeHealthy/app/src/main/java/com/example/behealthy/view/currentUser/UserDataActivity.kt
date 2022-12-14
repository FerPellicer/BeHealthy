package com.example.behealthy.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.databinding.ActivityUserDataBinding
import com.example.behealthy.model.utils.Compressor.reduceImageSize
import com.example.behealthy.viewModel.AuthViewModel
import com.example.behealthy.viewModel.UserViewModel
import com.example.fragments.data.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel: UserViewModel
    private lateinit var uid: String
    private var uri: Uri = Uri.parse("null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: Bundle? = intent.extras

        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        val navController = findNavController(R.id.nav_host_fragment_activity_user_data)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        uid = authViewModel.currentUser!!.uid
        loadUserData()

        binding.profileImage.setOnClickListener { changeProfileImage() }
        binding.updateImage.setOnClickListener { changeImage() }


        // Cargar fragmento en función del destino seleccionado por el usuario
        when (bundle?.getString("nav")) {

            "nav_saved_recipes" -> {
                navController.navigate(R.id.action_user_profile_to_user_saved_recipes)

            }

            "nav_user_recipes" -> {
                navController.navigate(R.id.action_user_profile_to_user_recipes)
            }

        }

        configureNavViewDestination(navView, navController)


    }


    private fun configureNavViewDestination(
        navView: BottomNavigationView,
        navController: NavController
    ) {
        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.user_profile -> {
                    navController.navigate(R.id.user_profile)
                    true
                }


                R.id.user_saved_recipes -> {
                    navController.navigate(R.id.user_saved_recipes)
                    true
                }


                R.id.user_recipes -> {
                    navController.navigate(R.id.user_recipes)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }


    private fun changeImage() {

        if (uri.toString() != "null") {
            // uri has been initialized


            uri = reduceImageSize(context = this, uri, quality = 20, angle = 90f)

            Glide.with(this).asBitmap().load(uri)
                .into(binding.profileImage)

            val fileName = "profileImages//${uri.lastPathSegment}"

            userViewModel.changeImage(uri, fileName, uid)

            userViewModel.imageUrlFlow.observe(this) {

                if (it.toString() == "Success(result=Success)") {
                    Toast.makeText(this, "Imagen actualizada correctamente", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e("Error Imagen", this.toString())
                    Toast.makeText(
                        this, "Se ha producido un error al actualizar la imagen." +
                                "\nVuelva a intentarlo", Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            Toast.makeText(this, "Primero debe seleccionar una nueva imagen", Toast.LENGTH_SHORT)
                .show()
        }


    }

    private fun loadUserData() {

        userViewModel.userData()
        userViewModel.userDataFlow.observe(this) {
            it?.let {
                when (it) {
                    is Resource.Failure -> {
                        Log.e("Error", "Failed traying to download user data")
                    }
                    is Resource.Success -> {

                        val imagen = it.result?.get("imageProfile")
                        Glide.with(this).asBitmap().load(imagen)
                            .into(binding.profileImage)

                        binding.userNameHeader.text = it.result?.get("name").toString()

                    }
                    else -> {}
                }
            }
        }


    }


    private fun changeProfileImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_DENIED) {

                ContextCompat.checkSelfPermission(
                    this,
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
                this, "Necesita conceder permisos a la aplicación",
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

}

