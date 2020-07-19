package com.handtruth.mc.nbt.test

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.nbt.tags.CompoundTag
import com.handtruth.mc.nbt.tags.Tag
import kotlinx.serialization.Serializable

@Serializable
data class Inventory(val items: List<Item>, val metadata: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Inventory

        if (items != other.items) return false
        if (!metadata.contentEquals(other.metadata)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = items.hashCode()
        result = 31 * result + metadata.contentHashCode()
        return result
    }
}

@Serializable
data class Item(val id: String, val count: Byte, val durability: Short, val pages: Map<String, String>)

@Serializable
data class Player(val id: Int, val name: String, val inventory: Inventory, val moment: FloatArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Player

        if (id != other.id) return false
        if (name != other.name) return false
        if (inventory != other.inventory) return false
        if (!moment.contentEquals(other.moment)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + inventory.hashCode()
        result = 31 * result + moment.contentHashCode()
        return result
    }
}

fun player2nbt(player: Player) = NBT().serialize(Player.serializer(), player) as CompoundTag
fun nbt2player(tag: Tag<*>) = NBT().deserialize(Player.serializer(), tag)
