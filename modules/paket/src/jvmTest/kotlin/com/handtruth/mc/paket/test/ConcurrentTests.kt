package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.ctest.SynchronizedSenderTest
import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.test.Test

class ConcurrentTests {
    private val opts = StressOptions()
        .iterations(3)
        .threads(4)
        .logLevel(LoggingLevel.INFO)

    @Test
    fun synchronizedSenderTest() {
        LinChecker.check(SynchronizedSenderTest::class.java, opts)
    }
}
