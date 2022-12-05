package com.example.behealthy.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.model.Recipe
import com.example.behealthy.model.LocalUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject

class CardViewAdapter(recipeListParam: ArrayList<Recipe>): RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {

    private var recipeList: ArrayList<Recipe> = ArrayList()
    private var original: ArrayList<Recipe> = recipeListParam
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recipe_element, viewGroup, false)

        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val currentItem = recipeList[i]

        val context = viewHolder.itemView.context

        Glide.with(context).asBitmap().load(currentItem.image).into(viewHolder.itemImage)
        viewHolder.itemName.text = currentItem.name
        viewHolder.itemLikes.text = currentItem.likes

        val docRef = currentItem.user?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            Log.d("document", documentSnapshot.toString())
            var usuario : LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!
            usuario.name?.let { Log.d("usuario1", it) }

            viewHolder.itemUserName.text = usuario.userName
            Glide.with(context).asBitmap().load(usuario.imageProfile).into(viewHolder.itemUserImage)
        }

        viewHolder.itemImage.setOnClickListener {
            val bundle = Bundle()

            bundle.putSerializable("itemInfo", currentItem)

            Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_recipe, bundle)
                .onClick(viewHolder.itemView)

        }
    }

    override fun getItemCount(): Int {
        Log.d("originalarr", recipeList.toString())
        return recipeList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemImage: ImageView = itemView.findViewById(R.id.recipe_image)
        val itemName: TextView = itemView.findViewById(R.id.recipe_name)
        val itemUserImage: ImageView = itemView.findViewById(R.id.user_image)
        val itemLikes: TextView = itemView.findViewById(R.id.recipe_likes)
        val itemUserName: TextView = itemView.findViewById(R.id.user_name)
    }

    fun init() {
        recipeList = original.clone() as ArrayList<Recipe>
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(strSearch: String) {
        if (strSearch.isEmpty()) {
            recipeList.clear()
            recipeList.addAll(original)
        } else {
            recipeList.clear()
            for (recipe in original) {
                if (recipe.name!!.lowercase().contains(strSearch)) {
                    recipeList.add(recipe)
                }
            }

        }

        notifyDataSetChanged()
    }

}