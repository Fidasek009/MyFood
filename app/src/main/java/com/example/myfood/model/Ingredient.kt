package com.example.myfood.model


data class Ingredient(
    val name: String,
    val packSize: Int,
    var amount: Int,
    val unit: String
)

data class RecipeIngredient(
    val id: String,
    val name: String,
    val amount: Int,
    val unit: String
)