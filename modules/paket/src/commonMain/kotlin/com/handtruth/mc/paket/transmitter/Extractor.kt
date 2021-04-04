package com.handtruth.mc.paket.transmitter

import com.handtruth.mc.paket.Paket

public interface Extractor {
    public val remaining: Int

    public fun extract(paket: Paket)
}
