package com.example.behealthy.view.currentUser

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.behealthy.adapter.CardViewAdapter
import com.example.behealthy.databinding.FragmentSavedRecipesBinding
import com.example.behealthy.model.data.LocalUser
import com.example.behealthy.model.data.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


class SavedRecipesFragment : Fragment() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var recipesIds: ArrayList<String>
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var myAdapter: CardViewAdapter
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()


    private var _binding: FragmentSavedRecipesBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SavedRecipesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding = FragmentSavedRecipesBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = binding.recycleRecipes
        recipeRecyclerView.layoutManager = LinearLayoutManager(activity)
        recipeRecyclerView.setHasFixedSize(true)

        recipeArrayList = arrayListOf()

        recipesIds = arrayListOf()

        myAdapter = CardViewAdapter(recipeArrayList, recipesIds, "savedRecipesFragment")

        myAdapter.ownRecipes(false, true)

        recipeRecyclerView.adapter = myAdapter

    }

    override fun onResume() {
        super.onResume()

        myAdapter.clear()
        eventChangeListener()
    }

    fun eventChangeListener() {

        val docRef2 = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
        docRef2.get().addOnSuccessListener { documentSnapshot ->
            val usuario2: LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!

            db = FirebaseFirestore.getInstance()
            db.collection("recipesData").addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("FirestoreError", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED && (usuario2.saveRecipes?.contains(
                                dc.document.id
                            ) == true)
                        ) {
                            //Log.e("lista", dc.document.toString())
                            recipeArrayList.add(dc.document.toObject(Recipe::class.java))
                            recipesIds.add(dc.document.id)
                        }
                    }
                    myAdapter.init()
                    myAdapter.notifyDataSetChanged()
                }

            })

        }
    }
}