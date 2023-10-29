package com.example.myfood

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.adapter.IngredientAdapter
import com.example.myfood.adapter.RecipeAdapter
import com.example.myfood.model.Recipe
import com.example.myfood.model.Ingredient
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        // create view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // custom function to create toolbar and drawer
        createToolbar()

        // defaultly render only available recipes
        renderAvailableRecipes()
    }

    private fun createToolbar(){
        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        // drawer toggle
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // drawer item click listener
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.availableRecipes -> {
                    renderAvailableRecipes()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.allRecipes -> {
                    // Handle option 1
                    true
                }
                R.id.myIngredients -> {
                    renderIngredients()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.shoppingList -> {
                    // Handle option 2
                    true
                }
                R.id.about -> {
                    // Handle option 2
                    true
                }
                else -> false
            }
        }
    }

    // drawer toggle click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun renderAvailableRecipes() {
        // available recipes
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecipeAdapter(getDummyRecipeData())

        // add recipe button
        val addRecipeButton = findViewById<FloatingActionButton>(R.id.addButton)
        addRecipeButton.setOnClickListener {
            val createRecipe = Intent(this, CreateRecipe::class.java)
            startActivity(createRecipe)
        }
    }

    // TODO: load actual data
    private fun getDummyRecipeData(): List<Recipe> {
        val igs = getDummyIngredients()
        return listOf(
            Recipe("Recipe 1", "img.jpg", igs, "Kill yourself"),
            Recipe("Recipe 2", "img.png", igs, "Let bro cook"),
        )
    }

    private fun renderIngredients(){
        // available recipes
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = IngredientAdapter(getDummyIngredients())

        // add ingredient button
        val addIngredientButton = findViewById<FloatingActionButton>(R.id.addButton)
        addIngredientButton.setOnClickListener {
            val createIngredient = Intent(this, CreateIngredient::class.java)
            startActivity(createIngredient)
        }
    }

    // TODO: load actual data
    private fun getDummyIngredients(): List<Ingredient> {
        val ig1: Ingredient = Ingredient("Milk", 500, "ml")
        val ig3: Ingredient = Ingredient("Coke", 100, "g")
        val ig2: Ingredient = Ingredient("Egg", 5, "x")
        return listOf(ig1, ig2, ig3)
    }
}
