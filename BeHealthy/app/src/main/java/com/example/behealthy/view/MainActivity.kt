package com.example.behealthy.view
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.behealthy.R
import com.example.behealthy.UserFormFragment
import com.example.behealthy.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (authViewModel.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, UserFormFragment.newInstance())
                .commitNow()
        }

        else {

            val intent = Intent(this, SlideMenuActivity::class.java)
            startActivity(intent)
        }



    }
}