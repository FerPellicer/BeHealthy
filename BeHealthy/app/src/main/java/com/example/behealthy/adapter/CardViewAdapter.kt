package com.example.behealthy.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.model.Recipe
import com.example.behealthy.model.LocalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.awaitAll

class CardViewAdapter(recipeListParam: ArrayList<Recipe>, recipesIds: ArrayList<String>): RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var recipeList: ArrayList<Recipe> = ArrayList()
    private var original: ArrayList<Recipe> = recipeListParam
    private var recipesIds: ArrayList<String> = recipesIds
    private var listLikes: ArrayList<String> = ArrayList()
    private var listSaves: ArrayList<String> = ArrayList()
    private lateinit var usuario : User
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recipe_element, viewGroup, false)

        return ViewHolder(itemView)

    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val currentItem = recipeList[i]

        val context = viewHolder.itemView.context

        Glide.with(context).asBitmap().load(currentItem.image).into(viewHolder.itemImage)
        viewHolder.itemName.text = currentItem.name
        viewHolder.itemLikes.text = currentItem.likes + " likes"

        val docRef = currentItem.user?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            Log.d("document", documentSnapshot.toString())
            var usuario : LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!
            usuario.name?.let { Log.d("usuario1", it) }

            viewHolder.itemUserName.text = usuario.userName
            Glide.with(context).asBitmap().load(usuario.imageProfile).into(viewHolder.itemUserImage)


        }

        val docRef2 = currentItem.user?.let { db.collection("users").document(firebaseAuth.currentUser?.uid.toString()) }
        docRef2?.get()?.addOnSuccessListener { documentSnapshot ->
            Log.d("document", documentSnapshot.toString())
            var usuario2: LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!

            if((usuario2.likesUser?.contains(recipesIds[original.indexOf(currentItem)]) == true)){
                viewHolder.itemHeart.setImageDrawable(getDrawable(context, R.drawable.heart_icon_red))
            }else{
                viewHolder.itemHeart.setImageDrawable(getDrawable(context, R.drawable.heart_icon))
            }

            listLikes = usuario2.likesUser!!

            viewHolder.itemHeart.setOnClickListener {

                if(listLikes.contains(recipesIds[original.indexOf(currentItem)])){
                    viewHolder.itemHeart.setImageDrawable(getDrawable(context, R.drawable.heart_icon))
                    listLikes.remove(recipesIds[original.indexOf(currentItem)])
                    usuario2.likesUser = listLikes
                    currentItem.likes = ((currentItem.likes?.toInt())?.minus(1)).toString()
                }else{
                    viewHolder.itemHeart.setImageDrawable(getDrawable(context, R.drawable.heart_icon_red))
                    listLikes.add(recipesIds[original.indexOf(currentItem)])
                    usuario2.likesUser = listLikes
                    currentItem.likes = ((currentItem.likes?.toInt())?.plus(1)).toString()
                }

                viewHolder.itemLikes.text = currentItem.likes + " likes"

                val user = hashMapOf(
                    "email" to usuario2.email,
                    "imageProfile" to usuario2.imageProfile,
                    "likesUser" to usuario2.likesUser,
                    "name" to usuario2.name,
                    "saveRecipes" to usuario2.saveRecipes,
                    "surname" to usuario2.surname,
                    "userName" to usuario2.surname
                )

                val recipe = hashMapOf(
                    "name" to currentItem.name,
                    "description" to currentItem.description,
                    "ingredients" to currentItem.ingredients,
                    "image" to currentItem.image,
                    "steps" to currentItem.steps,
                    "likes" to currentItem.likes,
                    "user" to currentItem.user
                )

                db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).set(user)

                db.collection("recipesData").document(recipesIds[original.indexOf(currentItem)]).set(recipe)
            }

            if((usuario2.saveRecipes?.contains(recipesIds[original.indexOf(currentItem)]) == true)){
                viewHolder.itemSave.setImageDrawable(getDrawable(context, R.drawable.save_icon_black))
            }else{
                viewHolder.itemSave.setImageDrawable(getDrawable(context, R.drawable.save_icon))
            }

            listSaves = usuario2.saveRecipes as ArrayList<String>

            viewHolder.itemSave.setOnClickListener {

                if(listSaves.contains(recipesIds[original.indexOf(currentItem)])){
                    viewHolder.itemSave.setImageDrawable(getDrawable(context, R.drawable.save_icon))
                    listSaves.remove(recipesIds[original.indexOf(currentItem)])
                    usuario2.saveRecipes = listSaves
                }else{
                    viewHolder.itemSave.setImageDrawable(getDrawable(context, R.drawable.save_icon_black))
                    listSaves.add(recipesIds[original.indexOf(currentItem)])
                    usuario2.saveRecipes = listSaves
                }

                val user = hashMapOf(
                    "email" to usuario2.email,
                    "imageProfile" to usuario2.imageProfile,
                    "likesUser" to usuario2.likesUser,
                    "name" to usuario2.name,
                    "saveRecipes" to usuario2.saveRecipes,
                    "surname" to usuario2.surname,
                    "userName" to usuario2.surname
                )

                db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).set(user)
            }



        }


        viewHolder.itemImage.setOnClickListener {
            val bundle = Bundle()

            bundle.putSerializable("itemInfo", currentItem)

            Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_recipe, bundle)
                .onClick(viewHolder.itemView)

        }

    }

    override fun getItemCount(): Int {

        return recipeList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemImage: ImageView = itemView.findViewById(R.id.recipe_image)
        val itemHeart: ImageView = itemView.findViewById(R.id.hearth)
        val itemSave: ImageView = itemView.findViewById(R.id.save)
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