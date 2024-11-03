package com.example.IntelliHome

import com.example.IntelliHome.ListofHostViewActivity.Property
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PropertyParser {
     fun parseProperties(message: String): List<Property> {
        val properties = mutableListOf<Property>()

        try {
            val jsonArray = JSONArray(message) // Trata el mensaje como un JSONArray
            for (i in 0 until jsonArray.length()) {
                val json = JSONObject(jsonArray.getString(i)) // Extrae cada objeto como JSONObject
                val property = Property(
                    action = json.getString("action"),
                    idPropertyRegister = json.getString("idPropertyRegister"),
                    location = json.getString("location"),
                    typeofHouse = json.getString("typeofHouse"),
                    availability = json.optString("availability", "No especificada"),
                    cantofPeople = json.getInt("cantofPeople"),
                    amenities = json.getJSONArray("amenidades").let { amenitiesArray ->
                        List(amenitiesArray.length()) { j -> amenitiesArray.getString(j) }
                    },
                    rules = json.getString("rules"),
                    price = json.getInt("price")
                )
                properties.add(property)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return properties
    }
}