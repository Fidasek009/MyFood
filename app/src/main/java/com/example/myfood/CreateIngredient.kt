package com.example.myfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.myfood.model.database

class CreateIngredient : ComponentActivity() {
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
    }

    fun createIngredient() {
        val name = findViewById<EditText>(R.id.ingredientName).text.toString()
        val amount = findViewById<EditText>(R.id.ingredientAmount).text.toString()
        val unit = findViewById<EditText>(R.id.ingredientUnit).text.toString()

        // do not accept empty fields
        if(name == "" || amount == "" || unit == "") return

        database.newIngredient(name, amount.toInt(), unit)

        // successfully created ingredient
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}