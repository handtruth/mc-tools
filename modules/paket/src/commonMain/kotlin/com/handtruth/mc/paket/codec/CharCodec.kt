package com.handtruth.mc.paket.codec

import com.handtruth.mc.paket.util.CHAR_SIZE
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.field.CharField
import com.handtruth.mc.paket.util.readChar
import com.handtruth.mc.paket.util.writeChar
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public object CharCodec : Codec<Char> {
    override fun measure(value: Char): Int = CHAR_SIZE
    override fun read(input: Input): Char = input.readChar()
    override fun write(output: Output, value: Char): Unit = output.writeChar(value)

    public operator fun provideDelegate(paket: Paket, property: KProperty<*>): CharField {
        return paket.field(CharField())
    }
}
