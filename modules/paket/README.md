MC:Tools Paket
========================

This library defines base for communication protocol. Module [client] uses it
in Minecraft protocol implementation. Currently, compression is not supported.
See also [C++] version.

Usage
------------------------

You can use this library to define your own communication protocol.

### Add Library as Dependency

#### Gradle

In Gradle, you can add dependency this way.

```kotlin
repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:tools-paket:$toolsVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:tools-paket-jvm:$toolsVersion")
}
```

### Packet Declaration

You need to define your packets for protocol. For example, let's create
packet class with string and integer list fields.

```kotlin
enum class ExampleID {
    One, Two, Three, NoOp
}

class ExamplePaket(str: String = "", list: List<Int> = emptyList()) : Paket() {
    override val id = ExampleID.One

    val str by string(str)
    val list by listOfVarInt(list)
}
```

### Packet Transmission

If you want to transfer this packet you need `PaketTransmitter`
implementation. There are some implementations for [ktor] channel,
[korio] async stream and [kotlinx.io] `Input` / `Output`.

Neither [ktor] nor [korio] are dependencies of this library, so you need
to declare them yourself in Maven or Gradle.

Example:

```kotlin
val ts = PaketTransmitter(input, output)

val paketA = ExamplePaket("example", listOf(1, 2, 3))
ts.send(paketA) // Send packet to output
if (ts.catchAs<ExampleID>() == ExampleID.One) { // Check packet ID
    val paketB = ExamplePaket()
    ts.receive(paketB) // Receive packet from input
}
```

### Packet Transmitter Transformations

### Paket Filter

Sometimes you don't want to receive some packets. For such situations there are
`PaketReceiver::filter` and `PaketTransmitter::filter` functions. These
functions take a filter as the argument and returns a new `PaketReceiver` or
`PaketTransmitter` object with applied filter.

```kotlin
val transmitter = PaketTransmitter(channel)
val ts = transmitter filter { it.getId<ExampeID>() != ExampleID.NoOp }
```

### Paket Broadcast

**WARNING:** This is an experimental feature.

Sometimes you need to use a PaketTransmitter in different execution contexts.
You can easily make PaketSender fully concurrent with
`PaketSender::asSynchronized()` extension function. This is impossible to do
with `PaketReceiver`. This library has several solutions for this problem.
One of them is broadcast paket receiver.

There is one problem with this solution that need to be told before any
example. Currently, `PaketReceiver` broadcasting is not fully concurrent, but
you can still use it one thread. Kotlin coroutines may help you to organize
your code, so the broadcasting will look like it is concurrent.

```kotlin
// single thread coroutine dispatcher
val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

withContext(dispatcher) {
    val broadcast = PaketTransmitter(channel).broadcast()
    val one = async {
        broadcast.openSubscription().use { ts ->
            ts.send(ExamplePaket("kotlinbergh"))
            ts.receive<OtherPaket>()
        }
    }
    val two = async {
        broadcast.openSubscription().use { ts ->
            ts.receive<OtherPaket>()
        }
    }
    // should be same
    assert(one.await() == two.await())
    broadcast.close()
}
```

Despite broadcast limitations you may find it useful because of dynamic
subscription mechanic. You can use it with the packet nesting, that will be
described later.

If one of the subscribers is not used anymore, you should close it. This is
important because broadcast receiver will catch next packet from parent
receiver only when all subscribers receive a previous packet.

### Paket Router

This is not experimental, fully concurrent and preferable alternative
to Paket Broadcast. This mechanic will deliver a packet to specific
`PaketReceiver` depends on custom router logic.

There is one problem with Paket Router. You can specify a number of
`PaketReceiver` objects only once. You can't dynamically extend it later.

Router logic is a function that returns an id of `PaketReceiver` where packet
should go. If this function will return -1 then a packet will be dropped. This
behavior make `PaketRouter` to be a composition of a number of filters and
broadcast packet receiver.

```kotlin
val parent = PaketTransmitter(channel)
val (ts1, ts2) = parent.split(2) {
    when (it.peek<ExamplePaket>().str) {
        "one" -> 0
        "two" -> 1
        else -> -1
    }
}

coroutineScope {
    launch {
        ts1.use {
            val packet: ExamplePaket = ts1.receive()
            assert(packet.str == "one")
        }
    }
    launch {
        ts2.use {
            val packet: ExamplePaket = ts2.receive()
            assert(packet.str == "two")
        }
    }
}
parent.close()
```

