package com.example.myfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.adapter.IngredientAdapter
import com.example.myfood.adapter.RecipeAdapter
import com.example.myfood.model.Database
import com.example.myfood.model.Recipe
import com.example.myfood.model.database
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private var showingRecipes = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // initialize database
        database = Database(this)

        // create view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // custom function to create toolbar and drawer
        createToolbar()

        // render only available recipes by default
        renderRecipes(database.getAvailableRecipes())
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
                    renderRecipes(database.getAvailableRecipes())
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.allRecipes -> {
                    renderRecipes(database.getRecipes())
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.myIngredients -> {
                    renderIngredients()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.shoppingList -> {
                    // TODO: Create shopping list
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

    // handle result when creating new recipe or ingredient (render with new entries)
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if(showingRecipes){
                    renderRecipes(database.getAvailableRecipes())
                } else {
                    renderIngredients()
                }
            }
        }
    
    private fun renderRecipes(recipes: MutableList<Recipe>) {
        // available recipes
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecipeAdapter(this.startForResult ,recipes)
        showingRecipes = true

        // add recipe button
        val addRecipeButton = findViewById<FloatingActionButton>(R.id.addButton)
        addRecipeButton.setOnClickListener {
            val createRecipe = Intent(this, CreateRecipe::class.java)
            startForResult.launch(createRecipe)
        }
    }

    private fun renderIngredients(){
        // available recipes
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = IngredientAdapter(this.startForResult, database.getIngredients())
        showingRecipes = false

        // add ingredient button
        val addIngredientButton = findViewById<FloatingActionButton>(R.id.addButton)
        addIngredientButton.setOnClickListener {
            val createIngredient = Intent(this, CreateIngredient::class.java)
            startForResult.launch(createIngredient)
        }
    }
}
