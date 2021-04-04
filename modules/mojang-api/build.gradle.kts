plugins {
    kotlin("plugin.serialization")
}

dependencies {
    fun mc(name: String) = "$group:$name"
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun ktor(name: String) = "io.ktor:ktor-$name"

    commonMainApi(project(":tools-shared"))
    commonMainImplementation(kotlinx("serialization-json"))
    commonMainImplementation("io.ktor:ktor-client-core")

    "jvmTestImplementation"(ktor("client-cio"))
}
