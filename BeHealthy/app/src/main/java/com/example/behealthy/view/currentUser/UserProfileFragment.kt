package com.example.behealthy.view.currentUser

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.behealthy.databinding.FragmentUserProfileBinding
import com.example.behealthy.viewModel.AuthViewModel
import com.example.behealthy.viewModel.UserViewModel
import com.example.fragments.data.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserProfileFragment : Fragment() {


    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel : UserViewModel
    private lateinit var authViewModel: AuthViewModel

    private lateinit var currentName: String
    private lateinit var currentUserName: String
    private lateinit var currentSurname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        loadUserData()
        binding.updateButton.setOnClickListener { updateData() }


    }

    private fun loadUserData() {
        userViewModel.userData()
        userViewModel.userDataFlow.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Resource.Failure -> {
                        Log.e("Error", "Failed traying to download user data")
                    }
                    is Resource.Success -> {

                        currentName = it.result?.get("name").toString()
                        binding.nameField
                            .setText(it.result?.get("name").toString())

                        currentUserName = it.result?.get("userName").toString()
                        binding.userNameField
                            .setText(it.result?.get("userName").toString())

                        currentSurname = it.result?.get("surname").toString()
                        binding.surnameField
                            .setText(it.result?.get("surname").toString())


                        binding.emailField
                            .setText(it.result?.get("email").toString())

                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateData() {
        val name = binding.nameField.text.toString()
        val userName = binding.userNameField.text.toString()
        val surName = binding.surnameField.text.toString()
        val email = binding.emailField.text.toString()

        if (name.isNotEmpty() && userName.isNotEmpty() && surName.isNotEmpty()) {

            if (isDataChanged()) {
                val updatedData = hashMapOf<String, Any>(
                    "name" to name,
                    "userName" to userName,
                    "surname" to surName,
                )

                userViewModel.updateUserData(updatedData)
                userViewModel.userDataFlow.observe(viewLifecycleOwner) {
                    it?.let {
                        when (it) {
                            is Resource.Failure -> {
                                Log.e("Error", "Fallo al intentar actualiza los datos")

                                Toast.makeText(
                                    activity, "Fallo al intentar actualiza los datos",
                                    Toast.LENGTH_SHORT).show()
                            }
                            is Resource.Success -> {
                                Toast.makeText(
                                    activity, "Los datos fueron actualizados con éxito ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
                }

            }

            else {
                Toast.makeText(
                    activity, "No se ha detectado ningún cambio",
                    Toast.LENGTH_SHORT).show()
            }

        }

        else {
            Toast.makeText(
                activity, "Tiene que rellenar todos los campos",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDataChanged(): Boolean {

        return binding.nameField.text.toString() != currentName ||
                binding.userNameField.text.toString() != currentUserName ||
                binding.surnameField.text.toString() != currentSurname
    }


    companion object {
        @JvmStatic fun newInstance() = UserProfileFragment()
    }
}