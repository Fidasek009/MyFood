package com.example.myfood.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream


const val IMAGE_DIR = "images"

// global database
lateinit var database: Database


class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Create tables and initial data if needed
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(RECIPES_TABLE)
        db.execSQL(INGREDIENTS_TABLE)
        db.execSQL(RECIPE_INGREDIENTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades (if needed)
    }

    companion object {
        private const val DATABASE_NAME = "my_food.db"
        private const val DATABASE_VERSION = 1

        private const val RECIPES_TABLE = """
            CREATE TABLE recipes (
                id INTEGER PRIMARY KEY,
                name TEXT,
                instructions TEXT
            );
        """

        private const val INGREDIENTS_TABLE = """
            CREATE TABLE ingredients (
                id INTEGER PRIMARY KEY,
                name TEXT,
                pack_size INTEGER,
                amount INTEGER,
                unit TEXT
            );
        """

        private const val RECIPE_INGREDIENTS_TABLE = """
            CREATE TABLE recipe_ingredients (
                recipe_id INTEGER,
                ingredient_id INTEGER,
                amount INTEGER,
                unit TEXT,
                FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
            );
        """
    }
}


class Database {
    private var storagePath: String
    private var images: Images
    private var db: SQLiteDatabase

    constructor(context: Context) {
        this.storagePath = context.filesDir.absolutePath

        this.images = Images(storagePath)
        this.db = MyDatabaseHelper(context).writableDatabase
    }

    // ------------------- INSERTIONS -------------------
    fun newIngredient(name: String, amount: Int, unit: String) {
        val ingredientValues = ContentValues().apply {
            put("name", name)
            put("pack_size", amount)
            put("amount", amount)
            put("unit", unit)
        }

        db.insert("ingredients", null, ingredientValues)
    }

    fun newRecipe(name: String, image: String, ingredients: List<RecipeIngredient>, instructions: String) {
        val recipeValues = ContentValues().apply {
            put("name", name)
            put("instructions", instructions)
        }

        val recipeId = db.insert("recipes", null, recipeValues)

        // save ingredients
        for (ingredient in ingredients) {
            val ingredientValues = ContentValues().apply {
                put("recipe_id", recipeId)
                put("ingredient_id", ingredient.id)
                put("amount", ingredient.amount)
                put("unit", ingredient.unit)
            }

            db.insert("recipe_ingredients", null, ingredientValues)
        }

        // TODO: save image
    }

    // ------------------- SELECTIONS -------------------
    fun getIngredient(id: String): Ingredient {
        val cursor = db.query(
            "ingredients",
            arrayOf("id", "name", "pack_size", "amount", "unit"),
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Ingredient not found")
        }

        val ingredient = Ingredient(
            cursor.getInt(0).toString(),
            cursor.getString(1),
            cursor.getInt(2),
            cursor.getInt(3),
            cursor.getString(4)
        )
        cursor.close()
        return ingredient
    }


    fun getRecipe(id: String): Recipe {
        // recipe info
        val cursor = db.query(
            "recipes",
            arrayOf("id", "name", "instructions"),
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Recipe not found")
        }

        // fetch ingredients
        return Recipe(
            cursor.getInt(0).toString(),
            cursor.getString(1),
            getRecipeIngredients(id),
            cursor.getString(2)
        )
    }


    private fun getRecipeIngredients(recipeId: String): List<RecipeIngredient> {
        val cursor = db.rawQuery(
            """
            SELECT ig.id, ig.name, ri.amount, ri.unit
            FROM recipe_ingredients ri
            INNER JOIN ingredients ig ON ri.ingredient_id = ig.id
            WHERE ri.recipe_id = ?
            """.trimIndent(),
            arrayOf(recipeId)
        )

        val ingredients = mutableListOf<RecipeIngredient>()
        while (cursor.moveToNext()) {
            ingredients.add(
                RecipeIngredient(
                    cursor.getInt(0).toString(),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3)
                )
            )
        }

        cursor.close()
        return ingredients
    }


    fun getIngredients(): List<Ingredient> {
        val cursor = db.query(
            "ingredients",
            arrayOf("id", "name", "pack_size", "amount", "unit"),
            null,
            null,
            null,
            null,
            null
        )

        val ingredients = mutableListOf<Ingredient>()
        while (cursor.moveToNext()) {
            ingredients.add(
                Ingredient(
                    cursor.getInt(0).toString(),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4)
                )
            )
        }

        cursor.close()
        return ingredients
    }


    fun getRecipes(): List<Recipe> {
        val cursor = db.query(
            "recipes",
            arrayOf("id", "name", "instructions"),
            null,
            null,
            null,
            null,
            null
        )

        val recipes = mutableListOf<Recipe>()
        while (cursor.moveToNext()) {
            recipes.add(
                Recipe(
                    cursor.getInt(0).toString(),
                    cursor.getString(1),
                    getRecipeIngredients(cursor.getInt(0).toString()), // TODO: try .getString(0)
                    cursor.getString(2)
                )
            )
        }

        cursor.close()
        return recipes
    }


    fun getAvailableRecipes(): List<Recipe> {
        val cursor = db.rawQuery(
            """
            SELECT r.id, r.name, r.instructions, COUNT(CASE WHEN ig.amount >= ri.amount THEN 1 END) have, COUNT(*) need
            FROM recipes r
            INNER JOIN recipe_ingredients ri ON r.id = ri.recipe_id
            INNER JOIN ingredients ig ON ri.ingredient_id = ig.id
            GROUP BY recipe_id HAVING need = have
            """.trimIndent(),
            null
        )

        val recipes = mutableListOf<Recipe>()
        while (cursor.moveToNext()) {
            recipes.add(
                Recipe(
                    cursor.getInt(0).toString(),
                    cursor.getString(1),
                    getRecipeIngredients(cursor.getInt(0).toString()),
                    cursor.getString(2)
                )
            )
        }

        cursor.close()
        return recipes

    }

    // ------------------- UPDATES -------------------
    fun addIngredientAmount(id: String) {
        db.execSQL(
            """
            UPDATE ingredients
            SET amount = amount + pack_size
            WHERE id = ?
            """.trimMargin(),
            arrayOf(id)
        )
    }

    fun removeIngredientAmount(id: String) {
        db.execSQL(
            """
            UPDATE ingredients
            SET amount = amount - pack_size
            WHERE id = ? AND amount >= pack_size
            """.trimMargin(),
            arrayOf(id)
        )
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
