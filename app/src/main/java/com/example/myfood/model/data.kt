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
        db.execSQL(SHOPPING_LIST_TABLE)
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
                FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
            );
        """

        private const val SHOPPING_LIST_TABLE = """
            CREATE TABLE shopping_list (
                ingredient_id INTEGER,
                checked BOOLEAN,
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
            );
        """
    }
}


class Database(context: Context) {
    private var storagePath: String
    private var db: SQLiteDatabase

    init {
        this.storagePath = context.filesDir.absolutePath
        this.db = MyDatabaseHelper(context).writableDatabase

        // create image directory if it doesn't exist
        val imageDir = File(storagePath, IMAGE_DIR)
        if (!imageDir.exists() && !imageDir.isDirectory) {
            imageDir.mkdir()
        }
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
            cursor.getString(0),
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
            cursor.getString(0),
            cursor.getString(1),
            getRecipeIngredients(id),
            cursor.getString(2)
        )
    }


    private fun getRecipeIngredients(recipeId: String): List<RecipeIngredient> {
        val cursor = db.rawQuery(
            """
            SELECT ig.id, ig.name, ri.amount, ig.unit
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
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3)
                )
            )
        }

        cursor.close()
        return ingredients
    }


    fun getIngredients(): MutableList<Ingredient> {
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
                    cursor.getString(0),
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


    fun getRecipes(): MutableList<Recipe> {
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
                    cursor.getString(0),
                    cursor.getString(1),
                    getRecipeIngredients(cursor.getString(0)),
                    cursor.getString(2)
                )
            )
        }

        cursor.close()
        return recipes
    }

    fun getAvailableRecipes(): MutableList<Recipe> {
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
                    cursor.getString(0),
                    cursor.getString(1),
                    getRecipeIngredients(cursor.getString(0)),
                    cursor.getString(2)
                )
            )
        }

        cursor.close()
        return recipes

    }

    fun getShoppingList(): MutableList<RecipeIngredient> {
        val cursor = db.rawQuery(
            """
            SELECT ig.id, ig.name, sl.checked, ig.unit
            FROM ingredients ig
            INNER JOIN shopping_list sl ON ig.id = sl.ingredient_id
            """.trimIndent(),
            null
        )

        val ingredients = mutableListOf<RecipeIngredient>()
        while (cursor.moveToNext()) {
            ingredients.add(
                RecipeIngredient(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3)
                )
            )
        }

        cursor.close()
        return ingredients
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

    fun newRecipe(name: String, image: Bitmap?, ingredients: List<RecipeIngredient>, instructions: String) {
        // save recipe
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
            }

            db.insert("recipe_ingredients", null, ingredientValues)
        }

        saveImage(recipeId.toString(), image)
    }

    fun addToShoppingList(ingredientId: String) {
        // check if ingredient is already in shopping list
        val cursor = db.rawQuery("SELECT COUNT(*) FROM shopping_list WHERE ingredient_id = ?", arrayOf(ingredientId))
        cursor.use {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) return
        }

        // add to shopping list
        val shoppingListValues = ContentValues().apply {
            put("ingredient_id", ingredientId)
            put("checked", false)
        }

        db.insert("shopping_list", null, shoppingListValues)
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

    fun checkShoppingListItem(ingredientId: String, checked: Boolean) {
        val checkedValue = ContentValues().apply { put("checked", checked) }
        db.update("shopping_list", checkedValue, "ingredient_id = ?", arrayOf(ingredientId))
    }

    fun editIngredient(id: String, name: String, amount: Int, unit: String){
        val ingredientValues = ContentValues().apply {
            put("name", name)
            put("pack_size", amount)
            put("amount", amount)
            put("unit", unit)
        }

        db.update("ingredients", ingredientValues, "id = ?", arrayOf(id))
    }

    fun editRecipe(id: String, name: String, image: Bitmap?, ingredients: List<RecipeIngredient>, instructions: String){
        val recipeValues = ContentValues().apply {
            put("name", name)
            put("instructions", instructions)
        }

        db.update("recipes", recipeValues, "id = ?", arrayOf(id))

        // update ingredients
        db.delete("recipe_ingredients", "recipe_id = ?", arrayOf(id))
        for (ingredient in ingredients) {
            val ingredientValues = ContentValues().apply {
                put("recipe_id", id)
                put("ingredient_id", ingredient.id)
                put("amount", ingredient.amount)
            }

            db.insert("recipe_ingredients", null, ingredientValues)
        }

        saveImage(id, image)
    }

    // ------------------- DELETIONS -------------------
    fun deleteIngredient(id: String) {
        db.delete("ingredients", "id = ?", arrayOf(id))
    }

    fun deleteRecipe(id: String) {
        db.delete("recipes", "id = ?", arrayOf(id))
    }

    fun deleteShoppingListItem(id: String) {
        db.delete("shopping_list", "ingredient_id = ?", arrayOf(id))
    }

    // ------------------- IMAGES -------------------
    fun getImage(id: String): Bitmap? {
        val imageFile = File(storagePath, "$IMAGE_DIR/$id.jpeg")
        if (!imageFile.exists()) {
            return null
        }
        return BitmapFactory.decodeFile(imageFile.absolutePath)
    }

    private fun saveImage(id: String, image: Bitmap?) {
        if (image == null) return

        val imageFile = File(storagePath, "$IMAGE_DIR/$id.jpeg")
        val fileOutputStream = FileOutputStream(imageFile)
        image.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
        fileOutputStream.close()
    }
}
