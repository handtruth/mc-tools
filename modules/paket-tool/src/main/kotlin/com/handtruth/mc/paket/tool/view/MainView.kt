package com.handtruth.mc.paket.tool.view

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.tool.model.SetupViewModel
import com.handtruth.mc.paket.tool.util.CoroutineView
import com.handtruth.mc.paket.tool.util.PaketKeeper
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.text.FontWeight
import javafx.util.Callback
import tornadofx.*

class MainView : CoroutineView() {

    private val viewModel: SetupViewModel by inject()

    private val keeper = SimpleObjectProperty<PaketKeeper>(null)
    private val paket: ReadOnlyObjectProperty<Paket?>

    data class FieldInfo(val type: String, var default: Any)

    private val paketFields: ObservableList<FieldInfo>

    init {
        val fields = SimpleListProperty<FieldInfo>()
        paketFields = fields
        val transformation = SimpleObjectProperty<Paket?>(null)
        paket = transformation
        keeper.addListener { _, oldValue, newValue ->
            transformation.value?.let { oldValue.give(it) }
            val paket = newValue?.take()
            transformation.value = paket
            paketFields.clear()
            if (paket != null) {
                val list = paket.fields.map { field ->
                    FieldInfo(field::class.simpleName!!.removeSuffix("Field"), field.value ?: Any())
                }.asObservable()
                fields.value = list
            }
        }
    }

    class PaketInfo(name: String, val paket: Paket, val keeper: PaketKeeper) {
        val nameProperty = SimpleStringProperty(name)
        var name: String by nameProperty
            private set

        override fun toString() = name
    }

    private var paketsInUse: ObservableList<PaketInfo> by singleAssign()
    private val selectedPaket = SimpleObjectProperty<PaketInfo?>(null)

    val dummyStringProperty = SimpleStringProperty("{NULL}")

    override val root = borderpane {
        left = splitpane(Orientation.VERTICAL) {
            scrollpane {
                treeview(viewModel.tree.root) {
                    maxWidth = Double.MAX_VALUE
                    onUserSelect {
                        val paket = it.paket
                        if (paket != null)
                            keeper.value = paket
                    }
                }
            }
            scrollpane {
                vbox {
                    gridpane {
                        row {
                            label("Package:") {
                                style {
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            val packageProperty = keeper.stringBinding { paket ->
                                paket?.type?.let {
                                    it.qualifiedName?.dropLast(it.simpleName?.length?.plus(1) ?: 0)
                                } ?: "{NULL}"
                            }
                            textfield(packageProperty) {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                        row {
                            label("Name:") {
                                style {
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            textfield(keeper.stringBinding { it?.type?.simpleName ?: "{NULL}" }) {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                        row {
                            gridpaneConstraints {
                                marginTop = 5.0
                            }
                            label("ID:") {
                                style {
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            val idBinding = paket.stringBinding { paket ->
                                paket?.let { "${it.id} (#${it.id.ordinal})" } ?: "{NULL}"
                            }
                            textfield(idBinding) {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                    }
                    separator()
                    label("Fields") {
                        style {
                            fontSize = 16.pt
                        }
                    }
                    tableview(paketFields) {
                        readonlyColumn("Type", FieldInfo::type)
                        readonlyColumn("Default Value", FieldInfo::default)
                        maxHeight = 200.0
                    }
                    separator()
                    button("Add") {
                        action {
                            val keeper = keeper.value ?: return@action
                            val paket = keeper.take()
                            paketsInUse.add(PaketInfo(paket::class.simpleName ?: "paket", paket, keeper))
                        }
                    }
                }
                hiddenWhen(keeper.isNull)
            }
        }
        right = splitpane(Orientation.VERTICAL) {
            listview<PaketInfo> {
                paketsInUse = items
                selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    selectedPaket.value = newValue
                }
            }
            scrollpane {
                vbox {
                    gridpane {
                        row {
                            label("Name:") {
                                style {
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            textfield(selectedPaket.select<PaketInfo?, String> { it?.nameProperty ?: dummyStringProperty }) {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                        row {
                            gridpaneConstraints {
                                marginTop = 5.0
                            }
                            label("ID:") {
                                style {
                                    fontWeight = FontWeight.BOLD
                                }
                            }
                            val idBinding = selectedPaket.stringBinding { paket ->
                                paket?.let { "${it.paket.id} (#${it.paket.id.ordinal})" } ?: "{NULL}"
                            }
                            textfield(idBinding) {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                    }
                    separator()
                    label("Fields") {
                        style {
                            fontSize = 16.pt
                        }
                    }
                    tableview<FieldInfo> {
                        selectedPaket.onChange { info ->
                            info?.let {
                                items.clear()
                                it.paket.fields.mapTo(items) { f ->
                                    FieldInfo(f::class.simpleName.orEmpty(), f.value ?: Any())
                                }
                            }
                        }
                        readonlyColumn("Type", FieldInfo::type)
                        column("Value", FieldInfo::default) {
                            cellFactory = Callback { FieldCell(it) }
                        }
                        maxHeight = 200.0
                    }
                }
                hiddenWhen(selectedPaket.isNull)
            }
        }
    }

    class FieldCell(column: TableColumn<FieldInfo, Any>) : TableCell<FieldInfo, Any>() {
        override fun updateItem(item: Any, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                text = null;
                graphic = null;
            } else {
                when (item) {
                    is Int -> textfield(item.toString())
                    is String -> textfield(item)
                }
            }
        }
    }
}
