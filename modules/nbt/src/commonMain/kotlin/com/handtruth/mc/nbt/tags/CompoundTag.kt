package com.handtruth.mc.nbt.tags

import com.handtruth.mc.nbt.NBTBinaryCodec
import com.handtruth.mc.nbt.NBTStringCodec
import com.handtruth.mc.nbt.util.*
import com.handtruth.mc.types.Dynamic
import com.handtruth.mc.types.buildDynamic
import io.ktor.utils.io.core.*

object CompoundTag : Tag<Dynamic> {
    override val type = Dynamic::class

    override fun readBinary(input: Input, conf: NBTBinaryCodec): Dynamic {
        return buildDynamic {
            while (true) {
                if (input.endOfInput) {
                    break
                }
                val tagId = input.readByte()
                val tag = conf.tagsModule.tagById(tagId)
                if (tag == EndTag) {
                    break
                }
                val key = readString(input, conf.binaryConfig)
                val value = tag.readBinary(input, conf)
                val previous = fields.put(key, value)
                validate(previous == null) { "value with that name was already specified" }
            }
        }
    }

    override fun readText(input: Reader, conf: NBTStringCodec): Dynamic {
        input.skipSpace()
        if (input.read() != '{') {
            error("not an compound tag")
        }
        return buildDynamic {
            while (true) {
                when (deduceTag(input, conf)) {
                    EndTag -> {
                        input.skipSpace()
                        check(input.read() == '}') { "unexpected token" }
                        break
                    }
                    StringTag -> {
                    }
                    else -> error("string expected as key")
                }
                val key = readString(input)
                input.skipSpace()
                check(input.read() == ':') { "key-value delimiter expected" }
                val valueTag = deduceTag(input, conf)
                check(valueTag != EndTag) { "value expected" }
                val value = valueTag.readText(input, conf)
                key assign value
                input.skipSpace()
                when (input.read()) {
                    ',' -> {
                    }
                    '}' -> break
                    else -> error("unexpected token")
                }
            }
        }
    }

    override fun writeBinary(output: Output, conf: NBTBinaryCodec, value: Dynamic) {
        for ((key, any) in value) {
            val tag = conf.tagsModule.tagOf(any)
            output.writeByte(conf.tagsModule.tagIdOf(tag))
            writeString(output, conf.binaryConfig, key)
            tag.writeBinary(output, conf, any)
        }
        output.writeByte(0)
    }

    override fun writeText(output: Appendable, conf: NBTStringCodec, value: Dynamic, level: Int) {
        smartJoin(
            value.iterator(),
            output,
            prefix = "{",
            postfix = "}",
            pretty = conf.stringConfig.pretty,
            level = level,
            identString = conf.stringConfig.identString
        ) { (key, value) ->
            writeString(this, conf.stringConfig.quoteKeys, key, conf)
            append(if (conf.stringConfig.pretty) ": " else ":")
            val tag = conf.tagsModule.tagOf(value)
            tag.writeText(this, conf, value, level + 1)
        }
    }

    override fun toString() = "TAG_Compound"
}
