package com.fatih.prayertime.util.model.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type

class SajdaTypeAdapter : JsonDeserializer<Boolean?> {

    override fun deserialize(
        json: com.google.gson.JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Boolean? {
        return when {
            json.isJsonPrimitive && json.asJsonPrimitive.isBoolean -> json.asBoolean
            json.isJsonObject -> json.asJsonObject.get("recommended")?.asBoolean
            else -> null
        }
    }
}