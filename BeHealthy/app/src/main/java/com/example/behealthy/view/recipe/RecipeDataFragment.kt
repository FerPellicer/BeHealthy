package com.example.behealthy.view.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.behealthy.databinding.FragmentRecipeDataBinding
import com.example.behealthy.model.data.LocalUser
import com.example.behealthy.model.data.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class RecipeDataFragment : Fragment() {
    private var _binding: FragmentRecipeDataBinding? = null
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecipeDataBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipe = arguments?.getSerializable("itemInfo") as Recipe


        val docRef = recipe.user?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            //Log.d("document", documentSnapshot.toString())
            val usuario: LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!
            //usuario.name?.let { Log.d("usuario1", it) }

            binding.userName.text = "@" + usuario.userName
            Glide.with(requireContext()).asBitmap().load(usuario.imageProfile)
                .into(binding.userImage)
        }

        Glide.with(requireContext()).asBitmap().load(recipe.image).into(binding.recipeImage)
        binding.name.text = recipe.name
        binding.descriptionText.text = recipe.description
        binding.ingredientsText.text = recipe.ingredients
        binding.stepsText.text = recipe.steps
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = RecipeDataFragment()

    }
}