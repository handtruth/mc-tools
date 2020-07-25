package com.handtruth.mc.paket.tool.model

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.tool.util.PaketKeeper
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import tornadofx.ItemViewModel

data class FieldModel(val type: String, var value: Any?)

//class FieldViewModel : ItemViewModel<FieldModel>(FieldModel())

data class PaketModel(var name: String, val paket: Paket, val keeper: PaketKeeper) {
    override fun toString() = name
}

class PaketViewModel(itemProperty: ObjectProperty<PaketModel>) : ItemViewModel<PaketModel>(itemProperty = itemProperty) {
    val name = bind(PaketModel::name)

    //val fields = bind { this.item?.paket?.fields }
}
