package com.example.behealthy.view
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.behealthy.R
import com.example.behealthy.view.user.UserFormFragment
import com.example.behealthy.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (userViewModel.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, UserFormFragment.newInstance("", ""))
                .commitNow()
        }

        else {

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }



    }
}