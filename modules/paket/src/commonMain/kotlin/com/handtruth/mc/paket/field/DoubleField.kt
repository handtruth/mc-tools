package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.util.DOUBLE_SIZE
import com.handtruth.mc.paket.codec.DoubleCodec
import io.ktor.utils.io.core.*
import kotlin.reflect.KProperty

public class DoubleField(public var double: Double = .0) : AbstractField<Double>() {
    override val size: Int get() = DOUBLE_SIZE

    override var value: Double by this::double

    override val codec: DoubleCodec get() = DoubleCodec

    override fun read(input: Input) {
        double = input.readDouble()
    }

    override fun write(output: Output) {
        output.writeDouble(double)
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return double
    }

    @Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
    public override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        this.double = value
    }
}
