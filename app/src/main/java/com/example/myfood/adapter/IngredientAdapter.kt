package com.example.myfood.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.CreateIngredient
import com.example.myfood.R
import com.example.myfood.model.Ingredient
import com.example.myfood.model.database


// bind views to variables
class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
    val ingredientAmount: TextView = itemView.findViewById(R.id.ingredientAmount)
    val addIngredient: ImageButton = itemView.findViewById(R.id.addIngredient)
    val removeIngredient: ImageButton = itemView.findViewById(R.id.removeIngredient)
}


// display ingredients in a RecyclerView
class IngredientAdapter(private val handler: ActivityResultLauncher<Intent>, private val ingredients: MutableList<Ingredient>) : RecyclerView.Adapter<IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_layout, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]

        renderIngredient(holder, position)

        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder, ingredient, position)
            true
        }

        holder.addIngredient.setOnClickListener {
            database.addIngredientAmount(ingredient.id)
            ingredients[position] = database.getIngredient(ingredient.id)
            notifyItemChanged(position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderIngredient(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientName.text = ingredient.name
        holder.ingredientAmount.text = "${ingredient.amount} ${ingredient.unit}"

        if (ingredient.amount == 0) {
            holder.ingredientName.setTextColor(Color.parseColor("#FF0000"))
            holder.removeIngredient.setImageResource(R.drawable.baseline_add_shopping_cart_24)

            // add to shopping list
            holder.removeIngredient.setOnClickListener {
                database.addToShoppingList(ingredient.id)
            }
        } else {
            holder.ingredientName.setTextColor(Color.parseColor("#FFFFFF"))
            holder.removeIngredient.setImageResource(R.drawable.baseline_remove_24)

            // remove ingredient
            holder.removeIngredient.setOnClickListener {
                database.removeIngredientAmount(ingredient.id)
                ingredients[position] = database.getIngredient(ingredient.id)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    private fun showPopupMenu(holder: IngredientViewHolder, ingredient: Ingredient, position: Int) {
        val view = holder.itemView
        val popupMenu = PopupMenu(view.context, view, Gravity.END, 0, R.style.PopupMenuStyle)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.item_menu, popupMenu.menu)

        // Set a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(holder.itemView.context, CreateIngredient::class.java)
                    intent.putExtra("id", ingredient.id)
                    handler.launch(intent)
                    true
                }
                R.id.action_delete -> {
                    ingredients.removeAt(position)
                    database.deleteIngredient(ingredient.id)
                    notifyItemRemoved(position)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}
