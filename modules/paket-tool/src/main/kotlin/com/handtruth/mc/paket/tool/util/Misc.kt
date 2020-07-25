package com.handtruth.mc.paket.tool.util

import javafx.scene.Node
import tornadofx.View

inline fun <R> disable(node: Node, block: () -> R): R {
    node.isDisable = true
    try {
        return block()
    } finally {
        node.isDisable = false
    }
}
