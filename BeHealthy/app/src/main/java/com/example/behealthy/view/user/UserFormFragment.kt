package com.example.behealthy.view.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.behealthy.R
import com.example.behealthy.databinding.FragmentUserFormBinding

class UserFormFragment : Fragment() {


    private var _binding: com.example.behealthy.databinding.FragmentUserFormBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserFormBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.setOnClickListener { login() }
        binding.register.setOnClickListener { register() }
    }

    private fun register() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, RegisterFragment.newInstance())
            ?.commitNow()
    }

    private fun login() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, LoginFragment.newInstance())
            ?.commitNow()
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFormFragment()
    }
}