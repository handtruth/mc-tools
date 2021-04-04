package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.LONG_SIZE
import com.handtruth.mc.paket.codec.LongCodec
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class LongField(public var long: Long = 0) : AbstractField<Long>() {
    override val size: Int get() = LONG_SIZE

    override var value: Long by this::long

    override val codec: LongCodec get() = LongCodec

    override fun read(input: Input) {
        long = input.readLong()
    }

    override fun write(output: Output) {
        output.writeLong(long)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return long
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        this.long = value
    }
}
