package com.handtruth.mc.paket.tool.util

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketSource
import com.handtruth.mc.paket.tool.model.Sample
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure


class PaketTree {
    private val _root = TreeItem(PaketsImpl(""))
    val root: TreeItem<out Pakets> get() = _root

    interface Pakets {
        val name: String
        val paket: PaketKeeper?
    }

    private class PaketsImpl(override val name: String) : Pakets {
        override var paket: PaketKeeper? = null
        override fun toString() = name
    }

    fun put(keeper: PaketKeeper): TreeItem<out Pakets> {
        val parts = keeper.type.qualifiedName!!.split('.')
        var current = _root
        val iter = parts.iterator()
        while (iter.hasNext()) {
            val part = iter.next()
            val next = current.children.find { it.value.name == part }
            if (next == null) {
                val result = TreeItem(PaketsImpl(part))
                current.children += result
                current = result
            } else {
                current = next
            }
        }
        current.value.paket = keeper
        return current
    }
}

fun findPakets(tree: PaketTree) {
    val reflections = Reflections()
    // find PaketSources
    val sources = reflections.getSubTypesOf(PaketSource::class.java).asSequence().map {
        it!!.kotlin
    }.mapNotNull { source ->
        val parent = source.supertypes.find { it.jvmErasure == PaketSource::class }!!
        val paketType = parent.arguments.first().type ?: return@mapNotNull null
        val sourceInstance = source.objectInstance ?: return@mapNotNull null
        @Suppress("UNCHECKED_CAST")
        (paketType.jvmErasure as KClass<out Paket>) to sourceInstance
    }
    val result: MutableMap<KClass<out Paket>, PaketKeeper> = hashMapOf()
    sources.associateTo(result) { it.first to PaketKeeper(it.second, it.first) }
    // find Pakets
    reflections.getSubTypesOf(Paket::class.java).asSequence().map {
        it.kotlin
    }.filter {
        it !in result
    }.forEach {
        val keeper = PaketKeeper(it)
        try {
            keeper.take()
            result[it] = keeper
        } catch (e: Exception) {

        }
    }
    result.values.forEach {
        println(it.type)
        tree.put(it)
    }
    tree.put(PaketKeeper(Sample::class))
}
