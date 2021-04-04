package com.handtruth.mc.paket.transmitter

import com.handtruth.mc.paket.Paket

public interface Inserter {
    public val collected: Int

    public fun insert(paket: Paket)
}
