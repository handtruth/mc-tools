package com.handtruth.mc.client.test

import com.handtruth.mc.chat.ChatMessage
import com.handtruth.mc.chat.buildChat
import com.handtruth.mc.client.MinecraftClient
import com.handtruth.mc.client.model.ServerStatus
import com.handtruth.mc.client.use
import com.handtruth.mc.minecraft.UUID
import com.handtruth.mc.minecraft.model.Player
import io.ktor.test.dispatcher.testSuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.seconds

class APITest {
    @Test
    fun getVanillaStatus() = testSuspend {
        withTimeout(3.seconds) {
            MinecraftClient("vanilla.mc.handtruth.com", 25565).use {
                println(it.getStatus())
            }
        }
    }

    companion object {
        private val exampleStatus = ServerStatus(
            version = ServerStatus.Version(
                name = "Server",
                protocol = -1
            ),
            players = ServerStatus.Players(
                max = 250,
                online = 19,
                sample = listOf(
                    Player(
                        id = UUID("c683771d-8246-4e14-9db0-528b63c265cb"),
                        name = "Popka"
                    ),
                    Player(
                        id = UUID("80d86712-45be-445e-b16c-5861eacd9624"),
                        name = "Zopka"
                    ),
                    Player(
                        id = UUID("12b53aaf-4a1c-48d9-a3f3-3b40e220c541"),
                        name = "Player"
                    )
                )
            ),
            description = buildChat {
                text(" ")
                text("Ассоци")
                italic {
                    +("иции ")
                    bold {
                        +("с ")
                        color(ChatMessage.Color.Gold) {
                            underlined {
                                text("лет")
                            }
                            text("а")
                            color(ChatMessage.Color.DarkRed) {
                                text("ю")
                            }
                            italic(false) {
                                +("щ")
                            }
                            text("им")
                        }
                    }
                }
                text(" ")
                strikethrough {
                    text("квадра")
                }
                text("коп")
                obfuscated {
                    text("тером")
                }
            }
        )
    }

    @ExperimentalCoroutinesApi @Test
    fun getExampleStatus() = testSuspend {
        MinecraftClient("example.mc.handtruth.com", 25565).use { client ->
            val status = client.getStatus()
            assertNotNull(status.favicon)
            val expected = exampleStatus.copy(favicon = status.favicon)
            assertEquals(expected.description, status.description)
            val string = status.description.toString()
            assertEquals(" Ассоцииции с летающим квадракоптером", string)
            assertEquals(string.length, status.description.length)
            assertEquals(expected, status)
            client.ping().take(3).collect()
            client.disconnect()
        }
    }

    @Test
    fun pingVanilla() = testSuspend {
        MinecraftClient("vanilla.mc.handtruth.com", 25565).use { client ->
            val ping = client.ping()
                .drop(1)
                .take(10)
                .withIndex()
                .map { (i, ping) -> println("#$i -> $ping"); ping.inMilliseconds }
                .reduce { a, b -> a + b } / 10
            println("${ping}ms")
        }
    }
}
