package com.example.myfood.model


data class Ingredient(
    val id: String,
    val name: String,
    val packSize: Int,
    val amount: Int,
    val unit: String
)

data class RecipeIngredient(
    val id: String,
    val name: String,
    var amount: Int,
    val unit: String
)