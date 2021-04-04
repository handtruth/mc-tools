package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.codec.struct
import com.handtruth.mc.paket.field.field
import kotlinx.serialization.KSerializer

@PublishedApi
internal class StructPaket<T>(serializer: KSerializer<T>, initial: T) : Paket() {
    var item by field(struct(serializer), initial)
}
