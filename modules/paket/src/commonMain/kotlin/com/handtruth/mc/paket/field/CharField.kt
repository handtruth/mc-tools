package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.CHAR_SIZE
import com.handtruth.mc.paket.codec.CharCodec
import com.handtruth.mc.paket.util.readChar
import com.handtruth.mc.paket.util.writeChar
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class CharField(public var char: Char = 0.toChar()) : AbstractField<Char>() {
    override val size: Int get() = CHAR_SIZE

    override var value: Char by this::char

    override val codec: CharCodec get() = CharCodec

    override fun read(input: Input) {
        char = input.readChar()
    }

    override fun write(output: Output) {
        output.writeChar(char)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Char {
        return char
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Char) {
        this.char = value
    }
}
