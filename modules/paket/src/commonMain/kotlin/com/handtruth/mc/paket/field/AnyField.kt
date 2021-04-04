package com.handtruth.mc.paket.field

import com.handtruth.mc.paket.codec.Codec

public class AnyField<T>(override val codec: Codec<T>, override var value: T) : AbstractField<T>()
