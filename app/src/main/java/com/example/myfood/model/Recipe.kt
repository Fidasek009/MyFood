package com.example.myfood.model


data class Recipe(
    val name: String,
    val image: String,
    var ingredients: List<Ingredient>,
    val instructions: String
)