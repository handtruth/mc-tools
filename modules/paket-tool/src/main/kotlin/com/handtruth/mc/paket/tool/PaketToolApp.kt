package com.handtruth.mc.paket.tool

import com.handtruth.mc.paket.tool.view.SetupView
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*

class PaketToolApp : App(SetupView::class, Styles::class), CoroutineScope {
    override val coroutineContext = SupervisorJob() + CoroutineName("paket-tool") + Dispatchers.JavaFx
}
