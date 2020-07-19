plugins {
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun kommon(name: String) = "com.handtruth.kommon:kommon-$name"

    val atomicfuVersion: String by project

    commonMainApi(kotlinx("io"))
    commonMainApi(kotlinx("coroutines-core-common"))
    commonMainApi(kotlinx("serialization-runtime-common"))
    commonMainCompileOnly(project(":tools-nbt"))
    commonMainCompileOnly("io.ktor:ktor-io")
    commonMainCompileOnly("com.soywiz.korlibs.korio:korio")
    commonMainImplementation("org.jetbrains.kotlinx:atomicfu-common:$atomicfuVersion")

    commonTestImplementation(project(":tools-nbt"))
    commonTestImplementation("io.ktor:ktor-io")
    commonTestImplementation("com.soywiz.korlibs.korio:korio")

    "jvmMainApi"(kotlinx("coroutines-core"))
    "jvmMainApi"(kotlinx("serialization-runtime"))
    "jvmMainImplementation"(kotlin("reflect"))
    "jvmMainImplementation"("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
    "jvmMainCompileOnly"("io.ktor:ktor-io-jvm")

    "jvmTestImplementation"("io.ktor:ktor-io-jvm")
}
