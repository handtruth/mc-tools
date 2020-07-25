package com.handtruth.mc.paket.tool.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import tornadofx.View
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewContext(
    private val parent: CoroutineContext
) : ReadOnlyProperty<View, CoroutineContext>, CoroutineView.LifecycleObject {

    private lateinit var context: CoroutineContext

    override fun onDock() {
        context = parent + Job(parent[Job])
    }

    override fun onUndock() {
        context.cancel()
    }

    override fun getValue(thisRef: View, property: KProperty<*>) = context

}
