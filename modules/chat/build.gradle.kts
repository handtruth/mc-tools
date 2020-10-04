plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun mc(name: String) = "$group:$name"
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun ktor(name: String) = "io.ktor:ktor-$name"

    commonMainApi(project(":tools-shared"))
    commonMainImplementation(project(":tools-paket"))
    commonMainImplementation(kotlinx("serialization-json"))
}