If one of the receivers will be closed, then packets that should be delivered
to them will be dropped. You should close receivers if you do not want to use
them anymore. If you will not do so, then all other packet receivers will
stuck forever waiting the pending packet to be consumed by its receiver.

Paket Router is a fully concurrent solution. You can use it concurrently in
different threads and coroutines. You can also use Paket Router as
synchronization primitive. Consider the following example.

```kotlin
class Session(
    val channel: ?,
    override val coroutineContext:  CoroutineContext
) : CoroutineScope {
    @Volatile private var _list: List<Int> = emptyList()
    val list get() = _list

    init {
        launch(Dispatchers.Default) {
            work()
        }
    }
    
    suspend fun work() {
        PaketTransmitter(channel).use { ts ->
            val (ts1, ts2) = ts.split(2) { ... }
            coroutineScope {
                launch { ts1Work(ts1) }
                launch { ts2Work(ts2) }
            }
        }
    }

    suspend fun ts1Work(ts: PaketTransmitter): Nothing {
        ts.replyAll {
            val paket: ExamplePaket = peek()
            _list = _list + paket.list
            return@reply ExamplePaket("respond")
        }
    }

    suspend fun ts2Work(ts: PaketTransmitter): Nothing {
        ts.receiveAll {
            val paket: ExamplePaket = peek()
            _list = _list + paket.list
        }
    }
}
```

There is Copy On Write list in example above. All values that are added to the
list will be actually added without any loss. That is because addition is
performed with synchronization by Paket Router.

There are some higher order functions for these situations:
`PaketReceiver::receive` and `PaketTransmitter::reply`. The first one will
catch a packet do stuff in lambda and then drop it. Lambda is called with
`PaketPeeking` object as the receiver argument. You can read a packet with it.

`PaketTransmitter::reply` will do exactly the same thing but will also send a
response before dropping a packet.

### Protocol Extension or Paket Nesting

Each protocol that uses this library is limited by `Enum` object. It can be
extended with protocol versions, but it cannot be extended by dynamic modules.

The solution is to place a packet to the packet. For that purpose you can use
`NestPaketTransmitter` and `PaketTransmitter::nest` operator.

Firstly, you need to describe packet header, then implement a
`PacketNestSource` and finally create a new `NestPaketTransmitter`.

`NestPaketTransmitter` is a regular `PaketTransmitter` but all its
functionality is bound to nesting protocol. For example,
`NestPaketTransmitter::idOrdinal` will return inner packet ID.

```kotlin
open class HeadPaket : Paket() {
    override val id = ExampleID.Second

    val seq by varInt(counter++)

    companion object {
        private var counter = 0
    }
}

class BodyPaket(body: Paket) : HeadPaket() {
    val body by paket(body)
}

object MyNest : NestSource<HeadPaket> {
    override fun head() = HeadPaket()
    override fun produce(paket: Paket) = BodyPaket(paket)
}

enum class IDS {
    First, Second, Third
}

object FirstPaket : SinglePaket<FirstPaket>() {
    override val id = IDS.First
}

object SecondPaket : SinglePaket<SecondPaket>() {
    override val id = IDS.Second

    val str by string("kotlinbergh")
}

object ThirdPaket : SinglePaket<ThirdPaket>() {
    override val id = IDS2.Third
}

val main = PaketTransmitter(channel)
val (other, nesting) = main.split(2) {
    when (it.getId<ExampleID>()) {
        ExampleID.Second -> 1
        else -> 0
    }
}
val ts = nesting nest MyNest
coroutineScope {
    launch { other.use { ... } }
    launch {
        ts.use {
            // nesting PaketTransmitter acts like regular for nesting protocol
            when (ts.catchAs<IDS>()) {
                IDS.First -> ts.receive(FirstPaket)
                IDS.Second -> {
                    assertEquals(SecondPaket.size, ts.size)
                    ts.receive(SecondPaket)
                    assertEquals("kotlinbergh", SecondPaket.str)
                }
                IDS.Third -> ts.receive {
                    ts.peek(ThirdPaket)
                }
            }
        }
    }
}
```

Known Issues
-------------------------------

1. This library can be slow. That's because I use some workarounds for
   bugs in [kotlinx.io] builds. We will resolve this issue when
   [kotlinx.io] will be updated.

[ktor]: https://ktor.io
[korio]: https://korlibs.soywiz.com/korio/
[kotlinx.io]: https://github.com/Kotlin/kotlinx-io
[client]: /modules/client/README.md
[C++]: https://github.com/handtruth/paket-cpp
