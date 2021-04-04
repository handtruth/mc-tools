package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.SZLongCodec
import com.handtruth.mc.util.measureSZLong
import com.handtruth.mc.util.readSZLong
import com.handtruth.mc.util.writeSZLong
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class SZLongField(public var long: Long = 0L) : AbstractField<Long>() {
    override val size: Int get() = measureSZLong(long)

    override var value: Long by this::long

    override val codec: SZLongCodec get() = SZLongCodec

    override fun read(input: Input) {
        long = input.readSZLong()
    }

    override fun write(output: Output) {
        output.writeSZLong(long)
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
