package com.handtruth.mc.paket.tool.model

import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.tool.util.PaketTree
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.singleAssign

class SetupModel {
    val urlProperty = SimpleStringProperty("loopback")
    var url: String by urlProperty
}

class SetupViewModel : ItemViewModel<SetupModel>(SetupModel()) {
    val url = bind(SetupModel::url)

    val tree = PaketTree()
    var ts: PaketTransmitter by singleAssign()
}
