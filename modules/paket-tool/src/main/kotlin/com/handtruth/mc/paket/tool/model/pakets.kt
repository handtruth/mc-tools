package com.handtruth.mc.paket.tool.model

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.fields.*

enum class IDS {
    One, Two, Tree
}

class Sample : Paket() {
    override val id = IDS.One

    val ohh by varInt(23)
    val heh by string("ujgygrzfimo")
}
