package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.SZIntCodec
import com.handtruth.mc.util.measureSZInt
import com.handtruth.mc.util.readSZInt
import com.handtruth.mc.util.writeSZInt
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class SZIntField(public var int: Int = 0) : AbstractField<Int>() {
    override val size: Int get() = measureSZInt(int)

    override var value: Int by this::int

    override val codec: SZIntCodec get() = SZIntCodec

    override fun read(input: Input) {
        int = input.readSZInt()
    }

    override fun write(output: Output) {
        output.writeSZInt(int)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return int
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.int = value
    }
}
