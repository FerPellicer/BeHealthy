package com.example.behealthy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.behealthy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var loginButton: TextView
    lateinit var registerButton: TextView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.login.setOnClickListener { login() }
        binding.register.setOnClickListener { registerIntent() }


    }

    private fun registerIntent() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)

    }

    private fun login() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
