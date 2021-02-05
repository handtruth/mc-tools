package com.handtruth.mc.minecraft.model.test

import com.handtruth.mc.minecraft.model.Player
import com.handtruth.mc.minecraft.model.PlayerSerializer
import com.handtruth.mc.types.UUID
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerTest {
    @Test
    fun playerTest() {
        // There is no actual needs for this, but coverage fails
        val json = Json {}
        val player = Player("ktlo", UUID.parse("be844e64-ac07-4606-8997-6d47fe4084ca"))
        val string = json.encodeToString(PlayerSerializer, player)
        val actual = json.decodeFromString(PlayerSerializer, string)
        assertEquals(player, actual)
    }
}
