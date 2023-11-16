package com.example.myfood.adapter

import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.model.RecipeIngredient
import com.example.myfood.model.database

class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ingredientCheckBox: CheckBox = itemView.findViewById<CheckBox>(R.id.ingredientCheckBox)
    val deleteButton: ImageButton = itemView.findViewById<ImageButton>(R.id.deleteFromList)
}

class ShoppingListAdapter(private var ingredients: MutableList<RecipeIngredient>) : RecyclerView.Adapter<ShoppingListViewHolder>() {
    private val uncheckedIngredients = mutableListOf<RecipeIngredient>()
    private val checkedIngredients = mutableListOf<RecipeIngredient>()
    private var animationInProgress = false

    init {
        splitLists()
        combineLists()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_list_layout, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return ingredients[position].id.toLong()
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val ingredient = ingredients[position]

        holder.ingredientCheckBox.text = ingredient.name
        holder.ingredientCheckBox.isChecked = ingredient.amount == 1

        // set text color
        if (holder.ingredientCheckBox.isChecked) {
            holder.ingredientCheckBox.setTextColor(holder.ingredientCheckBox.context.getColor(R.color.dark_white))
        } else {
            holder.ingredientCheckBox.setTextColor(holder.ingredientCheckBox.context.getColor(R.color.white))
        }

        // delete ingredient on click
        holder.deleteButton.setOnClickListener {
            ingredients.removeAt(position)
            database.deleteShoppingListItem(ingredient.id)
            splitLists()

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, ingredients.size)
        }

        // FIXME: dupe glitch
        holder.ingredientCheckBox.setOnClickListener {
            if (!animationInProgress) {
                if (holder.ingredientCheckBox.isChecked) {
                    checkIngredient(ingredient, true)
                } else {
                    checkIngredient(ingredient, false)
                }

                combineLists()
                moveItem(position, ingredients.indexOf(ingredient))
            }
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    private fun checkIngredient(ingredient: RecipeIngredient, checked: Boolean) {
        if (checked) {
            ingredient.amount = 1
            checkedIngredients.add(ingredient)
            uncheckedIngredients.remove(ingredient)
        } else {
            ingredient.amount = 0
            uncheckedIngredients.add(ingredient)
            checkedIngredients.remove(ingredient)
        }

        database.checkShoppingListItem(ingredient.id, checked)
    }

    private fun splitLists(){
        uncheckedIngredients.clear()
        checkedIngredients.clear()

        for (ingredient in ingredients){
            if (ingredient.amount == 1)
                checkedIngredients.add(ingredient)
            else
                uncheckedIngredients.add(ingredient)
        }
    }

    private fun combineLists() {
        ingredients = mutableListOf<RecipeIngredient>().apply {
            addAll(uncheckedIngredients)
            addAll(checkedIngredients)
        }
    }

    fun setAnimationInProgress(inProgress: Boolean) {
        animationInProgress = inProgress
    }

    private fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return notifyItemChanged(fromPosition)

        setAnimationInProgress(true)
        notifyItemMoved(fromPosition, toPosition)

        if (toPosition < fromPosition)
            notifyItemRangeChanged(toPosition, ingredients.size)
        else
            notifyItemRangeChanged(fromPosition, ingredients.size)

        android.os.Handler(Looper.getMainLooper()).post {
            setAnimationInProgress(false)
        }
    }
}