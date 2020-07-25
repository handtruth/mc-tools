@file:UseSerializers(ChatMessageJsonSerializer::class)

package com.handtruth.mc.chat

import com.handtruth.mc.chat.ChatMessageJsonSerializer
import com.handtruth.mc.chat.parseControlSequences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ChatMessage(
    val text: String,
    val bold: Boolean? = null,
    val italic: Boolean? = null,
    val underlined: Boolean? = null,
    val strikethrough: Boolean? = null,
    val obfuscated: Boolean? = null,
    val color: Color? = null,
    val insertion: String? = null,
    val clickEvent: ChatEvent? = null,
    val hoverEvent: ChatEvent? = null,
    val extra: List<ChatMessage> = emptyList()
) {

    val isPlain: Boolean
        get() {
            val nullable =
                bold ?: italic ?: underlined ?: strikethrough ?: obfuscated ?: color ?: insertion ?: clickEvent
                ?: hoverEvent
            return nullable == null && extra.isEmpty()
        }

    private fun stringify(builder: Appendable) {
        builder.append(text)
        for (each in extra)
            each.stringify(builder)
    }

    fun toChatString() = json.stringify(ChatMessageJsonSerializer, this)

    val length: Int get() = text.length + extra.sumBy { it.length }

    override fun toString(): String = buildString { stringify(this) }

    private fun flatten(chats: MutableList<ChatMessage>, extra: List<ChatMessage> = this.extra) {
        for (chat in extra) {
            val newChat = chat.copy(
                bold = chat.bold ?: bold,
                italic = chat.italic ?: italic,
                underlined = chat.underlined ?: underlined,
                strikethrough = chat.strikethrough ?: strikethrough,
                obfuscated = chat.obfuscated ?: obfuscated,
                color = chat.color ?: color,
                insertion = chat.insertion ?: insertion,
                clickEvent = chat.clickEvent ?: clickEvent,
                hoverEvent = chat.hoverEvent ?: hoverEvent,
                extra = emptyList()
            )
            chats += newChat
            newChat.flatten(chats, chat.extra)
        }
    }

    fun flatten(): ChatMessage {
        val chats = mutableListOf<ChatMessage>()
        flatten(chats)
        chats.removeAll { it.text == "" }
        return copy(extra = chats)
    }

    fun resolveControlSequences(): ChatMessage {
        val chat = parseControlSequences(text)
        return copy(text = "", extra = listOf(chat) + extra.map { it.resolveControlSequences() })
    }

    @Serializable
    data class ChatEvent(
        val action: String,
        val value: String
    )

    @Suppress("unused")
    @Serializable
    enum class Color(val color: String, val css: String, val code: Int) {
        @SerialName("unknown") Unknown("unknown", "black", 0),
        @SerialName("black") Black("black", "black", 0x000000),
        @SerialName("dark_blue") DarkBlue("dark_blue", "darkblue", 0x0000AA),
        @SerialName("dark_green") DarkGreen("dark_green", "darkgreen", 0x00AA00),
        @SerialName("dark_aqua") DarkAqua("dark_aqua", "darkcyan", 0x00AAAA),
        @SerialName("dark_red") DarkRed("dark_red", "darkred", 0xAA0000),
        @SerialName("dark_purple") DarkPurple("dark_purple", "purple", 0xAA00AA),
        @SerialName("gold") Gold("gold", "gold", 0xFFAA00),
        @SerialName("gray") Gray("gray", "gray", 0xAAAAAA),
        @SerialName("dark_gray") DarkGray("dark_gray", "darkgray", 0x555555),
        @SerialName("blue") Blue("blue", "blue", 0x5555FF),
        @SerialName("green") Green("green", "green", 0x55FF55),
        @SerialName("aqua") Aqua("aqua", "cyan", 0x55FFFF),
        @SerialName("red") Red("red", "red", 0xFF5555),
        @SerialName("light_purple") LightPurple("light_purple", "mediumorchid", 0xFF55FF),
        @SerialName("yellow") Yellow("yellow", "yellow", 0xFFFF55),
        @SerialName("white") White("white", "white", 0xFFFFFF);

        override fun toString(): String {
            return color
        }

        companion object {
            private val byName = values().associateBy { it.color }
            private val byCSS = values().associateBy { it.css }

            fun getByName(color: String) = byName[color] ?: Unknown
            fun getByCSS(color: String) = byCSS[color] ?: Unknown
            fun getByCode(code: Char) = when (code) {
                in '0'..'9' -> values()[code - '0' + 1]
                in 'a'..'f' -> values()[code - 'a' + 11]
                in 'A'..'F' -> values()[code - 'A' + 11]
                else -> Unknown
            }
        }
    }

    companion object {
        val empty = ChatMessage("")
        fun parse(string: String): ChatMessage = json.parse(ChatMessageJsonSerializer, string)
    }
}
