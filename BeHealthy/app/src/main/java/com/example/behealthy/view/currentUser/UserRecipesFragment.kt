package com.example.behealthy.view.currentUser

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.behealthy.model.data.Recipe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.behealthy.adapter.CardViewAdapter
import com.example.behealthy.databinding.FragmentUserRecipesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


open class UserRecipesFragment : Fragment(){

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var recipesIds: ArrayList<String>
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var myAdapter: CardViewAdapter
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()


    private var _binding: FragmentUserRecipesBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = UserRecipesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding = FragmentUserRecipesBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = binding.recycleRecipes
        recipeRecyclerView.layoutManager = LinearLayoutManager(activity)
        recipeRecyclerView.setHasFixedSize(true)

        recipeArrayList = arrayListOf()

        recipesIds = arrayListOf()

        myAdapter = CardViewAdapter(recipeArrayList, recipesIds)

        recipeRecyclerView.adapter = myAdapter

        EventChangeListener()
    }

    fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("recipesData").addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    Log.e("FirestoreError", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){
                    val recipe : Recipe = dc.document.toObject(Recipe::class.java)
                    if(dc.type == DocumentChange.Type.ADDED && (firebaseAuth.currentUser?.uid.toString().equals(recipe.user))){
                        Log.e("lista", dc.document.toString())
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