package com.handtruth.mc.collections.test.concurrent

import com.handtruth.mc.collections.CopyOnWriteList
import org.jetbrains.kotlinx.lincheck.annotations.OpGroupConfig
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.jetbrains.kotlinx.lincheck.verifier.linearizability.LinearizabilityVerifier

@StressCTest(verifier = LinearizabilityVerifier::class)
@OpGroupConfig(name = "producer", nonParallel = true)
class COWListTest : VerifierState() {
    private val list = CopyOnWriteList<Int>()

    @Operation(group = "producer")
    fun add(item: Int) = list.add(item)

    @Operation(handleExceptionsAsResult = [IndexOutOfBoundsException::class])
    fun get(index: Int) = list[index]

    override fun extractState(): Any {
        return list
    }
}
