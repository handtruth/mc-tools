package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.NBTStringConfig
import com.handtruth.mc.nbt.util.Reader
import com.handtruth.mc.nbt.util.readSize
import com.handtruth.mc.nbt.util.skipSpace
import com.handtruth.mc.nbt.util.writeSize
import com.handtruth.mc.types.Bytes
import com.handtruth.mc.util.UnsafeBytes
import io.ktor.utils.io.core.*

object BytesTag : Tag<Bytes> {
    override val type = Bytes::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): Bytes {
        val size = readSize(input, conf.binaryConfig)
        @OptIn(UnsafeBytes::class)
        return Bytes.wrap(input.readBytes(size))
    }

    override fun readText(input: Reader, conf: NBTStringCodec): Bytes {
        input.skipSpace()
        check(input.read() == '(') { "bytes token expected" }
        var count = 0
        while (true) {
            val char = input.read()
            if (char == ')') {
                break
            }
            ++count
        }
        input.back()
        val string = input.previous(count)
        input.read()
        return Bytes.fromString(string, variant = conf.stringConfig.base64Variant)
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Bytes) {
        val size = value.size
        writeSize(output, conf.binaryConfig, size)
        if (size != 0) {
            @OptIn(UnsafeBytes::class)
            output.writeFully(value.getByteArray())
        }
    }

    private fun writeBase64(value: Bytes, output: Appendable, config: NBTStringConfig, level: Int) {
        output.append('(')
        if (config.pretty && config.base64LineLength != Int.MAX_VALUE) {
            val ident = '\n' + config.identString.repeat(level + 1)
            output.append(ident)
            value.appendTo(output, ident, config.base64Variant, config.base64LineLength)
            output.append(ident.substring(0, ident.length - 1))
        } else {
            value.appendTo(output, variant = config.base64Variant)
        }
        output.append(')')
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Bytes, level: Int) {
        val size = value.size
        if (size != 0) {
            writeBase64(value, output, conf.stringConfig, level)
        } else {
            output.append("()")
        }
    }

    override fun toString() = "TAG_Bytes"
}
