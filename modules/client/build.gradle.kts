plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun mc(name: String) = "$group:$name"
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun ktor(name: String) = "io.ktor:ktor-$name"

    commonMainApi(project(":tools-chat"))
    commonMainImplementation(project(":tools-paket"))
    commonMainImplementation(kotlinx("serialization-json"))
    commonMainImplementation("io.ktor:ktor-client-core")
    commonMainImplementation("io.ktor:ktor-network")

    "jvmMainImplementation"(ktor("client-cio"))
}
