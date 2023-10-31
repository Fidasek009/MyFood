package com.example.myfood.model


data class Recipe(
    val name: String,
    val image: String,
    var ingredients: List<RecipeIngredient>,
    val instructions: String
)