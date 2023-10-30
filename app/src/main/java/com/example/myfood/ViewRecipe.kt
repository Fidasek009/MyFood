package com.example.myfood

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ViewRecipe : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_recipe)

        // TODO: convert to recipe ID
        // load recipe parameters
        val recipeName = intent.getStringExtra("recipeName")
        val recipeImage = intent.getStringExtra("recipeImage")
        val recipeIngredients = intent.getStringExtra("recipeIngredients")
        val instructions = intent.getStringExtra("recipeInstructions")

        // findViewById<ImageView>(R.id.recipeImage).setImageResource(//todo: load image)
        findViewById<TextView>(R.id.recipeName).text = recipeName
        findViewById<TextView>(R.id.ingredientsList).text = recipeIngredients
        findViewById<TextView>(R.id.instructions).text = instructions

        // close button
        val closeButton = findViewById<Button>(R.id.close)
        closeButton.setOnClickListener {
            finish()
        }
    }
}