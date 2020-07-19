plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun mc(name: String) = "$group:$name"
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun ktor(name: String) = "io.ktor:ktor-$name"

    commonMainApi(project(":tools-chat"))
    commonMainImplementation(project(":tools-paket"))
    commonMainImplementation(kotlinx("io"))
    commonMainImplementation(kotlinx("serialization-runtime-common"))
    commonMainImplementation("com.soywiz.korlibs.korio:korio")
    commonMainImplementation("io.ktor:ktor-client-core")

    "jvmMainImplementation"(kotlinx("serialization-runtime"))
    "jvmMainImplementation"(ktor("io-jvm"))
    "jvmMainImplementation"(ktor("client-core-jvm"))
    "jvmMainImplementation"(ktor("client-cio"))
}
