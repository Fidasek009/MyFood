package com.example.myfood.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.ViewRecipe
import com.example.myfood.model.Recipe


// bind views to variables
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recipeName: TextView = itemView.findViewById(R.id.recipeName)
    val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    val recipeIngredients: RecyclerView = itemView.findViewById(R.id.recipeIngredients)
}


// display recipes in a RecyclerView
class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_layout, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.recipeName.text = recipe.name
        // TODO: set recipe image

        // ingredients
        holder.recipeIngredients.layoutManager = GridLayoutManager(holder.itemView.context, 3, LinearLayoutManager.HORIZONTAL,false)
        holder.recipeIngredients.adapter = ItemAdapter(recipe.ingredients)

        // open recipe on click
        holder.itemView.setOnClickListener {
            // get current recipe
            val recipeId = recipes[position].id

            // get context (to open new activity)
            val context = holder.itemView.context
            val intent = Intent(context, ViewRecipe::class.java)

            // pass recipe to activity
            intent.putExtra("recipeId", recipeId)

            // open new activity
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}