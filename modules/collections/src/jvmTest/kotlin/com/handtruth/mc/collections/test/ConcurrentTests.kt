package com.handtruth.mc.collections.test

import com.handtruth.mc.collections.test.concurrent.COWListTest
import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class ConcurrentTests {
    private val opts = StressOptions()
        .iterations(10)
        .threads(2)
        .logLevel(LoggingLevel.INFO)

    @Test
    fun cowListTest() {
        LinChecker.check(COWListTest::class.java, opts)
    }

    @Test
    fun cowMapTest() {
        // LinChecker.check(COWMapTest::class.java, opts)
    }
}
