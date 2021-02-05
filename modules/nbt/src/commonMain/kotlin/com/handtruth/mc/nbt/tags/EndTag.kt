package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.Reader
import kotlinx.io.Input
import kotlinx.io.Output

object EndTag : Tag<Nothing> {
    override val type = Nothing::class
    override fun readBinary(input: Input, conf: NBTBinaryCodec) = throw UnsupportedOperationException()
    override fun readText(input: Reader, conf: NBTStringCodec) = throw UnsupportedOperationException()
    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Nothing) = Unit
    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Nothing, level: Int) = Unit
    override fun toString() = "TAG_End"
}
