package com.example.myfood.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.ViewRecipe
import com.example.myfood.model.Recipe
import com.example.myfood.model.RecipeIngredient


// bind views to variables
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recipeName: TextView = itemView.findViewById(R.id.recipeName)
    val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    val recipeIngredients: TextView = itemView.findViewById(R.id.recipeIngredients)
}


// display recipes in a RecyclerView
class RecipeAdapter(private val recipes: List<Pair<String, Recipe>>) : RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_layout, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position].second

        holder.recipeName.text = recipe.name
        // TODO: set recipe image
        holder.recipeIngredients.text = ingredientsString(recipe.ingredients)

        holder.itemView.setOnClickListener {
            // get current recipe
            val recipeId = recipes[position].first

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

    private fun ingredientsString(ingredients: List<RecipeIngredient>): String {
        val rows: MutableList<String> = mutableListOf("", "", "")
        var colWidth = 0
        var maxWidth = 0

        for(i in ingredients.indices) {
            val row = i % 3
            // create tabs to new column
            if (rows[row].length < colWidth) rows[row] += " ".repeat(colWidth - rows[row].length)

            rows[row] += "  • ${ingredients[i].name} (${ingredients[i].amount} ${ingredients[i].unit})"

            // update max width
            if (maxWidth < rows[row].length) maxWidth = rows[row].length
            if (row == 2) colWidth = maxWidth
        }

        return rows.joinToString("\n")
    }
}