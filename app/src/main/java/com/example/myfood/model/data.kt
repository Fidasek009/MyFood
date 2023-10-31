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
const val INGREDIENT_STORAGE = "storage.txt"
const val IMAGE_DIR = "images"

// global database
lateinit var database: Database

class Database {
    private var ingredients: MutableMap<String, Ingredient>
    private var recipes: MutableMap<String, Recipe>
    private var storagePath: String
    private var images: Images

    constructor(context: Context) {
        this.storagePath = context.filesDir.absolutePath
        this.images = Images(storagePath)

        // read files
        this.ingredients = readIngredients()
        this.recipes = readRecipes()
    }

    // ------------------- FILE OPS -------------------
    private fun readIngredients(): MutableMap<String, Ingredient>{
        val gson = Gson()
        val ingredientFile = File(storagePath, INGREDIENT_FILE)
        val ingredientsType = object : TypeToken<MutableMap<String, Ingredient>>() {}.type

        // file doesn't exist (create it)
        if (!ingredientFile.exists()) {
            ingredientFile.createNewFile()
            return mutableMapOf()
        }

        // read file
        val ingredientJson = ingredientFile.readText(Charsets.UTF_8)

        // empty file
        if (ingredientJson == "") return mutableMapOf()

        return gson.fromJson(ingredientJson, ingredientsType)
    }

    private fun writeIngredients() {
        val gson = Gson()
        val ingredientFile = File(storagePath, INGREDIENT_FILE)
        val ingredientJson = gson.toJson(ingredients)
        ingredientFile.writeText(ingredientJson, Charsets.UTF_8)
    }

    private  fun readRecipes(): MutableMap<String, Recipe> {
        val gson = Gson()
        val recipeFile = File(storagePath, RECIPE_FILE)
        val recipesType = object : TypeToken<MutableMap<String, Recipe>>() {}.type

        // file doesn't exist (create it)
        if (!recipeFile.exists()) {
            recipeFile.createNewFile()
            return mutableMapOf()
        }

        // read file
        val recipeJson = recipeFile.readText(Charsets.UTF_8)

        // empty file
        if (recipeJson == "") return mutableMapOf()

        return gson.fromJson(recipeJson, recipesType)
    }

    private fun writeRecipes() {
        val gson = Gson()
        val recipeFile = File(storagePath, RECIPE_FILE)
        val recipeJson = gson.toJson(recipes)
        recipeFile.writeText(recipeJson, Charsets.UTF_8)
    }

    // ------------------- DATABASE OPS -------------------
    fun newIngredient(name: String, amount: Int, unit: String) {
        val id = UUID.randomUUID().toString()
        val ingredient = Ingredient(name, amount, amount, unit)
        ingredients[id] = ingredient
        writeIngredients()
    }

    fun newRecipe(name: String, image: String, ingredients: List<RecipeIngredient>, instructions: String) {
        val id = UUID.randomUUID().toString()
        val recipe = Recipe(name, image, ingredients, instructions)
        recipes[id] = recipe
        writeRecipes()
    }

    // ------------------- GETTERS -------------------
    fun getIngredient(id: String): Ingredient? {
        return ingredients[id]
    }

    fun getRecipe(id: String): Recipe? {
        return recipes[id]
    }

    fun getIngredientNames(): List<String> {
        return ingredients.values.map { it.name }
    }

    fun getIngredients(): List<Pair<String, Ingredient>> {
        return ingredients.toList()
    }

    fun getRecipes(): List<Pair<String, Recipe>> {
        return recipes.toList()
    }

    fun getAvailableIngredients(): Set<String> {
        return ingredients.filter { it.value.amount > 0 }.keys
    }

    fun getAvailableRecipes(): List<Pair<String, Recipe>> {
        val availableIngredients = getAvailableIngredients()
        return recipes.filter { it.value.ingredients.all { availableIngredients.contains(it.id) } }.toList()
    }

    // ------------------- SETTERS -------------------
    fun addIngredientAmount(id: String) {
        val ingredient = ingredients[id]
        if (ingredient != null) {
            ingredient.amount += ingredient.packSize
            writeIngredients()
        }
    }

    fun removeIngredientAmount(id: String) {
        val ingredient = ingredients[id]
        if (ingredient != null && 0 < ingredient.amount) {
            ingredient.amount -= ingredient.packSize
            writeIngredients()
        }
    }
}


class Images {
    private val storagePath: String

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
