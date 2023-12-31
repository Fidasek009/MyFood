package com.example.myfood

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.myfood.model.RecipeIngredient
import com.example.myfood.model.database

class ViewRecipe : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_recipe)

        // load recipe parameters
        val recipeId = intent.getStringExtra("recipeId")
        val recipe = database.getRecipe(recipeId ?: "")

        // set image
        val img = database.getImage(recipe.id)
        if (img != null)
            findViewById<ImageView>(R.id.recipeImage).setImageBitmap(img)

        findViewById<TextView>(R.id.recipeName).text = recipe.name
        findViewById<TextView>(R.id.ingredientsList).text = ingredientString(recipe.ingredients)
        findViewById<TextView>(R.id.instructions).text = recipe.instructions

        // close button
        val closeButton = findViewById<Button>(R.id.close)
        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun ingredientString(ingredients: List<RecipeIngredient>): String {
        var s = ""

        for (ingredient in ingredients) {
            s += "• ${ingredient.name} (${ingredient.amount} ${ingredient.unit})\n"
        }

        return s
    }
}