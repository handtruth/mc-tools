package com.handtruth.mc.paket.tool

import java.util.*
import kotlin.reflect.KProperty

object Assembly {
    private val properties = Properties()

    init {
        val resource = javaClass.classLoader.getResource("META-INF/paket.properties")
        properties.load(resource!!.openStream()!!)
    }

    private operator fun getValue(thisRef: Any, property: KProperty<*>) =
        properties[property.name] as String

    val group by this
    val name by this
    val version by this

    val coordinates by lazy {
        "$group:$name:$version"
    }
}
