package com.handtruth.mc.paket.tool.util

import com.handtruth.mc.paket.tool.PaketToolApp
import javafx.scene.Node
import kotlinx.coroutines.CoroutineScope
import tornadofx.View
import kotlin.coroutines.CoroutineContext

abstract class CoroutineView(title: String? = null, icon: Node? = null) : View(title, icon), CoroutineScope {

    private val lifecycleObjects = mutableListOf<LifecycleObject>()

    interface LifecycleObject {
        fun onBeforeShow() {}
        fun onCreate() {}
        fun onDelete() {}
        fun onSave() {}
        fun onDock() {}
        fun onUndock() {}
    }

    private fun bindScope(): ViewContext {
        val viewContext = ViewContext((app as PaketToolApp).coroutineContext)
        lifecycle(viewContext)
        return viewContext
    }

    fun lifecycle(lifecycleObject: LifecycleObject) {
        lifecycleObjects += lifecycleObject
    }

    override val coroutineContext by bindScope()

    override fun onBeforeShow() {
        super.onBeforeShow()
        lifecycleObjects.forEach { it.onBeforeShow() }
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleObjects.forEach { it.onCreate() }
    }

    override fun onDelete() {
        super.onDelete()
        lifecycleObjects.forEach { it.onDelete() }
    }

    override fun onSave() {
        super.onSave()
        lifecycleObjects.forEach { it.onSave() }
    }

    override fun onDock() {
        super.onDock()
        lifecycleObjects.forEach { it.onDock() }
    }

    override fun onUndock() {
        super.onUndock()
        lifecycleObjects.forEach { it.onUndock() }
    }
}
