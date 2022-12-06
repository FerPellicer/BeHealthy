package com.example.behealthy.view.currentUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.behealthy.databinding.FragmentUserRecipesBinding
import com.example.behealthy.viewModel.UserViewModel


class UserRecipesFragment : Fragment() {

    private var _binding: FragmentUserRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentUserRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserRecipesFragment()
    }

}