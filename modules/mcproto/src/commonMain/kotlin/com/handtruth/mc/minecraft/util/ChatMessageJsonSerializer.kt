package com.handtruth.mc.minecraft.util

import com.handtruth.mc.minecraft.model.ChatMessage
import kotlinx.serialization.json.*

object ChatMessageJsonSerializer : JsonTransformingSerializer<ChatMessage>(
    ChatMessage.serializer(), "chatVariant"
) {
    override fun readTransform(element: JsonElement): JsonElement {
        return when (element) {
            is JsonArray -> json {
                "text" to emptyElement
                "extra" to element
            }
            is JsonPrimitive -> json {
                "text" to element
            }
            is JsonObject -> element
        }
    }

    private val emptyElement: JsonElement = JsonPrimitive("")

    override fun writeTransform(element: JsonElement): JsonElement {
        element as JsonObject
        when (element.size) {
            1 -> {
                val text = element["text"]
                if (text != null)
                    return text
            }
            2 -> {
                val text = element["text"]
                val extra = element["extra"]
                if (text != null && extra != null && text is JsonPrimitive && extra is JsonArray && text.content.isEmpty())
                    return JsonArray(listOf(emptyElement) + extra.content)
            }
        }
        return element
    }
}
