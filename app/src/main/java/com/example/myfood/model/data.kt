package com.example.myfood.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


const val INGREDIENT_FILE = "ingredients.json"
const val RECIPE_FILE = "recipes.json"
const val IMAGE_DIR = "images"

// global database
lateinit var database: Database

class Database {
    var ingredients: MutableMap<String, Ingredient>
    var recipes: MutableMap<String, Recipe>
    var storagePath: String
    var images: Images

    constructor(context: Context) {
        this.storagePath = context.filesDir.absolutePath
        this.images = Images(storagePath)

        val gson = Gson()

        // read ingredients from file
        val ingredientFile = File(storagePath, INGREDIENT_FILE)
        val ingredientsType = object : TypeToken<MutableMap<String, Ingredient>>() {}.type

        if (!ingredientFile.exists()) {
            ingredientFile.createNewFile()
            this.ingredients = mutableMapOf()
        } else {
            val ingredientJson = ingredientFile.readText(Charsets.UTF_8)

            // empty file
            if (ingredientJson == "") {
                this.ingredients = mutableMapOf()
            } else {
                this.ingredients = gson.fromJson(ingredientJson, ingredientsType)
            }
        }

        // read recipes from file
        val recipeFile = File(storagePath, RECIPE_FILE)
        val recipesType = object : TypeToken<MutableMap<String, Recipe>>() {}.type

        if (!recipeFile.exists()) {
            recipeFile.createNewFile()
            this.recipes = mutableMapOf()
        } else {
            val recipeJson = recipeFile.readText(Charsets.UTF_8)

            // empty file
            if (recipeJson == "") {
                this.recipes = mutableMapOf()
            } else {
                this.recipes = gson.fromJson(recipeJson, recipesType)
            }
        }
    }

    fun writeIngredients() {
        val gson = Gson()
        val ingredientFile = File(storagePath, INGREDIENT_FILE)
        val ingredientJson = gson.toJson(ingredients)
        ingredientFile.writeText(ingredientJson, Charsets.UTF_8)
    }

    fun getIngredientNames(): List<String> {
        return ingredients.values.map { it.name }
    }

    fun writeRecipes() {
        val gson = Gson()
        val recipeFile = File(storagePath, RECIPE_FILE)
        val recipeJson = gson.toJson(recipes)
        recipeFile.writeText(recipeJson, Charsets.UTF_8)
    }

    fun newIngredient(name: String, amount: Int, unit: String) {
        val id = UUID.randomUUID().toString()
        val ingredient = Ingredient(name, amount, unit)
        ingredients[id] = ingredient
        writeIngredients()
    }

    fun newRecipe(name: String, image: String, ingredients: List<Ingredient>, instructions: String) {
        val id = UUID.randomUUID().toString()
        val recipe = Recipe(name, image, ingredients, instructions)
        recipes[id] = recipe
        writeRecipes()
    }

    fun getIngredient(id: String): Ingredient? {
        return ingredients[id]
    }

    fun getRecipe(id: String): Recipe? {
        return recipes[id]
    }

    fun getIngredients(): List<Ingredient> {
        return ingredients.values.toList()
    }

    fun getRecipes(): List<Recipe> {
        return recipes.values.toList()
    }
}


class Images {
    var storagePath: String
    constructor(storagePath: String) {
        this.storagePath = storagePath

        // create directory if it doesn't exist
        val imageDir = File(storagePath, IMAGE_DIR)
        if (!imageDir.exists() && !imageDir.isDirectory) {
            imageDir.mkdir()
        }
    }

    fun getImage(id: String): Bitmap? {
        val imageFile = File(storagePath, "$IMAGE_DIR/$id")
        if (!imageFile.exists()) {
            return null
        }
        return BitmapFactory.decodeFile(imageFile.absolutePath)
    }

    fun saveImage(id: String, image: Bitmap) {
        val imageFile = File(storagePath, "$IMAGE_DIR/$id")
        val fileOutputStream = FileOutputStream(imageFile)
        image.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
        fileOutputStream.close()
    }
}
