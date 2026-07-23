package com.example.midtermproject_24125072.data

import com.example.midtermproject_24125072.R

data class CoffeeItem(
    val name: String,
    val price: Double,
    val description: String,
    val imageResId: Int,
    val id: String
)

val coffeeList = listOf(
    CoffeeItem(
        id = "americano",
        name = "Americano",
        description = "Placeholder",
        imageResId = R.drawable.americano,
        price = 3.00
    ),
    CoffeeItem(
        id = "cappuchino",
        name = "Cappuchino",
        description = "Placeholder",
        imageResId = R.drawable.cappuccino,
        price = 3.00
    ),
    CoffeeItem(
        id = "mocha",
        name = "Mocha",
        description = "Placeholder",
        imageResId = R.drawable.mocha,
        price = 3.00
    ),
    CoffeeItem(
        id = "white_coffee",
        name = "White coffee",
        description = "Placeholder",
        imageResId = R.drawable.flatwhite,
        price = 3.00
    ),
)