package com.handtruth.mc.types.test

import com.handtruth.mc.types.*
import kotlinx.collections.immutable.PersistentList
import kotlin.test.*

class DynamicTest {

    @Test
    fun dynamicTest() {
        val actual = buildMutableDynamic {
            "lol" assign true
            "kek" assign null
            "integer" assign 23
            "inner" {
                "some" list {
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
        println(actual.contentDeepToString(true))
        with(actual) {
            "inner" {
                assertTrue { "some" in this }
                @Suppress("UNCHECKED_CAST")
                var some: PersistentList<Any> by this
                @Suppress("UNUSED_VALUE")
                some = some.add(actual)
            }
        }
        println(actual.contentDeepToString(true))
        actual.fields.asDynamic()
    }

    @Test
    fun persistDynamic() {
        val expected = buildDynamic {
            "level1" {
                "level2" {
                    "something" assign "hello"
                }
                "something" short 1337
            }
            "int" int 23
        }
        val expectedHash = expected.contentDeepHashCode()
        val actual = expected {
            "level1" {
                "level2" {
                    "other" assign "world"
                }
            }
        }
        assertEquals(expectedHash, expected.contentDeepHashCode())
        assertNotEquals(expectedHash, actual.contentDeepHashCode())
        assertFalse { expected contentDeepEquals actual }
        val level2: Dynamic by actual["level1"] as Dynamic
        assertEquals("hello", level2["something"])
        assertEquals("world", level2["other"])
    }
}
