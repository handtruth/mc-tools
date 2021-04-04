package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.ULongCodec
import com.handtruth.mc.paket.util.LONG_SIZE
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class ULongField(public var long: ULong = 0u) : AbstractField<ULong>() {
    override val size: Int get() = LONG_SIZE

    override var value: ULong by this::long

    override val codec: ULongCodec get() = ULongCodec

    override fun read(input: Input) {
        long = input.readULong()
    }

    override fun write(output: Output) {
        output.writeULong(long)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): ULong {
        return long
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: ULong) {
        this.long = value
    }
}
