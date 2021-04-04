package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.SHORT_SIZE
import com.handtruth.mc.paket.codec.ShortCodec
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class ShortField(public var short: Short = 0) : AbstractField<Short>() {
    override val size: Int get() = SHORT_SIZE

    override var value: Short by this::short

    override val codec: ShortCodec get() = ShortCodec

    override fun read(input: Input) {
        short = input.readShort()
    }

    override fun write(output: Output) {
        output.writeShort(short)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Short {
        return short
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Short) {
        this.short = value
    }
}
