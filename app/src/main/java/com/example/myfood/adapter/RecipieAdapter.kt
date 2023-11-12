package com.example.myfood.adapter

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.CreateRecipe
import com.example.myfood.R
import com.example.myfood.ViewRecipe
import com.example.myfood.model.Recipe
import com.example.myfood.model.database


// bind views to variables
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recipeName: TextView = itemView.findViewById(R.id.recipeName)
    val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    val recipeIngredients: RecyclerView = itemView.findViewById(R.id.recipeIngredients)
}


// display recipes in a RecyclerView
class RecipeAdapter(private val handler: ActivityResultLauncher<Intent>, private val recipes: MutableList<Recipe>) : RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_layout, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.recipeName.text = recipe.name

        // set image
        val img = database.getImage(recipe.id)
        if (img != null)
            holder.recipeImage.setImageBitmap(img)

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

        // popup menu
        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder, recipe, position)
            true
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    private fun showPopupMenu(holder: RecipeViewHolder, recipe: Recipe, position: Int) {
        val view = holder.itemView
        val popupMenu = PopupMenu(view.context, view, Gravity.END, 0, R.style.PopupMenuStyle)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.item_menu, popupMenu.menu)

        // Set a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(holder.itemView.context, CreateRecipe::class.java)
                    intent.putExtra("id", recipe.id)
                    handler.launch(intent)
                    true
                }
                R.id.action_delete -> {
                    recipes.removeAt(position)
                    this.notifyItemRemoved(position)
                    database.deleteRecipe(recipe.id)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}