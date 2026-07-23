package com.example.midtermproject_24125072.data

import android.content.Context
import com.example.midtermproject_24125072.R
import org.json.JSONArray
import java.io.IOException

data class CoffeeItem(
    val name: String,
    val price: Double,
    val description: String,
    val imageResId: Int,
    val id: String
)

public  fun getImageResId(itemName: String): Int = when (itemName) {
    "americano" -> R.drawable.americano
    "cappuchino" -> R.drawable.cappuccino
    "mocha" -> R.drawable.mocha
    "flatwhite" -> R.drawable.flatwhite
    else -> -1
}

fun loadCoffeeList(context: Context): List<CoffeeItem> {
    val jsonString = try {
        context.assets.open("coffee_items.json")
            .bufferedReader()
            .use { it.readText() }
    } catch (e: IOException) {
        return emptyList()
    }
    val jsonArray = JSONArray(jsonString)
    val list = mutableListOf<CoffeeItem>()
    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val imageResId = getImageResId(obj.getString("id"))
        list.add(
            CoffeeItem(
                id = obj.getString("id"),
                name = obj.getString("name"),
                description = obj.getString("description"),
                imageResId = imageResId,
                price = obj.getDouble("price")
            )
        )
    }
    return list
}