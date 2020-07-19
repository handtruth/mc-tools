package com.handtruth.mc.chat.test

import com.handtruth.mc.chat.ChatMessage
import com.handtruth.mc.chat.buildChat
import com.handtruth.mc.chat.parseControlSequences
import com.handtruth.mc.chat.toChatString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatMessageTest {

    @Test
    fun controlSequencesTest() {
        val expected = ChatMessage(
            "",
            extra = listOf(
                ChatMessage(text = "Paradox Universe", color = ChatMessage.Color.DarkPurple, bold = true),
                ChatMessage(text = " "),
                ChatMessage(text = "(", color = ChatMessage.Color.Red),
                ChatMessage(text = "SpaceTech", color = ChatMessage.Color.Gray, italic = true),
                ChatMessage(text = ")", color = ChatMessage.Color.Red),
                ChatMessage(text = " "),
                ChatMessage(text = "QKM", color = ChatMessage.Color.Gold, bold = true, underlined = true)
            )
        )
        val actual = parseControlSequences("§r§5§lParadox Universe§r §c(§7§oSpaceTech§r§c)§r §6§l§nQKM")
        assertEquals(expected, actual)
        val string = actual.toString()
        assertEquals("Paradox Universe (SpaceTech) QKM", string)
        assertEquals(string.length, actual.length)

        val boxed = ChatMessage(
            text = "§r§5§lParadox Universe",
            extra = listOf(ChatMessage("§r §c(§7§oSpaceTech§r§c)§r §6§l§nQKM"))
        ).resolveControlSequences().flatten()
        assertEquals(expected, boxed)
    }

    @Test
    fun stringFormat() {
        val chat = buildChat {
            color(ChatMessage.Color.Gold) {
                bold {
                    +("Hello")
                }
                text(" ")
                italic {
                    text("World!!!")
                }
            }
            text(" Plain")
        }
        assertEquals(
            """["",{"text":"Hello","bold":true,"color":"gold"},{"text":" ","color":"gold"},{"text":"World!!!","italic":true,"color":"gold"}," Plain"]""",
            chat.toChatString()
        )
        assertEquals(
            """[{"text":"Hello","bold":true,"color":"gold"},{"text":" ","color":"gold"},{"text":"World!!!","italic":true,"color":"gold"}," Plain"]""",
            chat.extra.toChatString()
        )

        val simple = buildChat {
            text("Text")
        }
        assertEquals("\"Text\"", simple.toChatString())
        assertTrue { simple.isPlain }

        val ordinal = buildChat {
            bold {
                italic {
                    color(ChatMessage.Color.Gold) {
                        +("Ordinal")
                    }
                }
            }
        }
        assertEquals("""{"text":"Ordinal","bold":true,"italic":true,"color":"gold"}""", ordinal.toChatString())
    }
}
