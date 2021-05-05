package com.handtruth.mc.collections.test.concurrent

import com.handtruth.mc.collections.CopyOnWriteMap
import org.jetbrains.kotlinx.lincheck.annotations.OpGroupConfig
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.jetbrains.kotlinx.lincheck.verifier.linearizability.LinearizabilityVerifier

@StressCTest(verifier = LinearizabilityVerifier::class)
@OpGroupConfig(name = "producer", nonParallel = true)
class COWMapTest : VerifierState() {
    private val map = CopyOnWriteMap<Int, String>()

    object NullObject

    @Operation(group = "producer")
    fun put(key: Int, value: String) = map.put(key, value) ?: NullObject

    fun get(key: Int) = map[key] ?: NullObject

    override fun extractState(): Any {
        return map
    }
}
