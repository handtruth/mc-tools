plugins {
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("io.ktor.utils.io.core.ExperimentalIoApi")
        }
    }
}

dependencies {
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"

    commonMainImplementation(project(":tools-zint"))
    commonMainImplementation(project(":tools-types"))
    commonMainImplementation("io.ktor:ktor-io")
    commonMainApi(kotlinx("serialization-core"))
    commonMainApi(kotlinx("datetime"))
}
