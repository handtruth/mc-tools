package com.handtruth.mc.paket.tool.util

import javafx.collections.ModifiableObservableListBase

class MyObservableList<E> : ModifiableObservableListBase<E>() {
    private val list: MutableList<E> = java.util.Collections.synchronizedList(mutableListOf())
    override fun get(index: Int) = list[index]
    override fun doRemove(index: Int) = list.removeAt(index)
    override fun doSet(index: Int, element: E) = list.set(index, element)
    override fun doAdd(index: Int, element: E) = list.add(index, element)
    override val size = list.size
}
