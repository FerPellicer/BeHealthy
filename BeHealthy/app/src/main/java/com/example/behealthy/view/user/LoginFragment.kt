package com.example.behealthy.view.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.behealthy.R
import com.example.behealthy.databinding.FragmentLoginBinding
import com.example.behealthy.view.SlideMenuActivity
import com.example.behealthy.viewModel.AuthViewModel
import com.example.fragments.data.Resource
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {


    private val authViewModel: AuthViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.setOnClickListener { login() }
        binding.register.setOnClickListener { register() }

        authViewModel.currentUser?.let { Log.e("", it.toString()) }
    }


    private fun login() {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {

            authViewModel.loginUser(email, password)
            authViewModel.loginFlow.observe(viewLifecycleOwner) {
                it?.let {
                    when (it) {
                        is Resource.Failure -> {
                            Log.e("Error", "No se ha iniciado sesión")
                            Toast.makeText(requireActivity(), "El correo o la contraseña son incorrectos").show()
                        }
                        is Resource.Success -> {
                            Log.e("Succes", "Se ha iniciado sesión")

                            Toast.makeText(
                                activity, "Se ha iniciado correctamente",
                                Toast.LENGTH_SHORT)

                            val intent = Intent(activity, SlideMenuActivity::class.java)
                            startActivity(intent)
                            getActivity()?.finish()
                        }
                        else -> {}
                    }
                }
            }

        } else {
            Toast.makeText(
                activity,
                "Es necesario rellenar todos los campos !!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun register() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, RegisterFragment.newInstance())
            ?.commitNow()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         **/
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}