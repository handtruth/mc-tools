package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.skipSpace
import io.ktor.utils.io.core.*

object BooleanTag : Tag<Boolean> {
    override val type = Boolean::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec) = input.readByte() != 0.toByte()

    override fun readText(input: Reader, conf: NBTStringCodec): Boolean {
        input.skipSpace()
        return when (input.read()) {
            't' -> {
                for (c in "rue") {
                    check(c == input.read()) { "boolean token expected" }
                }
                true
            }
            'f' -> {
                for (c in "alse") {
                    check(c == input.read()) { "boolean token expected" }
                }
                false
            }
            else -> error("boolean token expected")
        }
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Boolean) {
        output.writeByte(if (value) 1 else 0)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Boolean, level: Int) {
        output.append(if (value) "true" else "false")
    }

    override fun toString() = "TAG_Boolean"
}
