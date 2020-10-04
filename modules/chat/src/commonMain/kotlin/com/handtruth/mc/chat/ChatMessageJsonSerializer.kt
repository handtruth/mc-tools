package com.handtruth.mc.chat

import kotlinx.serialization.json.*

object ChatMessageJsonSerializer : JsonTransformingSerializer<ChatMessage>(ChatMessage.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonArray -> buildJsonObject {
                put("text", emptyElement)
                put("extra", element)
            }
            is JsonPrimitive -> buildJsonObject {
                put("text", element)
            }
            is JsonObject -> element
        }
    }

    private val emptyElement: JsonElement = JsonPrimitive("")

    override fun transformSerialize(element: JsonElement): JsonElement {
        element as JsonObject
        when (element.size) {
            1 -> {
                val text = element["text"]
                if (text != null) {
                    return text
                }
            }
            2 -> {
                val text = element["text"]
                val extra = element["extra"]
                if (text != null && extra != null && text is JsonPrimitive && extra is JsonArray && text.content.isEmpty()) {
                    return buildJsonArray {
                        add(emptyElement)
                        extra.forEach { add(it) }
                    }
                }
            }
        }
        return element
    }
}
