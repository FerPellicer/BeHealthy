package com.example.behealthy.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.model.data.LocalUser
import com.example.behealthy.model.data.Recipe
import com.example.behealthy.view.recipe.SavedRecipesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("UNCHECKED_CAST")
class CardViewAdapter(
    recipeListParam: ArrayList<Recipe>,
    private var recipesIds: ArrayList<String>,

): RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var recipeList: ArrayList<Recipe> = ArrayList()
    private var original: ArrayList<Recipe> = recipeListParam
    private var listLikes: ArrayList<String> = ArrayList()
    private var listSaves: ArrayList<String> = ArrayList()
    private var searchText: String = ""
    private var searchActive: Boolean = false
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var ownRecipes : Boolean = false
    private var saveRecipes : Boolean = false
    private var auxList : ArrayList<String> = ArrayList()
    private var auxList1 : ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recipe_element, viewGroup, false)

        return ViewHolder(itemView)

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val currentItem = recipeList[i]

        Log.e("own", this.ownRecipes.toString())

        if(this.ownRecipes){

            viewHolder.itemDelete.visibility = View.VISIBLE;
            viewHolder.itemModify.visibility = View.VISIBLE;

            viewHolder.itemDelete.setOnClickListener {

                db.collection("recipesData").document(recipesIds[original.indexOf(currentItem)]).delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                removeItem(i)
            }

            viewHolder.itemModify.setOnClickListener {
                val docRef = currentItem.user?.let { db.collection("recipesData").document(recipesIds[original.indexOf(currentItem)]) }
                docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                    Log.d("document", documentSnapshot.toString())
                    val recipe : Recipe = documentSnapshot.toObject(Recipe::class.java)!!

                }
            }

        }else{

            viewHolder.itemDelete.visibility = View.INVISIBLE;
            viewHolder.itemModify.visibility = View.INVISIBLE;

        }

        val context = viewHolder.itemView.context

        Glide.with(context).asBitmap().load(currentItem.image).into(viewHolder.itemImage)
        viewHolder.itemName.text = currentItem.name
        viewHolder.itemLikes.text = currentItem.likes + " likes"

        val docRef = currentItem.user?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            Log.d("document", documentSnapshot.toString())
            val usuario : LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!
            usuario.name?.let { Log.d("usuario1", it) }

            viewHolder.itemUserName.text = usuario.userName
            Glide.with(context).asBitmap().load(usuario.imageProfile).into(viewHolder.itemUserImage)


        }

        val docRef2 = currentItem.user?.let { db.collection("users").document(firebaseAuth.currentUser?.uid.toString()) }
        docRef2?.get()?.addOnSuccessListener { documentSnapshot ->
            Log.d("document", documentSnapshot.toString())
            val usuario2: LocalUser = documentSnapshot.toObject(LocalUser::class.java)!!

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
                    "likesUser" to usuario2.likesUser
                )

                val recipe = hashMapOf(
                    "likes" to currentItem.likes
                )

                db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).update(
                    user as Map<String, Any>
                )

                db.collection("recipesData").document(recipesIds[original.indexOf(currentItem)]).update(
                    recipe as Map<String, Any>
                )

                searchActive = true

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

                    if(saveRecipes){
                        removeItem(i)
                        viewHolder.itemSave.setImageDrawable(getDrawable(context, R.drawable.save_icon_black))
                    }

                }else{
                    viewHolder.itemSave.setImageDrawable(getDrawable(context, R.drawable.save_icon_black))
                    listSaves.add(recipesIds[original.indexOf(currentItem)])
                    usuario2.saveRecipes = listSaves
                }

                val user = hashMapOf(
                    "saveRecipes" to usuario2.saveRecipes
                )

                db.collection("users").document(firebaseAuth.currentUser?.uid.toString()).update(
                    user as Map<String, Any>
                )
            }

            if(searchActive) {
                filter(searchText)
                searchActive = false
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
        val itemDelete: ImageView = itemView.findViewById(R.id.delete)
        val itemModify: ImageView = itemView.findViewById(R.id.modify)
    }

    fun init() {
        recipeList = original.clone() as ArrayList<Recipe>
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(strSearch: String) {

        if (this.searchText.isEmpty()) {
            recipeList.clear()
            recipeList.addAll(original)
            searchText = ""
        } else {
            searchText = strSearch
            recipeList.clear()
            for (recipe in original) {
                if (recipe.name!!.lowercase().contains(strSearch)) {
                    recipeList.add(recipe)
                }
            }

        }

        notifyDataSetChanged()
    }

    fun initSearch (newText: String) {
        this.searchText = newText
    }

    fun ownRecipes(option1 : Boolean, option2 : Boolean){
        this.ownRecipes = option1
        this.saveRecipes = option2
    }

    fun clear() {
        original.clear()
        recipeList.clear()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        recipeList.removeAt(position)
        original.removeAt(position)
        notifyDataSetChanged()
    }


}