package com.example.myfood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.adapter.RecipeAdapter
import com.example.myfood.model.Recipe
import com.example.myfood.model.Ingredient


class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        // create view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                R.id.nav_item1 -> {
                    // Handle option 1
                    true
                }
                R.id.nav_item2 -> {
                    // Handle option 2
                    true
                }
                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Set your adapter
        recyclerView.adapter = RecipeAdapter(getDummyRecipeData())
    }

    // drawer toggle click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    private fun getDummyRecipeData(): List<Recipe> {
        val ig1: Ingredient = Ingredient("Milk", 500, "ml")
        val ig3: Ingredient = Ingredient("Coke", 100, "g")
        val ig2: Ingredient = Ingredient("Egg", 5, "x")
        val igs: List<Ingredient> = listOf(ig1, ig2, ig3)

        return listOf(
            Recipe("Recipe 1", "img.jpg", igs, "Kill yourself"),
            Recipe("Recipe 2", "img.png", igs, "Let bro cook"),
        )
    }
}

