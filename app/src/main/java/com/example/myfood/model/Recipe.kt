package com.example.myfood.model


data class Recipe(
    val id: String,
    val name: String,
    var ingredients: List<RecipeIngredient>,
    val instructions: String
)