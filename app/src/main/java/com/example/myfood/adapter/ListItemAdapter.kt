package com.example.myfood.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.model.RecipeIngredient
import com.example.myfood.model.database


class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val listItemText: TextView = itemView.findViewById(R.id.recipeIngredientListItem)
}

class ItemAdapter(private val ingredients: List<RecipeIngredient>) : RecyclerView.Adapter<ListItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.listItemText.text = "• ${ingredient.name} (${ingredient.amount} ${ingredient.unit})  "

        // Set text color to red if ingredient is missing
        val amount = database.getIngredient(ingredient.id)?.amount
        if (amount == null || amount < ingredient.amount)
            holder.listItemText.setTextColor(Color.RED)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
