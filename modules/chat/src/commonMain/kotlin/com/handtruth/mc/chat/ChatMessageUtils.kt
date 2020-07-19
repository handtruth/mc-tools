package com.handtruth.mc.chat

import com.handtruth.mc.chat.ChatMessage
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@DslMarker
annotation class ChatMessageDsl

class ChatMessageBuilder {
    @PublishedApi internal val chats = mutableListOf<ChatMessage>()

    @ChatMessageDsl
    operator fun ChatMessage.unaryPlus() {
        chats += this
    }

    @ChatMessageDsl
    operator fun String.unaryPlus() {
        text(this)
    }

    @ChatMessageDsl
    fun text(string: String) {
        +ChatMessage(text = string)
    }

    @ChatMessageDsl
    inline fun <R> bold(value: Boolean = true, block: ChatMessageBuilder.() -> R): R {
        val builder = ChatMessageBuilder()
        val result = builder.block()
        +ChatMessage("", bold = value, extra = builder.chats)
        return result
    }

    @ChatMessageDsl
    inline fun <R> italic(value: Boolean = true, block: ChatMessageBuilder.() -> R): R {
        val builder = ChatMessageBuilder()
        val result = builder.block()
        +ChatMessage("", italic = value, extra = builder.chats)
        return result
    }

    @ChatMessageDsl
    inline fun <R> underlined(value: Boolean = true, block: ChatMessageBuilder.() -> R): R {
        val builder = ChatMessageBuilder()
        val result = builder.block()
        chats += ChatMessage("", underlined = value, extra = builder.chats)
        return result
    }

    @ChatMessageDsl
    inline fun <R> strikethrough(value: Boolean = true, block: ChatMessageBuilder.() -> R): R {
        val builder = ChatMessageBuilder()
        val result = builder.block()
        chats += ChatMessage("", strikethrough = value, extra = builder.chats)
        return result
    }

    @ChatMessageDsl
    inline fun <R> obfuscated(value: Boolean = true, block: ChatMessageBuilder.() -> R): R {
        val builder = ChatMessageBuilder()
        val result = builder.block()
        chats += ChatMessage("", obfuscated = value, extra = builder.chats)
        return result
    }

    @ChatMessageDsl
    inline fun <R> color(value: ChatMessage.Color, block: ChatMessageBuilder.() -> R): R {
        val builder = ChatMessageBuilder()
        val result = builder.block()
        chats += ChatMessage("", color = value, extra = builder.chats)
        return result
    }

    fun build() = ChatMessage("", extra = chats)
}

@ChatMessageDsl
inline fun buildChat(block: ChatMessageBuilder.() -> Unit): ChatMessage {
    val builder = ChatMessageBuilder()
    builder.block()
    val chat = builder.build().flatten()
    if (chat.extra.size == 1)
        return chat.extra[0]
    return chat
}

fun parseControlSequences(value: String): ChatMessage {
    val list = mutableListOf<ChatMessage>()

    var bold: Boolean? = null
    var italic: Boolean? = null
    var underlined: Boolean? = null
    var strikethrough: Boolean? = null
    var obfuscated: Boolean? = null
    var color: ChatMessage.Color? = null

    operator fun String.unaryPlus() {
        if (this.isNotEmpty())
            list.add(ChatMessage(this, bold, italic, underlined, strikethrough, obfuscated, color))
    }

    var begin = 0
    var end: Int
    while (true) {
        end = value.indexOf('§', begin)
        if (end == -1) {
            +value.substring(begin)
            break
        }
        +value.substring(begin, end)
        if (value.length <= end)
            break
        when (val c = value[end + 1]) {
            // Colors section
            in '0'..'9', in 'a'..'f', in 'A'..'F' -> color = ChatMessage.Color.getByCode(c)
            // Format section
            'k' -> obfuscated = true
            'l' -> bold = true
            'm' -> strikethrough = true
            'n' -> underlined = true
            'o' -> italic = true
            'r' -> {
                bold = null
                italic = null
                underlined = null
                strikethrough = null
                obfuscated = null
                color = null
            }
        }
        begin = end + 2
    }
    return when {
        list.isEmpty() -> ChatMessage.empty
        list.size == 1 -> list[0]
        else -> {
            ChatMessage("", extra = list)
        }
    }
}

fun List<ChatMessage>.toChatString(): String {
    return json.stringify(ChatMessageJsonSerializer.list, this)
}

internal val json = Json(JsonConfiguration.Stable.copy(encodeDefaults = false, ignoreUnknownKeys = true))
