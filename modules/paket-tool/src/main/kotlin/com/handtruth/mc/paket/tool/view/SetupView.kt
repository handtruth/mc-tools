package com.handtruth.mc.paket.tool.view

import com.handtruth.mc.paket.ExperimentalPaketApi
import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.tool.model.SetupViewModel
import com.handtruth.mc.paket.tool.util.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.ClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketRawSession
import io.ktor.client.request.url
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.network.tls.tls
import io.ktor.util.KtorExperimentalAPI
import javafx.geometry.Pos
import javafx.scene.control.ButtonBar
import javafx.scene.control.Label
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Bytes
import tornadofx.*
import java.net.InetSocketAddress
import java.net.URI

class SetupView : CoroutineView() {

    private val viewModel: SetupViewModel by inject()
    private var errorText by singleAssign<Label>()

    override val root = vbox {
        minWidth = 300.0
        label("Paket Tool") {
            maxWidth = Double.MAX_VALUE
            alignment = Pos.CENTER
            style {
                fontSize = 21.pt
            }
        }
        separator()
        form {
            fieldset {
                field("Source") {
                    textfield(viewModel.url) {
                        validator { validate(it) }
                    }
                }
            }
        }
        errorText = label {
            style {
                textFill = Color.RED
            }
        }
        separator()
        buttonbar {
            button("Proceed", ButtonBar.ButtonData.NEXT_FORWARD) {
                enableWhen(viewModel.valid)
                isDefaultButton = true
                action {
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        disable(this@buttonbar) {
                            viewModel.commit()
                            try {
                                connect(viewModel.item.url)
                                withContext(Dispatchers.IO) {
                                    findPakets(viewModel.tree)
                                }
                                find<MainView>().openWindow(escapeClosesWindow = false, owner = null)
                                close()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                errorText.text = e.message
                            }
                        }
                    }
                }
            }
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE) {
                isCancelButton = true
                action {
                    close()
                }
            }
        }
        style {
            padding = box(5.px)
        }
    }

    private suspend fun connect(address: String) {
        val url = URI(address)
        viewModel.ts = when (url.scheme) {
            null -> connectLoopback()
            "tcp" -> connectTCP(url)
            "ws" -> connectWS(address)
            else -> throw UnsupportedOperationException()
        }
    }

    @OptIn(ExperimentalPaketApi::class)
    private fun connectLoopback(): PaketTransmitter {
        return PaketTransmitter(Channel<Bytes>())
    }

    @OptIn(KtorExperimentalAPI::class)
    private suspend fun connectWS(address: String): PaketTransmitter {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }
        val connection: ClientWebSocketSession = client.webSocketRawSession {
            url(address)
        }
        val channel = connection.asChannel()
        val ts = PaketTransmitter(channel.output, channel.input)
        return object : PaketTransmitter by ts {
            override fun close() {
                ts.close()
                channel.close()
            }
        }
    }

    @OptIn(KtorExperimentalAPI::class)
    private suspend fun connectTCP(uri: URI): PaketTransmitter {
        val socket = run {
            val sock = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(uri.host, uri.port))
            if (uri.scheme == "tcps")
                sock.tls(Dispatchers.IO)
            else
                sock
        }
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel()
        val ts = PaketTransmitter(input, output)
        return object : PaketTransmitter by ts {
            override fun close() {
                ts.close()
                socket.close()
            }
        }
    }

    private fun ValidationContext.validate(it: String?): ValidationMessage? {
        return try {
            val url = URI(it!!)
            when (url.scheme) {
                null -> {
                    if (url.schemeSpecificPart == "loopback")
                        null
                    else
                        error("unknown source")
                }
                "tcp", "tcps", "ws", "wss" -> {
                    if (url.port == -1)
                        error("port number should be defined")
                    else
                        null
                }
                else -> error("unknown scheme")
            }
        } catch (e: Exception) {
            error(e.message)
        }
    }

}
