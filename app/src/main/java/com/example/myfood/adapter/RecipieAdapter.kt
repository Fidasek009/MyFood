package com.example.myfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.model.Recipe
import android.view.View
import android.widget.ImageView
import android.widget.TextView


// bind views to variables
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recipeName: TextView = itemView.findViewById(R.id.recipeName)
    val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    val recipeIngredients: TextView = itemView.findViewById(R.id.recipeIngredients)
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
        // TODO: display ingredients without overflowing
        holder.recipeIngredients.text = recipe.ingredients.joinToString("\n")
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}