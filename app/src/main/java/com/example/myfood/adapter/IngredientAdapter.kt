package com.example.myfood.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.model.Ingredient
import com.example.myfood.model.database


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

        renderIngredient(holder, ingredient)

        holder.itemView.setOnLongClickListener {
            // TODO: delete/edit ingredient (open some menu?)
            println("LONG HOLD")
            true
        }

        holder.addIngredient.setOnClickListener {
            database.addIngredientAmount(ingredient.id)
            val ig = database.getIngredient(ingredient.id)
            renderIngredient(holder, ig)
        }

        holder.removeIngredient.setOnClickListener {
            database.removeIngredientAmount(ingredient.id)
            val ig = database.getIngredient(ingredient.id)
            renderIngredient(holder, ig)
        }
    }

    private fun renderIngredient(holder: IngredientViewHolder, ingredient: Ingredient) {
        holder.ingredientName.text = ingredient.name
        holder.ingredientAmount.text = "${ingredient.amount} ${ingredient.unit}"

        holder.removeIngredient.isEnabled = ingredient.amount != 0
        if (ingredient.amount == 0) {
            holder.ingredientName.setTextColor(Color.parseColor("#FF0000"))
        } else {
            holder.ingredientName.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}