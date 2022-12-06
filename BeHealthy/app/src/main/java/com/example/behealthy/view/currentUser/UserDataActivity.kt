package com.example.behealthy.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.databinding.ActivityUserDataBinding
import com.example.behealthy.viewModel.AuthViewModel
import com.example.behealthy.viewModel.UserViewModel
import com.example.fragments.data.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel : UserViewModel
    private lateinit var uid : String

    private var uri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    }

    private fun changeImage() {

        val fileUri = Uri.parse("uri")

        val cR = this.contentResolver
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(cR.getType(fileUri))
        val fileName = "profileImages/${uid}/${fileUri.lastPathSegment}.${type}"

        Log.e("FileName", fileName)


        userViewModel.changeImage(fileUri, fileName)
        Log.e("userurl", userViewModel.imageUrlFlow.toString())

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

                        var imagen = it.result?.get("imageProfile")
                        Glide.with(this).asBitmap().load(imagen)
                            .into(binding.profileImage)

                        binding.userNameHeader
                            .setText(it.result?.get("name").toString())

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
            uri = data.toString()
            binding.profileImage.setImageURI(data)
        }

    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

}