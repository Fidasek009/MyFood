package com.example.myfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.model.Recipe
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.myfood.model.Ingredient


// bind views to variables
class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
    val ingredientAmount: TextView = itemView.findViewById(R.id.ingredientAmount)
    val addIngredient: Button = itemView.findViewById(R.id.addIngredient)
    val removeIngredient: Button = itemView.findViewById(R.id.removeIngredient)
}


// display ingredients in a RecyclerView
class IngredientAdapter(private val ingredients: List<Ingredient>) : RecyclerView.Adapter<IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_layout, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]

        holder.ingredientName.text = ingredient.name
        holder.ingredientAmount.text = ingredient.amount.toString() + " " + ingredient.unit
        // TODO: add button functionality
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}