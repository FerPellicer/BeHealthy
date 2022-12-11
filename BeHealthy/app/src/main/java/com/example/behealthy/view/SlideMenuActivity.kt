package com.example.behealthy.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.databinding.ActivitySlideMenuBinding
import com.example.behealthy.view.home.HomeFragment
import com.example.behealthy.viewModel.AuthViewModel
import com.example.behealthy.viewModel.UserViewModel
import com.example.fragments.data.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SlideMenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySlideMenuBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySlideMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarSlideMenu.toolbar)

        // Quitar titulo del action bar
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_slide_menu)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_scan, R.id.nav_recipe_form
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)

        updateUserData()

        configureNavViewDestination(navView)

    }

    private fun configureNavViewDestination(navView: NavigationView) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {


                R.id.nav_user_profile -> {
                    val intent = Intent(this, UserDataActivity::class.java)
                    intent.putExtra("nav", "nav_user_profile")
                    startActivity(intent)
                    false
                }


                R.id.nav_saved_recipes -> {

                    val intent = Intent(this, UserDataActivity::class.java)
                    intent.putExtra("nav", "nav_saved_recipes")
                    startActivity(intent)
                    false
                }


                R.id.nav_user_recipes -> {

                    val intent = Intent(this, UserDataActivity::class.java)
                    intent.putExtra("nav", "nav_user_recipes")
                    startActivity(intent)
                    false
                }

                R.id.share -> {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    val message: String = "\uD83E\uDD2A¿QUE QUÉ ESSSSS BEHEALTHY?\uD83E\uDD2A " +
                            "\nLa app para mejorar tu estilo de vida\n" +
                            "¡Descárgala gratis aquí! \uD83D\uDC47"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                    startActivity(shareIntent)
                    false
                }

                else -> {

                    false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserData()
    }

    private fun updateUserData() {


        val x = userViewModel.userData()
        userViewModel.userDataFlow.observe(this) {
            it?.let {
                when (it) {
                    is Resource.Failure -> {
                        Log.e("Error", "Failed traying to download user data")
                    }
                    is Resource.Success -> {
                        var imagen = it.result?.get("imageProfile")
                        Glide.with(this).asBitmap().load(imagen)
                            .into(findViewById(R.id.menu_profile_image))

                        binding.navView.findViewById<TextView>(R.id.menu_user_name)
                            .setText(it.result?.get("name").toString())

                        binding.navView.findViewById<TextView>(R.id.menu_username)
                            .setText("@" + it.result?.get("userName").toString())

                    }
                    else -> {}
                }
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_slide_menu)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.example.behealthy.R.id.logout -> {
                authViewModel.logout()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}