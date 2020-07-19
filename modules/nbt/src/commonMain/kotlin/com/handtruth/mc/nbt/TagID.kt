package com.handtruth.mc.nbt

import com.handtruth.mc.nbt.tags.*

enum class TagID(val resolver: TagResolver<*>) {
    End(EndTag), Byte(ByteTag), Short(ShortTag),
    Int(IntTag), Long(LongTag), Float(FloatTag),
    Double(DoubleTag), ByteArray(ByteArrayTag), String(StringTag),
    List(ListTag), Compound(CompoundTag), IntArray(IntArrayTag),
    LongArray(LongArrayTag)
}
