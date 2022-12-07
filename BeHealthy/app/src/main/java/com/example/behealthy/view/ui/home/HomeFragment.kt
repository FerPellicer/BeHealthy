package com.example.behealthy.view.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.behealthy.adapter.CardViewAdapter
import com.example.behealthy.databinding.FragmentHomeBinding
import com.example.behealthy.model.Recipe
import com.google.firebase.firestore.*

open class HomeFragment : Fragment(), SearchView.OnQueryTextListener{

    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var recipesIds: ArrayList<String>
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var svSearch : SearchView
    private lateinit var myAdapter: CardViewAdapter
    private lateinit var db : FirebaseFirestore


    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = binding.recycleRecipes
        recipeRecyclerView.layoutManager = LinearLayoutManager(activity)
        recipeRecyclerView.setHasFixedSize(true)

        svSearch = binding.txtBuscar

        svSearch.setOnQueryTextListener(this)

        recipeArrayList = arrayListOf()

        recipesIds = arrayListOf()

        myAdapter = CardViewAdapter(recipeArrayList, recipesIds)

        recipeRecyclerView.adapter = myAdapter

        EventChangeListener()

    }

    fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("recipesData").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    Log.e("FirestoreError", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if(dc.type == DocumentChange.Type.ADDED){
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

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        myAdapter.filter(newText)
        return false
    }


}