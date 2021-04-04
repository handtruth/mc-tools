package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.BYTE_SIZE
import com.handtruth.mc.paket.codec.ByteCodec
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class ByteField(public var byte: Byte = 0) : AbstractField<Byte>() {
    override val size: Int get() = BYTE_SIZE

    override var value: Byte by this::byte

    override val codec: ByteCodec get() = ByteCodec

    override fun read(input: Input) {
        byte = input.readByte()
    }

    override fun write(output: Output) {
        output.writeByte(byte)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Byte {
        return byte
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Byte) {
        this.byte = value
    }
}
