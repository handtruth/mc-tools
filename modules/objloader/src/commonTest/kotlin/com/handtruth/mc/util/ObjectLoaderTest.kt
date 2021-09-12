package com.handtruth.mc.util

import kotlin.test.Test
import kotlin.test.assertEquals

interface Service {
    val value: String
}

object MyService : Service {
    override val value = "R'yust"
}

object AnotherService : Service {
    override val value = "Me:gh"
}

object IgnoreService : Service {
    override val value = "not here"
}

class ObjectLoaderTest {
    @Test
    fun loadMyServices() {
        val services = loadObjects<Service>().toSet()
        val expected = setOf(MyService, AnotherService)
        assertEquals(expected, services)
    }
}
