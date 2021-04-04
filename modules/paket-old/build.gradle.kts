plugins {
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun kommon(name: String) = "com.handtruth.kommon:kommon-$name"

    val atomicfuVersion: String by project

    commonMainApi(kotlinx("coroutines-core"))
    commonMainApi(kotlinx("serialization-core"))
    commonMainApi("io.ktor:ktor-io")
    commonMainCompileOnly("com.soywiz.korlibs.korio:korio")
    commonMainImplementation(project(":tools-zint"))
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")

    commonTestImplementation("com.soywiz.korlibs.korio:korio")

    "jvmMainImplementation"(kotlin("reflect"))

    "jvmTestImplementation"("io.ktor:ktor-network")
}
