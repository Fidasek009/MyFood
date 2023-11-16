package com.example.myfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.ComponentActivity
import com.example.myfood.model.database

class CreateIngredient : ComponentActivity() {
    private val id by lazy { intent.getStringExtra("id") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_ingredient)

        // Cancel button
        val cancelButton = findViewById<Button>(R.id.cancelIngredient)
        cancelButton.setOnClickListener {
            finish()
        }

        val saveButton = findViewById<Button>(R.id.saveIngredient)
        saveButton.setOnClickListener {
            createIngredient()
        }

        // load values if in edit mode
        loadValues()
    }

    private fun loadValues() {
        if (id == null) return

        val ingredient = database.getIngredient(id!!)

        findViewById<EditText>(R.id.ingredientName).setText(ingredient.name)
        findViewById<EditText>(R.id.ingredientAmount).setText(ingredient.amount.toString())
        val units = resources.getStringArray(R.array.units)
        val unitIndex = units.indexOf(ingredient.unit)
        findViewById<Spinner>(R.id.ingredientUnit).setSelection(unitIndex)
    }

    private fun createIngredient() {
        val name = findViewById<EditText>(R.id.ingredientName).text.toString()
        val amount = findViewById<EditText>(R.id.ingredientAmount).text.toString()
        val unit = findViewById<Spinner>(R.id.ingredientUnit).selectedItem.toString()

        // do not accept empty fields
        if(name == "" || amount == "" || unit == "") return

        // create or edit ingredient
        if (id == null) {
            database.newIngredient(name, amount.toInt(), unit)
        }
        else {
            database.editIngredient(id!!, name, amount.toInt(), unit)
        }

        // successfully created ingredient
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}