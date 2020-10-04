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

    @Test
    fun parseSome() {
        val expected = buildChat {
            +" "
            +"Ассоци"
            italic {
                +"иции "
                bold {
                    +"с "
                    color(ChatMessage.Color.Gold) {
                        underlined {
                            +"лет"
                        }
                        +"а"
                        color(ChatMessage.Color.DarkRed) {
                            +"ю"
                        }
                        italic(false) {
                            +"щ"
                        }
                        +"им"
                    }
                }
            }
            +" "
            strikethrough {
                +"квадра"
            }
            +"коп"
            obfuscated {
                +"тером"
            }
        }
        val actual = ChatMessage.parse(
            """
            [
                " ",
                {
                    "text": "Ассоци"
                },
                {
                    "text": "иции ",
                    "italic": true
                },
                {
                    "text": "с ",
                    "bold": true,
                    "italic": true
                },
                {
                    "text": "лет",
                    "bold": true,
                    "italic": true,
                    "underlined": true,
                    "color": "gold"
                },
                {
                    "text": "а",
                    "bold": true,
                    "italic": true,
                    "color": "gold"
                },
                {
                    "text": "ю",
                    "bold": true,
                    "italic": true,
                    "color": "dark_red"
                },
                {
                    "text": "щ",
                    "bold": true,
                    "italic": false,
                    "color": "gold"
                },
                {
                    "text": "им",
                    "bold": true,
                    "italic": true,
                    "color": "gold"
                },
                {
                    "text": " "
                },
                {
                    "text": "квадра",
                    "strikethrough": true
                },
                {
                    "text": "коп"
                },
                {
                    "text": "тером",
                    "obfuscated": true
                }
            ]
            """.trimIndent()
        )
        assertEquals(expected, actual)
    }
}
