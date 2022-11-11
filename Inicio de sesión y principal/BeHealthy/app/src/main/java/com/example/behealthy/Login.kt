package com.example.behealthy

import android.R.attr.button
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.behealthy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class Login : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth;

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        firebaseAuth = FirebaseAuth.getInstance()

        //binding.register.setOnClickListener {
        //val intent = Intent(this, ActivityRegisterBinding::class.java)
        //startActivity(intent)
        //  }

        binding.login.setOnClickListener { login() }



        binding.register.setOnClickListener( { registerIntent() })

    }



    private fun login() {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Se ha iniciado sesión correctamente", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Ha habido un error", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

        }


    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun login(view: View) {
        Log.e("nsndsd", "He dsjkdskdjskdj" )
    }


    private fun registerIntent() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)

    }
}