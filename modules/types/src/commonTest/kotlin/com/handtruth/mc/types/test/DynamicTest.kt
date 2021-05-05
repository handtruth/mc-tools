package com.handtruth.mc.types.test

import com.handtruth.mc.types.*
import kotlin.test.Test

class DynamicTest {

    @Test
    fun dynamicTest() {
        val actual = buildMutableDynamic {
            "lol" assign true
            "kek" assign null
            "integer" assign 23
            "inner" {
                "some" assign mutableListOf<Any>().apply {
                    add(
                        buildDynamic {
                            "name" assign "ktlo"
                            "option" assign 23.42f
                        }
                    )
                    add(
                        mapOf(
                            "name" to "kaal'",
                            "option" to 14.88f
                        )
                    )
                }
            }
        }
        println(actual.contentToString(true))
        @Suppress("UNCHECKED_CAST")
        ((actual["inner"] as MutableDynamic)["some"] as MutableList<Any>).add(actual)
        println(actual.contentToString(true))
        actual.fields.asDynamic()
    }
}
