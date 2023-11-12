package com.example.myfood

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myfood.model.RecipeIngredient
import com.example.myfood.model.database

class CreateRecipe : ComponentActivity() {
    private val id by lazy { intent.getStringExtra("id") }
    private var ingredients = mutableListOf<RecipeIngredient>()
    private var allIngredients = database.getIngredients()
    private var selectedIngredientId = ""
    private lateinit var selectedBitmap: Bitmap

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

        val addImageButton = findViewById<Button>(R.id.addImageButton)
        addImageButton.setOnClickListener {
            // Open the gallery to select an image
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(galleryIntent)
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

        // load values if in edit mode
        loadValues()
    }

    private fun initializeIngredientList() {
        val ingredientList = findViewById<Spinner>(R.id.ingredientSpinner)
        val ingredientNames = getIngredientNames()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_menu, ingredientNames)
        adapter.setDropDownViewResource(R.layout.spinner_menu)
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

    private fun loadValues() {
        if (id == null) return

        val recipe = database.getRecipe(id!!)

        findViewById<EditText>(R.id.recipeName).setText(recipe.name)
        findViewById<EditText>(R.id.recipeInstructions).setText(recipe.instructions)
        ingredients = recipe.ingredients.toMutableList()
        renderIngredients()

        // load image if exists
        val bmp = database.getImage(recipe.id)
        if (bmp != null) {
            selectedBitmap = bmp
        }
    }

    private fun renderIngredients() {
        var ingredientString = ""

        for (ingredient in ingredients) {
            ingredientString += "• ${ingredient.name} ${ingredient.amount} ${ingredient.unit}\n"
        }

        findViewById<TextView>(R.id.ingredientsView).text = ingredientString
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Image selected successfully
            val imageUri = result.data?.data
            if (imageUri != null) {
                val inputStream = contentResolver.openInputStream(imageUri)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
            }
        }
    }

    private fun saveRecipe() {
        val recipeName = findViewById<EditText>(R.id.recipeName).text.toString()
        val recipeInstructions = findViewById<EditText>(R.id.recipeInstructions).text.toString()

        if(recipeName == "" || recipeInstructions == "" || ingredients.size == 0) return

        // create or edit ingredient
        if (id == null) {
            // create with image if selected
            if (this::selectedBitmap.isInitialized)
                database.newRecipe(recipeName, selectedBitmap, ingredients, recipeInstructions)
            else
                database.newRecipe(recipeName, null, ingredients, recipeInstructions)
        }
        else {
            // edit with image if selected
            if (this::selectedBitmap.isInitialized)
                database.editRecipe(id!!, recipeName, selectedBitmap, ingredients, recipeInstructions)
            else
                database.editRecipe(id!!, recipeName, null, ingredients, recipeInstructions)
        }

        // successfully created ingredient
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}
