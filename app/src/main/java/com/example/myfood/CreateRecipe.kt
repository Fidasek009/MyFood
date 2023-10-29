package com.example.myfood

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity


class CreateRecipe : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_recipe)

        // Cancel button
        val cancelButton = findViewById<Button>(R.id.cancelRecipe)
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
