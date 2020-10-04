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
    commonMainImplementation(kotlinx("serialization-json"))
    commonMainImplementation("com.soywiz.korlibs.korio:korio")
    commonMainImplementation("io.ktor:ktor-client-core")

    "jvmMainImplementation"(ktor("client-cio"))
}
