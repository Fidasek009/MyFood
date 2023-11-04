package com.example.myfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.myfood.model.RecipeIngredient
import com.example.myfood.model.database

class CreateRecipe : ComponentActivity() {
    private var ingredients = mutableListOf<RecipeIngredient>()
    private var allIngredients = database.getIngredients()
    private var selectedIngredientId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_recipe)

        // load ingredients into spinner
        initializeIngredientList()

        // Cancel button
        val cancelButton = findViewById<Button>(R.id.cancelRecipe)
        cancelButton.setOnClickListener {
            finish()
        }

        // Save button
        val saveButton = findViewById<Button>(R.id.saveRecipe)
        saveButton.setOnClickListener {
            saveRecipe()
        }

        // add ingredient button
        val addIngredientButton = findViewById<Button>(R.id.addIngredientButton)
        addIngredientButton.setOnClickListener {
            addIngredient()
            renderIngredients()
        }

        // remove ingredient button
        val removeIngredientButton = findViewById<Button>(R.id.removeIngredientButton)
        removeIngredientButton.setOnClickListener {
            if (ingredients.size > 0) {
                ingredients.removeLast()
                renderIngredients()
            }
        }
    }

    private fun initializeIngredientList() {
        val ingredientList = findViewById<Spinner>(R.id.ingredientSpinner)
        val ingredientNames = getIngredientNames()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ingredientNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ingredientList.adapter = adapter

        ingredientList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedIngredient = allIngredients[position]
                selectedIngredientId = selectedIngredient.id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedIngredientId = ""
            }
        }
    }

    private fun getIngredientNames(): List<String> {
        return allIngredients.map { it.name }
    }

    private fun addIngredient() {
        val name = findViewById<Spinner>(R.id.ingredientSpinner).selectedItem.toString()
        val amount = findViewById<EditText>(R.id.ingredientAmount).text.toString()
        val unit = findViewById<EditText>(R.id.ingredientUnit).text.toString()

        // do not accept empty fields
        if (name == "" || amount == "" || unit == "") return

        val ingredient = RecipeIngredient(selectedIngredientId, name, amount.toInt(), unit)
        ingredients.add(ingredient)
    }

    private fun renderIngredients() {
        var ingredientString = ""

        for (ingredient in ingredients) {
            ingredientString += "• ${ingredient.name} ${ingredient.amount} ${ingredient.unit}\n"
        }

        findViewById<TextView>(R.id.ingredientsView).text = ingredientString
    }

    private fun saveRecipe() {
        val recipeName = findViewById<EditText>(R.id.recipeName).text.toString()
        val recipeInstructions = findViewById<EditText>(R.id.recipeInstructions).text.toString()

        if(recipeName == "" || recipeInstructions == "" || ingredients.size == 0) return

        database.newRecipe(recipeName, "img.jpg", ingredients, recipeInstructions)

        // successfully created ingredient
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

}
