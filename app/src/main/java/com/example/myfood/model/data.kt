package com.example.myfood.model



// for handling all the data
class data {
    var recipes: MutableList<Recipe>
    var ingredients: MutableList<Ingredient>

    // TODO: load the data from the database/file
    constructor() {
        recipes = mutableListOf<Recipe>()
        ingredients = mutableListOf<Ingredient>()
    }
}